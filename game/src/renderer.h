#ifndef _renderer_h_
#define _renderer_h_

#include <GL/glew.h>
#include <GLFW/glfw3.h>
#include "chunk.h"
#include "state.h"

/**
 * @brief Структура, содержащая данные для рендеринга.
 */
typedef struct renderer_t_s renderer_t;
struct renderer_t_s {
    int width; /**< Ширина окна. */
    int height; /**< Высота окна. */
    float fov; /**< Угол обзора. */
    int ortho;  /**< Условие ортогонального вида. */
    
    GLuint block_program; /**< Идентификатор программы шейдера блоков. */
    GLuint matrix_loc; /**< Идентификатор данных для шейдера блоков. */
    GLuint camera_loc; /**< Идентификатор данных для шейдера блоков. */
    GLuint sampler_loc; /**< Идентификатор данных для шейдера блоков. */
    GLuint timer_loc; /**< Идентификатор данных для шейдера блоков. */
    GLuint position_loc; /**< Идентификатор данных для шейдера блоков. */
    GLuint normal_loc; /**< Идентификатор данных для шейдера блоков. */
    GLuint uv_loc; /**< Идентификатор данных для шейдера блоков. */
    
    GLuint line_program; /**< Идентификатор программы шейдера линий. */
    GLuint line_matrix_loc; /**< Идентификатор данных для шейдера линий. */
    GLuint line_position_loc; /**< Идентификатор данных для шейдера линий. */
    
    GLuint item_position_buffer; /**< Данные для отрисовки выбранного блока. */
    GLuint item_normal_buffer; /**< Данные для отрисовки выбранного блока. */
    GLuint item_uv_buffer; /**< Данные для отрисовки выбранного блока. */
};

/**
 * @brief Инициализирует данные для рендеринга, загуржает текстуры, шейдеры и т.д.
 * 
 * @param renderer указатель на структуру с данными для рендеринга.
 * @param window указатель на окно.
 * 
 * @return -1, если не полуичлось инициализировать библиотеку, 0 - иначе
*/
int init_renderer(renderer_t* renderer, GLFWwindow* window);

/**
 * @brief Рисует чанки.
 * 
 * @param renderer указатель на структуру с данными для рендеринга.
 * @param chunks указатель на массив чанков.
 * @param chunk_count количсетво чанков в массиве.
 * @param state состояние игрока.
*/
void render_chunks(renderer_t* renderer, Chunk* chunks, int chunk_count, state_t* state);

/**
 * @brief Рисует wireframe блока.
 * 
 * @param renderer указатель на структуру с данными для рендеринга.
 * @param state указатель на состояние игрока.
 * @param bx координата x блока.
 * @param by координата y блока.
 * @param bz координата z блока.
*/
void render_wireframe(renderer_t* renderer, state_t* state, int hx, int hy, int hz);

/**
 * @brief Рисует перекрестие.
 * 
 * @param renderer указатель на структуру с данными для рендеринга.
*/
void render_crosshairs(renderer_t* renderer);

/**
 * @brief Рисует выбранный элемент.
 * 
 * @param renderer указатель на структуру с данными для рендеринга.
 * @param update_item если 0, то рисуется тот же блок что и при прошлом вызове, иначе @c block_type.
 * @param block_type тип блока, не учитывается, если @c update_item == 0.
*/
void render_selected_item(renderer_t* renderer, int update_item, int block_type);

#endif // _renderer_h_
