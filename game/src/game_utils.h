#ifndef _game_utils_h_
#define _game_utils_h_

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

#endif // _game_utils_h_
