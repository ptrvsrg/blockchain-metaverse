#include "renderer.h"
#include "util.h"
#include "config.h"

int init_renderer(renderer_t* renderer) {
    if (glewInit() != GLEW_OK) {
        return -1;
    }

    // Инициализация opengl: настройка, текстуры, шейдеры etc.
    glEnable(GL_CULL_FACE);
    glEnable(GL_DEPTH_TEST);
    glEnable(GL_LINE_SMOOTH);
    glLogicOp(GL_INVERT);
    glClearColor(0.53, 0.81, 0.92, 1.00);

    GLuint vertex_array;
    glGenVertexArrays(1, &vertex_array);
    glBindVertexArray(vertex_array);

    GLuint texture;
    glGenTextures(1, &texture);
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, texture);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    load_png_texture("texture/texture.png");

    renderer->block_program = load_GPU_program(
            "shaders/block_vertex.glsl", "shaders/block_fragment.glsl");
    renderer->matrix_loc = glGetUniformLocation(renderer->block_program, "matrix");
    renderer->camera_loc = glGetUniformLocation(renderer->block_program, "camera");
    renderer->sampler_loc = glGetUniformLocation(renderer->block_program, "sampler");
    renderer->timer_loc = glGetUniformLocation(renderer->block_program, "timer");
    renderer->position_loc = glGetAttribLocation(renderer->block_program, "position");
    renderer->normal_loc = glGetAttribLocation(renderer->block_program, "normal");
    renderer->uv_loc = glGetAttribLocation(renderer->block_program, "uv");

    renderer->line_program = load_GPU_program(
            "shaders/line_vertex.glsl", "shaders/line_fragment.glsl");
    renderer->line_matrix_loc = glGetUniformLocation(renderer->line_program, "matrix");
    renderer->line_position_loc = glGetAttribLocation(renderer->line_program, "position");

    renderer->item_position_buffer = 0;
    renderer->item_normal_buffer = 0;
    renderer->item_uv_buffer = 0;
}
