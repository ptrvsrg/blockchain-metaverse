#include "db.h"

#include <stdlib.h>

#include "config.h"
#include "sqlite3.h"

static sqlite3 *db;
static sqlite3_stmt *insert_block_stmt;
static sqlite3_stmt *select_block_stmt;
static sqlite3_stmt *update_chunk_stmt;

int db_init() {
    static const char *create_query =
        "create table if not exists state ("
        "   x float not null,"
        "   y float not null,"
        "   z float not null,"
        "   rx float not null,"
        "   ry float not null"
        ");"
        "create table if not exists block ("
        "    p int not null,"
        "    q int not null,"
        "    x int not null,"
        "    y int not null,"
        "    z int not null,"
        "    w int not null"
        ");"
        "create index if not exists block_pq_idx on block(p, q);"
        "create index if not exists block_xyz_idx on block (x, y, z);"
        "create unique index if not exists block_pqxyz_idx on block (p, q, x, y, z);";

    static const char *insert_block_query =
        "insert or replace into block (p, q, x, y, z, w) "
        "values (?, ?, ?, ?, ?, ?);";

    static const char *select_block_query =
        "select w from block where x = ? and y = ? and z = ?;";

    static const char *update_chunk_query =
        "select x, y, z, w from block where p = ? and q = ?;";

    int rc;
    rc = sqlite3_open(DB_NAME, &db);
    if (rc) return rc;
    rc = sqlite3_exec(db, create_query, NULL, NULL, NULL);
    if (rc) return rc;
    rc = sqlite3_prepare_v2(db, insert_block_query, -1, &insert_block_stmt, NULL);
    if (rc) return rc;
    rc = sqlite3_prepare_v2(db, select_block_query, -1, &select_block_stmt, NULL);
    if (rc) return rc;
    rc = sqlite3_prepare_v2(db, update_chunk_query, -1, &update_chunk_stmt, NULL);
    if (rc) return rc;
    return 0;
}

void db_close() {
    sqlite3_finalize(insert_block_stmt);
    sqlite3_finalize(select_block_stmt);
    sqlite3_finalize(update_chunk_stmt);
    sqlite3_close(db);
}

void db_save_state(float x, float y, float z, float rx, float ry) {
    if (!db_enabled) {
        return;
    }
    static const char *query =
        "insert into state (x, y, z, rx, ry) values (?, ?, ?, ?, ?);";
    sqlite3_stmt *stmt;
    sqlite3_exec(db, "delete from state;", NULL, NULL, NULL);
    sqlite3_prepare_v2(db, query, -1, &stmt, NULL);
    sqlite3_bind_double(stmt, 1, x);
    sqlite3_bind_double(stmt, 2, y);
    sqlite3_bind_double(stmt, 3, z);
    sqlite3_bind_double(stmt, 4, rx);
    sqlite3_bind_double(stmt, 5, ry);
    sqlite3_step(stmt);
    sqlite3_finalize(stmt);
}

// Load the player state from the database.
// Arguments:
// - x: pointer to x position to load value into
// - y: pointer to y position to load value into
// - z: pointer to z position to load value into
// - rx: pointer to rotation x to load value into
// - ry: pointer to rotation y to load value into
// Returns:
// - non-zero if the state entry was successfully found and loaded
int db_load_state(float *x, float *y, float *z, float *rx, float *ry) {
    if (!db_enabled) {
        return 0;
    }
    static const char *query =
        "select x, y, z, rx, ry from state;";
    int result = 0;
    sqlite3_stmt *stmt;
    sqlite3_prepare_v2(db, query, -1, &stmt, NULL);
    if (sqlite3_step(stmt) == SQLITE_ROW) {
        *x = sqlite3_column_double(stmt, 0);
        *y = sqlite3_column_double(stmt, 1);
        *z = sqlite3_column_double(stmt, 2);
        *rx = sqlite3_column_double(stmt, 3);
        *ry = sqlite3_column_double(stmt, 4);
        result = 1;
    }
    sqlite3_finalize(stmt);
    return result;
}

// Let one of the workers insert a block into the database.
// Arguments:
// - p: chunk x position
// - q: chunk z position
// - x: block x position
// - y: block y position
// - z: block z position
// - w: block id
void db_insert_block(int p, int q, int x, int y, int z, int w) {
    if (!db_enabled) {
        return;
    }
    mtx_lock(&mtx);
    ring_put_block(&ring, p, q, x, y, z, w);
    cnd_signal(&cnd);
    mtx_unlock(&mtx);
}

// Actually insert a block into the database.
// Arguments:
// - p: chunk x position
// - q: chunk z position
// - x: block x position
// - y: block y position
// - z: block z position
// - w: block id value
void _db_insert_block(int p, int q, int x, int y, int z, int w) {
    sqlite3_reset(insert_block_stmt);
    sqlite3_bind_int(insert_block_stmt, 1, p);
    sqlite3_bind_int(insert_block_stmt, 2, q);
    sqlite3_bind_int(insert_block_stmt, 3, x);
    sqlite3_bind_int(insert_block_stmt, 4, y);
    sqlite3_bind_int(insert_block_stmt, 5, z);
    sqlite3_bind_int(insert_block_stmt, 6, w);
    sqlite3_step(insert_block_stmt);
}

void db_update_chunk(Map *map, int p, int q) {
    sqlite3_reset(update_chunk_stmt);
    sqlite3_bind_int(update_chunk_stmt, 1, p);
    sqlite3_bind_int(update_chunk_stmt, 2, q);
    while (sqlite3_step(update_chunk_stmt) == SQLITE_ROW) {
        int x = sqlite3_column_int(update_chunk_stmt, 0);
        int y = sqlite3_column_int(update_chunk_stmt, 1);
        int z = sqlite3_column_int(update_chunk_stmt, 2);
        int w = sqlite3_column_int(update_chunk_stmt, 3);
        map_set(map, x, y, z, w);
    }
}
