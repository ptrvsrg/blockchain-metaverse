#include "game_utils.h"
#include <math.h>
#include <stdlib.h>
#include "util.h"
#include "block.h"
#include "util.h"
#include "db.h"
#include "config.h"
#include "noise.h"

int chunked(float x) {
    return floorf(roundf(x) / CHUNK_SIZE);
}

int chunk_distance(Chunk *chunk, int p, int q) {
    int dp = ABS(chunk->p - p);
    int dq = ABS(chunk->q - q);
    return MAX(dp, dq);
}

int player_intersects_obstacle(Chunk *chunks, int chunk_count, int height, float x, float y, float z) {
    int result = 0;
    int p = chunked(x);
    int q = chunked(z);
    Chunk *chunk = find_chunk(chunks, chunk_count, p, q);
    if (chunk) {
        Map* map = &chunk->map;
        int nx = roundf(x);
        int ny = roundf(y);
        int nz = roundf(z);
        for (int i = 0; i < height; i++) {
            if (is_obstacle(map_get(map, nx, ny - i, nz))) {
                return 1;
            }
        }
        return 0;
    }

    return result;
}

void get_sight_vector(float rx, float ry, float *vx, float *vy, float *vz) {
    float m = cosf(ry);
    *vx = cosf(rx - RADIANS(90)) * m;
    *vy = sinf(ry);
    *vz = sinf(rx - RADIANS(90)) * m;
}

void get_motion_vector(int sz, int sx, float rx, float ry, float *vx, float *vy, float *vz) {
    *vx = 0;
    *vy = 0;
    *vz = 0;
    if (sz == 0 && sx == 0) {
        return;
    }
    float strafe = atan2f(sz, sx);
    *vx = cosf(rx + strafe);
    *vy = 0;
    *vz = sinf(rx + strafe);
}

Chunk *find_chunk(Chunk *chunks, int chunk_count, int p, int q) {
    for (int i = 0; i < chunk_count; i++) {
        Chunk *chunk = chunks + i;
        if (chunk->p == p && chunk->q == q) {
            return chunk;
        }
    }
    return 0;
}

int highest_block(Chunk *chunks, int chunk_count, float x, float z) {
    int result = -1;
    int nx = roundf(x);
    int nz = roundf(z);
    int p = chunked(x);
    int q = chunked(z);
    Chunk *chunk = find_chunk(chunks, chunk_count, p, q);
    if (chunk) {
        Map *map = &chunk->map;
        MAP_FOR_EACH(map, e)
            {
                if (is_obstacle(e->w) && e->x == nx && e->z == nz) {
                    result = MAX(result, e->y);
                }
            }END_MAP_FOR_EACH;
    }
    return result;
}

int _hit_test(Map *map, float max_distance, int previous, float x, float y, float z,
                     float vx, float vy, float vz, int *hx, int *hy, int *hz) {
    int m = 8;
    int px = 0;
    int py = 0;
    int pz = 0;
    for (int i = 0; i < max_distance * m; i++) {
        int nx = roundf(x);
        int ny = roundf(y);
        int nz = roundf(z);
        if (nx != px || ny != py || nz != pz) {
            int hw = map_get(map, nx, ny, nz);
            if (hw > 0) {
                if (previous) {
                    *hx = px;
                    *hy = py;
                    *hz = pz;
                } else {
                    *hx = nx;
                    *hy = ny;
                    *hz = nz;
                }
                return hw;
            }
            px = nx;
            py = ny;
            pz = nz;
        }
        x += vx / m;
        y += vy / m;
        z += vz / m;
    }
    return 0;
}

