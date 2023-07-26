#ifndef _item_h_
#define _item_h_

/* Идентификаторы блока */

#define BLOCK_EMPTY             0   /* Пустой блок */
#define BLOCK_GRASS             1   /* Блок травы */
#define BLOCK_SAND              2   /* Блок песка */
#define BLOCK_STONE             3   /* Блок камня */
#define BLOCK_BRICK             4   /* Блок кирпича */
#define BLOCK_WOOD              5   /* Блок дерева */
#define BLOCK_CEMENT            6   /* Блок цемента */
#define BLOCK_DIRT              7   /* Блок грязи */
#define BLOCK_PLANK             8   /* Блок деревянной доски */
#define BLOCK_SNOW              9   /* Блок снега */
#define BLOCK_GLASS             10  /* Блок стекла */
#define BLOCK_COBBLE            11  /* Блок булыжника */
#define BLOCK_LEAVES            15  /* Блок листьев */
#define BLOCK_CLOUD             16  /* Блок облака */
#define BLOCK_TALL_GRASS        17  /* Блок высокой травы */
#define BLOCK_YELLOW_FLOWER     18  /* Блок желтого цветка */
#define BLOCK_RED_FLOWER        19  /* Блок красного цветка */
#define BLOCK_PURPLE_FLOWER     20  /* Блок фиолетового цветка */
#define BLOCK_SUN_FLOWER        21  /* Блок подсолнечника */
#define BLOCK_WHITE_FLOWER      22  /* Блок белого цветка */
#define BLOCK_BLUE_FLOWER       23  /* Блок синего цветка */

/**
 * @brief Проверяет, является ли заданный блок растением.
 *
 * @param w Идентификатор блока.
 * @return 1, если блок является растением, 0 в противном случае.
 */
int is_plant(int w);

/**
 * @brief Проверяет, является ли заданный блок препятствием.
 *
 * @param w Идентификатор блока.
 * @return 1, если блок является препятствием, 0 в противном случае.
 */
int is_obstacle(int w);

/**
 * @brief Проверяет, является ли заданный блок разрушаемым.
 *
 * @param w Идентификатор блока.
 * @return 1, если блок является разрушаемым, 0 в противном случае.
 */
int is_destructable(int w);

/**
 * @brief Проверяет, является ли заданный блок прозрачным.
 *
 * @param w Идентификатор блока.
 * @return 1, если блок является прозрачным, 0 в противном случае.
 */
int is_transparent(int w);

/**
 * @brief Создает блок растения.
 *
 * @param vertex Массив вершин блока.
 * @param normal Массив нормалей блока.
 * @param texture Массив текстур блока.
 * @param x Координата x блока.
 * @param y Координата y блока.
 * @param z Координата z блока.
 * @param n Размер блока.
 * @param w Идентификатор блока.
 * @param rotation Угол поворота блока.
 */
void
make_plant(float *vertex, float *normal, float *texture, float x, float y, float z, float n, int w, float rotation);

/**
 * @brief Создает блок куба.
 *
 * @param vertex Массив вершин блока.
 * @param normal Массив нормалей блока.
 * @param texture Массив текстур блока.
 * @param left Идентификатор текстуры слева от блока.
 * @param right Идентификатор текстуры справа от блока.
 * @param top Идентификатор текстуры сверху от блока.
 * @param bottom Идентификатор текстуры снизу от блока.
 * @param front Идентификатор текстуры спереди от блока.
 * @param back Идентификатор текстуры сзади от блока.
 * @param x Координата x блока.
 * @param y Координата y блока.
 * @param z Координата z блока.
 * @param n Размер блока.
 * @param w Идентификатор блока.
 */
void
make_cube(float *vertex, float *normal, float *texture, int left, int right, int top, int bottom, int front, int back,
          float x, float y, float z, float n, int w);

/**
 * @brief Создает блок контура куба.
 *
 * @param vertex Массив вершин блока.
 * @param x Координата x блока.
 * @param y Координата y блока.
 * @param z Координата z блока.
 * @param n Размер блока.
 */
void make_cube_wireframe(float *vertex, float x, float y, float z, float n);

#endif
