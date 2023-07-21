#ifndef _game_utils_h_
#define _game_utils_h_

#include "chunk.h"
#include "state.h"

/**
 * @brief Функция, нужная для определения координаты чанка по координате блока.
 * 
 * @param x координата блока
 * @return координата чанка
*/
int chunked(float x);

/**
 * @brief Расстояние между чанком и положением игрока (p, q)
 * 
 * @param chunk чанк
 * @param p координата чанка игрока
 * @param q координата чанка игрока
*/
int chunk_distance(Chunk *chunk, int p, int q);

/**
 * @brief По координатам поворота камеры вычисляет радиус вектор.
 * 
 * @param rx координата поворота камеры rx.
 * @param ry координата поворота камеры ry.
 * @param vx указатель на координату x вычисляемого вектора.
 * @param vy указатель на координату y вычисляемого вектора.
 * @param vz указатель на координату z вычисляемого вектора.
*/
void get_sight_vector(float rx, float ry, float *vx, float *vy, float *vz);

/**
 * @brief Вычисляет вектор движения в плоскости по повороту камеры и плоскому вектору скорости.
 * 
 * @param sx координата скорости sx.
 * @param sy координата скорости sy.
 * @param rx координата поворота камеры rx.
 * @param ry координата поворота камеры ry.
 * @param vx указатель на координату x вычисляемого вектора.
 * @param vy указатель на координату y вычисляемого вектора.
 * @param vz указатель на координату z вычисляемого вектора.
*/
void get_motion_vector(int sz, int sx, float rx, float ry, float *vx, float *vy, float *vz);

/**
 * @brief Ищет чанк в массиве с заданными координтами
 * 
 * @param chunks массив чанков котором надо найти чанк
 * @param chunk_count количество чанков
 * @param p координата чанка
 * @param q координата чанка
 * 
 * @result найденный чанк или NULL, если чанк не был найден
*/
Chunk *find_chunk(Chunk *chunks, int chunk_count, int p, int q);

/**
 * @brief Возвращет высоту самого высокого блока по координатам плоскости (x, z)
 * 
 * @param x координата в мире
 * @param y координата в мире
 * 
 * @result возвращает y координату самого высокого блока
*/
int highest_block(Chunk *chunks, int chunk_count, float x, float z);

/**
 * @brief Находит на карте @c map блок, на который указывает игрок, с координатами (x, y, z)
 * смотярщий в направлении вектора (vx, vy, vz)
 * 
 * @param map указатель на карту
 * @param max_distance максимальное расстояние от игрока до блока, на который он указывает
 * @param previous если 0, то возвращает координаты существующего блока на карте. Если 1,
 * то возвращает координаты, куда блок будет поставлен
 * @param x координата x игрока
 * @param y координата y игрока
 * @param z координата z игрока
 * @param vx координата x вектора, в направлении которого смотрит игрок
 * @param vy координата y вектора, в направлении которого смотрит игрок
 * @param vz координата z вектора, в направлении которого смотрит игрок
 * @param hx указатель на найденную координату x блока
 * @param hy указатель на найденную координату y блока
 * @param hz указатель на найденную координату z блока
 * 
 * @return тип найденного блока
*/
int _hit_test(Map *map, float max_distance, int previous, float x, float y, float z,
                     float vx, float vy, float vz, int *hx, int *hy, int *hz);

/**
 * @brief Находит блок, на который указывает игрок
 * 
 * @param chunks массив чанков
 * @param chunk_count количество чанков в массиве
 * @param previous если 0, то возвращает координаты существующего блока на карте. Если 1,
 * то возвращает координаты, куда блок будет поставлен.
 * @param state  указатель на состояние игрока
 * @param bx указатель на найденную координату x блока
 * @param by указатель на найденную координату y блока
 * @param bz указатель на найденную координату z блока
 * 
 * @return тип найденного блока
*/
int hit_test(Chunk *chunks, int chunk_count, int previous, const state_t* state, int *bx, int *by, int *bz);

/**
 * @brief Проверяет, сталкивается ли игрок с препятствием, и если так,
 * то перемещает игрока соответствующим образом.
 * 
 * @param chunks массив чанков
 * @param chunk_count количество чанков в массиве
 * @param x указатель на x координату игрока
 * @param y указатель на y координату игрока
 * @param  указатель на  координату игрока
 * @return 0, если столкновения не произошло; 1 - иначе.
*/
int collide(Chunk *chunks, int chunk_count, int height, float *x, float *y, float *z);

int player_intersects_block(int height, float x, float y, float z, int hx, int hy, int hz);

/**
 * @brief генерация мира в чанке с координатами (p, q)
 * 
 * @param map указатель на карту
 * @param p координата чанка
 * @param q координата чанка
*/
void make_world(Map *map, int p, int q);

void exposed_faces(Map *map, int x, int y, int z, int *f1, int *f2, int *f3, int *f4, int *f5, int *f6);

/**
 * @brief Обновляет чанк (информацию для рендера)
 * 
 * @param chunck чанк, который надо обновить
*/
void update_chunk(Chunk *chunk);

/**
 * @attention использует базу данных (генерация и подгрузка из БД)
 * 
 * @brief подгружает чанк по заданным координатам
 * 
 * @param p координата чанка
 * @param q координата чанка
*/
void make_chunk(Chunk *chunk, int p, int q);

/**
 * @brief обновляет чанки вокруг игрока, deletes far chuncks and creates new ones
 * 
 * @param chunks массив чанков
 * @param chunk_count количество чанков
 * @param p координата чанка игрока
 * @param q координата чанка игрока
 * @param force если 0, то только удалит ненужные чанки,
 * если 1 - создаст все недостающие чанки в CREATE_CHUNK_RADIUS игрока
 * 
 * @result chunk_count количество чанков
*/
void ensure_chunks(Chunk *chunks, int *chunk_count, int p, int q, int force);

/**
 * @attention Записывает изменение в БД
 * @brief Устанавливает занчение блока в чанке, и обновляет чанк
 * 
 * @param chunks массив чанков
 * @param chunk_count количество чанков в массиве
 * @param p координата чанка
 * @param q координата чанка
 * @param x координата блока
 * @param y координата блока
 * @param z координата блока
 * @param w тип блока
*/
void _set_block(Chunk *chunks, int chunk_count, int p, int q, int x, int y, int z, int w);

/**
 * @brief Set block of type @c w at place (x, y, z)
 * 
 * @param chunks массив чанков
 * @param chunk_count количество чанков в массиве
 * @param x координата блока
 * @param y координата блока
 * @param z координата блока
 * @param w тип блока
*/
void set_block(Chunk *chunks, int chunk_count, int x, int y, int z, int w);

int player_intersects_obstacle(Chunk *chunks, int chunk_count, int height, float x, float y, float z);

#endif // _game_utils_h_