int hit_test(Chunk *chunks, int chunk_count, int previous, const state_t* state, int *bx, int *by, int *bz) {
    int result = 0;
    float best = 0;
    int p = chunked(state->x);
    int q = chunked(state->z);
    float vx, vy, vz;
    get_sight_vector(state->rx, state->ry, &vx, &vy, &vz);
    for (int i = 0; i < chunk_count; i++) {
        Chunk *chunk = chunks + i;
        if (chunk_distance(chunk, p, q) > 1) {
            continue;
        }
        int hx, hy, hz;
        int hw = _hit_test(&chunk->map, 8, previous,
                           state->x, state->y, state->z, vx, vy, vz, &hx, &hy, &hz);
        if (hw > 0) {
            float d = sqrtf(
                    powf(hx - state->x, 2) + powf(hy - state->y, 2) + powf(hz - state->z, 2));
            if (best == 0 || d < best) {
                best = d;
                *bx = hx;
                *by = hy;
                *bz = hz;
                result = hw;
            }
        }
    }
    return result;
}

int collide(Chunk *chunks, int chunk_count, int height, float *x, float *y, float *z) {
    int result = 0;
    int p = chunked(*x);
    int q = chunked(*z);
    Chunk *chunk = find_chunk(chunks, chunk_count, p, q);
    if (!chunk) {
        return result;
    }
    Map *map = &chunk->map;
    int nx = roundf(*x);
    int ny = roundf(*y);
    int nz = roundf(*z);
    float px = *x - nx;
    float py = *y - ny;
    float pz = *z - nz;
    float pad = 0.25;
    for (int dy = 0; dy < height; dy++) {
        if (px < -pad && is_obstacle(map_get(map, nx - 1, ny - dy, nz))) {
            *x = nx - pad;
        }
        if (px > pad && is_obstacle(map_get(map, nx + 1, ny - dy, nz))) {
            *x = nx + pad;
        }
        if (py < -pad && is_obstacle(map_get(map, nx, ny - dy - 1, nz))) {
            *y = ny - pad;
            result = 1;
        }
        if (py > pad && is_obstacle(map_get(map, nx, ny - dy + 1, nz))) {
            *y = ny + pad;
            result = 1;
        }
        if (pz < -pad && is_obstacle(map_get(map, nx, ny - dy, nz - 1))) {
            *z = nz - pad;
        }
        if (pz > pad && is_obstacle(map_get(map, nx, ny - dy, nz + 1))) {
            *z = nz + pad;
        }
    }
    return result;
}

int player_intersects_block(int height, float x, float y, float z, int hx, int hy, int hz) {
    int nx = roundf(x);
    int ny = roundf(y);
    int nz = roundf(z);
    for (int i = 0; i < height; i++) {
        if (nx == hx && ny - i == hy && nz == hz) {
            return 1;
        }
    }
    return 0;
}

void make_world(Map *map, int p, int q) {
    int pad = 1;
    for (int dx = -pad; dx < CHUNK_SIZE + pad; dx++) {
        for (int dz = -pad; dz < CHUNK_SIZE + pad; dz++) {
            int x = p * CHUNK_SIZE + dx;
            int z = q * CHUNK_SIZE + dz;
            float f = simplex2(x * 0.01, z * 0.01, 4, 0.5, 2);
            float g = simplex2(-x * 0.01, -z * 0.01, 2, 0.9, 2);
            int mh = g * 32 + 16;
            int h = f * mh;
            int w = 1;
            int t = 12;
            if (h <= t) {
                h = t;
                w = 2;
            }
            if (dx < 0 || dz < 0 || dx >= CHUNK_SIZE || dz >= CHUNK_SIZE) {
                w = -1;
            }
            // sand and grass terrain
            for (int y = 0; y < h; y++) {
                map_set(map, x, y, z, w);
            }
            if (w == 1) {
                // grass
                if (simplex2(-x * 0.1, z * 0.1, 4, 0.8, 2) > 0.6) {
                    map_set(map, x, h, z, 17);
                }
                // flowers
                if (simplex2(x * 0.05, -z * 0.05, 4, 0.8, 2) > 0.7) {
                    int w = 18 + simplex2(x * 0.1, z * 0.1, 4, 0.8, 2) * 7;
                    map_set(map, x, h, z, w);
                }
                // trees
                int ok = 1;
                if (dx - 4 < 0 || dz - 4 < 0 ||
                    dx + 4 >= CHUNK_SIZE || dz + 4 >= CHUNK_SIZE) {
                    ok = 0;
                }
                if (ok && simplex2(x, z, 6, 0.5, 2) > 0.84) {
                    for (int y = h + 3; y < h + 8; y++) {
                        for (int ox = -3; ox <= 3; ox++) {
                            for (int oz = -3; oz <= 3; oz++) {
                                int d = (ox * ox) + (oz * oz) + (y - (h + 4)) * (y - (h + 4));
                                if (d < 11) {
                                    map_set(map, x + ox, y, z + oz, 15);
                                }
                            }
                        }
                    }
                    for (int y = h; y < h + 7; y++) {
                        map_set(map, x, y, z, 5);
                    }
                }
            }
            // clouds
            for (int y = 64; y < 72; y++) {
                if (simplex3(x * 0.01, y * 0.1, z * 0.01, 8, 0.5, 2) > 0.75) {
                    map_set(map, x, y, z, 16);
                }
            }
        }
    }
}

