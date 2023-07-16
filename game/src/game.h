#ifndef _game_h_
#define _game_h_

#include <GL/glew.h>
#include <GLFW/glfw3.h>
#include "map.h"

/**
 * @brief Структура, представляющая позицию игрока в мире.
 */
typedef struct state_t_s state_t;
struct state_t_s {
    float x; /**< Координата x игрока. */
    float y; /**< Координата y игрока. */
    float z; /**< Координата z игрока. */
    float rx; /**< Координата rx поворота камеры игрока. */
    float ry; /**< Координата ry поворота камеры игрока. */
};

/**
 * @brief Структура, содержащая данные для рендеринга.
 * 
 * @details Можно разбить еще на несколько структур
 */
typedef struct renderer_t_s renderer_t;
struct renderer_t_s {
    GLFWwindow* window; /**< Окно для рендера. */
    int width;
    int height;
    GLuint block_program; /**< Индекс для программы шейдера блоков */
    GLuint matrix_loc;
    GLuint camera_loc;
    GLuint sampler_loc;
    GLuint timer_loc;
    GLuint position_loc;
    GLuint normal_loc;
    GLuint uv_loc;
    GLuint line_program;
    GLuint line_matrix_loc;
    GLuint line_position_loc;
    GLuint item_position_buffer;
    GLuint item_normal_buffer;
    GLuint item_uv_buffer;
    float matrix[16];
};

/**
 * @struct Структура представляющая информацию для рендера чанка
*/
typedef struct {
    Map map; /**< карта чанка */
    int p; /**< координата p чанка */
    int q; /**< координата q чанка */
    int faces; /**< информация для рендера */
    GLuint position_buffer; /**< информация для рендера */
    GLuint normal_buffer; /**< информация для рендера */
    GLuint uv_buffer; /**< информация для рендера */
} Chunk;

int init_renderer(renderer_t* renderer);

int render(Chunk* chunks, int chunck_count, state_t* state, renderer_t* renderer);

void destroy_renderer(renderer_t* renderer);

int run(void);

#endif
