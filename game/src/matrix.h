#ifndef _matrix_h_
#define _matrix_h_

/**
 * @brief Функция для нормализации вектора.
 * @param x Указатель на координату x вектора.
 * @param y Указатель на координату y вектора.
 * @param z Указатель на координату z вектора.
 */
void normalize(float *x, float *y, float *z);

/**
 * @brief Функция для установки единичной матрицы.
 *
 *  (   1   0   0   0   )<br>
 *  (   0   1   0   0   )<br>
 *  (   0   0   1   0   )<br>
 *  (   0   0   0   1   )
 *
 * @param matrix Указатель на матрицу.
 */
void matrix_identity(float *matrix);

/**
 * @brief Функция для трансляции матрицы.
 *
 *  (   1   0   0   0   )<br>
 *  (   0   1   0   0   )<br>
 *  (   0   0   1   0   )<br>
 *  (   dx  dy  dz  1   )
 *
 * @param matrix Указатель на матрицу.
 * @param dx Смещение по оси x.
 * @param dy Смещение по оси y.
 * @param dz Смещение по оси z.
 */
void matrix_translate(float *matrix, float dx, float dy, float dz);

/**
 * @brief Функция для поворота матрицы.
 * @param matrix Указатель на матрицу.
 * @param x Координата x оси вращения.
 * @param y Координата y оси вращения.
 * @param z Координата z оси вращения.
 * @param angle Угол поворота в радианах.
 */
void matrix_rotate(float *matrix, float x, float y, float z, float angle);

/**
 * @brief Функция для умножения вектора на матрицу.
 * @param vector Указатель на вектор.
 * @param a Указатель на матрицу A.
 * @param b Указатель на матрицу B.
 */
void vector_multiply(float *vector, float *a, float *b);

/**
 * @brief Функция для умножения матрицы на матрицу.
 * @param matrix Указатель на матрицу.
 * @param a Указатель на матрицу A.
 * @param b Указатель на матрицу B.
 */
void matrix_multiply(float *matrix, float *a, float *b);

/**
 * @brief Функция для создания матрицы проекции в виде пирамиды.
 * @param matrix Указатель на матрицу.
 * @param left Координата левой границы пирамиды.
 * @param right Координата правой границы пирамиды.
 * @param bottom Координата нижней границы пирамиды.
 * @param top Координата верхней границы пирамиды.
 * @param znear Ближняя плоскость отсечения.
 * @param zfar Дальняя плоскость отсечения.
 */
void matrix_frustum(float *matrix, float left, float right, float bottom, float top, float znear, float zfar);

/**
 * @brief Функция для создания матрицы перспективной проекции.
 * @param matrix Указатель на матрицу.
 * @param fov Угол обзора в градусах.
 * @param aspect Соотношение сторон экрана.
 * @param near Ближняя плоскость отсечения.
 * @param far Дальняя плоскость отсечения.
 */
void matrix_perspective(float *matrix, float fov, float aspect, float near, float far);

/**
 * @brief Функция для создания ортогональной матрицы проекции.
 * @param matrix Указатель на матрицу.
 * @param left Координата левой границы проекции.
 * @param right Координата правой границы проекции.
 * @param bottom Координата нижней границы проекции.
 * @param top Координата верхней границы проекции.
 * @param near Ближняя плоскость отсечения.
 * @param far Дальняя плоскость отсечения.
 */
void matrix_ortho(float *matrix, float left, float right, float bottom, float top, float near, float far);

/**
 * @brief Функция для обновления 2D матрицы проекции.
 * @param matrix Указатель на матрицу.
 * @param width Ширина окна.
 * @param height Высота окна.
 */
void matrix_update_2d(float *matrix, int width, int height);

/**
 * @brief Функция для обновления 3D матрицы проекции.
 * @param matrix Указатель на матрицу.
 * @param width Ширина окна.
 * @param height Высота окна.
 * @param x Координата x камеры.
 * @param y Координата y камеры.
 * @param z Координата z камеры.
 * @param rx Угол поворота по оси x.
 * @param ry Угол поворота по оси y.
 * @param fov Угол обзора.
 * @param ortho Флаг, указывающий, используется ли ортогональная проекция.
 */
void matrix_update_3d(float *matrix, int width, int height, float x, float y, float z,
                      float rx, float ry, float fov, int ortho);

/**
 * @brief Функция для обновления матрицы элемента.
 * @param matrix Указатель на матрицу.
 * @param width Ширина окна.
 * @param height Высота окна.
 */
void matrix_update_item(float *matrix, int width, int height);

#endif