void exposed_faces(Map *map, int x, int y, int z, int *f1, int *f2, int *f3, int *f4, int *f5, int *f6) {
    *f1 = is_transparent(map_get(map, x - 1, y, z));
    *f2 = is_transparent(map_get(map, x + 1, y, z));
    *f3 = is_transparent(map_get(map, x, y + 1, z));
    *f4 = is_transparent(map_get(map, x, y - 1, z)) && (y > 0);
    *f5 = is_transparent(map_get(map, x, y, z + 1));
    *f6 = is_transparent(map_get(map, x, y, z - 1));
}

void update_chunk(Chunk *chunk) {
    Map *map = &chunk->map;

    if (chunk->faces) {
        glDeleteBuffers(1, &chunk->position_buffer);
        glDeleteBuffers(1, &chunk->normal_buffer);
        glDeleteBuffers(1, &chunk->uv_buffer);
    }

    int faces = 0;
    MAP_FOR_EACH(map, e)
        {
            if (e->w <= 0) {
                continue;
            }
            int f1, f2, f3, f4, f5, f6;
            exposed_faces(map, e->x, e->y, e->z, &f1, &f2, &f3, &f4, &f5, &f6);
            int total = f1 + f2 + f3 + f4 + f5 + f6;
            if (is_plant(e->w)) {
                total = total ? 4 : 0;
            }
            faces += total;
        }END_MAP_FOR_EACH;

    GLfloat *position_data = malloc(sizeof(GLfloat) * faces * 18);
    GLfloat *normal_data = malloc(sizeof(GLfloat) * faces * 18);
    GLfloat *uv_data = malloc(sizeof(GLfloat) * faces * 12);
    int position_offset = 0;
    int uv_offset = 0;
    MAP_FOR_EACH(map, e)
        {
            if (e->w <= 0) {
                continue;
            }
            int f1, f2, f3, f4, f5, f6;
            exposed_faces(map, e->x, e->y, e->z, &f1, &f2, &f3, &f4, &f5, &f6);
            int total = f1 + f2 + f3 + f4 + f5 + f6;
            if (is_plant(e->w)) {
                total = total ? 4 : 0;
            }
            if (total == 0) {
                continue;
            }
            if (is_plant(e->w)) {
                float rotation = simplex3(e->x, e->y, e->z, 4, 0.5, 2) * 360;
                make_plant(
                        position_data + position_offset,
                        normal_data + position_offset,
                        uv_data + uv_offset,
                        e->x, e->y, e->z, 0.5, e->w, rotation);
            } else {
                make_cube(
                        position_data + position_offset,
                        normal_data + position_offset,
                        uv_data + uv_offset,
                        f1, f2, f3, f4, f5, f6,
                        e->x, e->y, e->z, 0.5, e->w);
            }
            position_offset += total * 18;
            uv_offset += total * 12;
        }END_MAP_FOR_EACH;

    GLuint position_buffer = make_buffer(
            GL_ARRAY_BUFFER,
            sizeof(GLfloat) * faces * 18,
            position_data
    );
    GLuint normal_buffer = make_buffer(
            GL_ARRAY_BUFFER,
            sizeof(GLfloat) * faces * 18,
            normal_data
    );
    GLuint uv_buffer = make_buffer(
            GL_ARRAY_BUFFER,
            sizeof(GLfloat) * faces * 12,
            uv_data
    );
    free(position_data);
    free(normal_data);
    free(uv_data);

    chunk->faces = faces;
    chunk->position_buffer = position_buffer;
    chunk->normal_buffer = normal_buffer;
    chunk->uv_buffer = uv_buffer;
}

