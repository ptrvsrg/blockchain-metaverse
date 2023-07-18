#ifndef _renderer_h_
#define _renderer_h_

#include <GL/glew.h>
#include "chunk.h"
#include "state.h"

/**
 * @brief Структура, содержащая данные для рендеринга.
 * 
 * @details Можно разбить еще на несколько структур
 */
typedef struct renderer_t_s renderer_t;
struct renderer_t_s {
    // GLFWwindow* window; /**< Окно для рендера. */
    // int width; /**< Ширина окна. */
    // int height;
    // float fov;
    // int ortho;
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

int init_renderer(renderer_t* renderer);

void render_chunks(Chunk* chunks, int chunk_count, state_t* state, renderer_t* renderer);

void render_wireframe(renderer_t* renderer, int hx, int hy, int hz);

#endif // _renderer_h_
