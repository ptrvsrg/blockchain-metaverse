#ifndef _db_h_
#define _db_h_

#include "map.h"

int db_init();
void db_close();
void db_save_state(float x, float y, float z, float rx, float ry);
int db_load_state(float *x, float *y, float *z, float *rx, float *ry);
void db_insert_block(int p, int q, int x, int y, int z, int w);
void db_update_chunk(Map *map, int p, int q);

#endif
