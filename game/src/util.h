#ifndef _util_h_
#define _util_h_

#include <GL/glew.h>
#include <GLFW/glfw3.h>

#define PI 3.14159265359    /* Значение числа Пи */

/**
 * @brief Конвертирует радианы в градусы.
 * @param radians Значение в радианах.
 * @return Значение в градусах.
 */
#define DEGREES(radians) ((radians) * 180 / PI)

/**
 * @brief Конвертирует градусы в радианы.
 * @param degrees Значение в градусах.
 * @return Значение в радианах.
 */
#define RADIANS(degrees) ((degrees) * PI / 180)

/**
 * @brief Возвращает абсолютное значение числа.
 * @param x Число.
 * @return Абсолютное значение числа.
 */
#define ABS(x) ((x) < 0 ? (-(x)) : (x))

/**
 * @brief Возвращает минимальное из двух чисел.
 * @param a Первое число.
 * @param b Второе число.
 * @return Минимальное число.
 */
#define MIN(a, b) ((a) < (b) ? (a) : (b))

/**
 * @brief Возвращает максимальное из двух чисел.
 * @param a Первое число.
 * @param b Второе число.
 * @return Максимальное число.
 */
#define MAX(a, b) ((a) > (b) ? (a) : (b))

/**
 * @brief Создает буфер.
 * @param target Тип буфера.
 * @param size Размер буфера.
 * @param data Данные для заполнения буфера.
 * @return Идентификатор созданного буфера.
 */
GLuint make_buffer(GLenum target, GLsizei size, const void *data);

/**
 * @brief Загружает программу на графическом процессоре.
 * @param vertex_path Путь к файлу вершинного шейдера.
 * @param fragments_path Путь к файлу фрагментного шейдера.
 * @return Идентификатор загруженной программы на графическом процессоре.
 */
GLuint load_GPU_program(const char *vertex_path, const char *fragments_path);

/**
 * @brief Загружает текстуру PNG.
 * @param file_name Имя файла текстуры PNG.
 */
void load_png_texture(const char *file_name);

#endif