void make_chunk(Chunk *chunk, int p, int q) {
    chunk->p = p;
    chunk->q = q;
    chunk->faces = 0;
    Map *map = &chunk->map;
    map_alloc(map);
    make_world(map, p, q);
    // подгружает изменения карты из БД
    db_update_chunk(map, p, q);
    // обновляет информацию для рендера
    update_chunk(chunk);
}

void ensure_chunks(Chunk *chunks, int *chunk_count, int p, int q, int force) {
    int count = *chunk_count;
    for (int i = 0; i < count; i++) {
        Chunk *chunk = chunks + i;
        if (chunk_distance(chunk, p, q) >= DELETE_CHUNK_RADIUS) {
            map_free(&chunk->map);
            glDeleteBuffers(1, &chunk->position_buffer);
            glDeleteBuffers(1, &chunk->normal_buffer);
            glDeleteBuffers(1, &chunk->uv_buffer);
            Chunk *other = chunks + (count - 1);
            chunk->map = other->map;
            chunk->p = other->p;
            chunk->q = other->q;
            chunk->faces = other->faces;
            chunk->position_buffer = other->position_buffer;
            chunk->normal_buffer = other->normal_buffer;
            chunk->uv_buffer = other->uv_buffer;
            count--;
        }
    }
    int n = CREATE_CHUNK_RADIUS;
    for (int i = -n; i <= n; i++) {
        for (int j = -n; j <= n; j++) {
            int a = p + i;
            int b = q + j;
            if (!find_chunk(chunks, count, a, b)) {
                make_chunk(chunks + count, a, b);
                count++;
                if (!force) {
                    *chunk_count = count;
                    return;
                }
            }
        }
    }
    *chunk_count = count;
}

void _set_block(Chunk *chunks, int chunk_count, int p, int q, int x, int y, int z, int w) {
    Chunk *chunk = find_chunk(chunks, chunk_count, p, q);
    if (chunk) {
        Map *map = &chunk->map;
        map_set(map, x, y, z, w);
        update_chunk(chunk);
    }
    db_insert_block(p, q, x, y, z, w);
}

void set_block(Chunk *chunks, int chunk_count, int x, int y, int z, int w) {
    int p = chunked(x);
    int q = chunked(z);
    _set_block(chunks, chunk_count, p, q, x, y, z, w);
    w = w ? -1 : 0;
    int p0 = x == p * CHUNK_SIZE;
    int q0 = z == q * CHUNK_SIZE;
    int p1 = x == p * CHUNK_SIZE + CHUNK_SIZE - 1;
    int q1 = z == q * CHUNK_SIZE + CHUNK_SIZE - 1;
    for (int dp = -1; dp <= 1; dp++) {
        for (int dq = -1; dq <= 1; dq++) {
            if (dp == 0 && dq == 0) continue;
            if (dp < 0 && !p0) continue;
            if (dp > 0 && !p1) continue;
            if (dq < 0 && !q0) continue;
            if (dq > 0 && !q1) continue;
            _set_block(chunks, chunk_count, p + dp, q + dq, x, y, z, w);
        }
    }
}
