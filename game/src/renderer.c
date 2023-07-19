#include <math.h>
#include <stdlib.h>

#include "renderer.h"
#include "util.h"
#include "config.h"
#include "chunk.h"
#include "state.h"
#include "matrix.h"
#include "block.h"
#include "game_utils.h"

static void draw_lines(GLuint buffer, GLuint position_loc, int size, int count);
static void draw_chunk(Chunk *chunk, GLuint position_loc, GLuint normal_loc, GLuint uv_loc);
static void draw_single_cube(GLuint position_buffer, GLuint normal_buffer, GLuint uv_buffer,
                             GLuint position_loc, GLuint normal_loc, GLuint uv_loc);
static int chunk_visible(Chunk *chunk, float *matrix);
static GLuint make_cube_buffer(float x, float y, float z, float n);
static GLuint make_line_buffer(int width, int height);
static void make_single_cube(GLuint *position_buffer, GLuint *normal_buffer, GLuint *uv_buffer, int w);

int init_renderer(renderer_t* renderer, GLFWwindow* window) {
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
    load_png_texture(TEXTURES_DIRECTORY TEXTURE_FILE);

    renderer->block_program = load_GPU_program(
            SHADERS_DIRECTORY BLOCK_VERTEX, SHADERS_DIRECTORY BLOCK_FRAGMENT);
    renderer->matrix_loc = glGetUniformLocation(renderer->block_program, "matrix");
    renderer->camera_loc = glGetUniformLocation(renderer->block_program, "camera");
    renderer->sampler_loc = glGetUniformLocation(renderer->block_program, "sampler");
    renderer->timer_loc = glGetUniformLocation(renderer->block_program, "timer");
    renderer->position_loc = glGetAttribLocation(renderer->block_program, "position");
    renderer->normal_loc = glGetAttribLocation(renderer->block_program, "normal");
    renderer->uv_loc = glGetAttribLocation(renderer->block_program, "uv");

    renderer->line_program = load_GPU_program(
            SHADERS_DIRECTORY LINE_VERTEX, SHADERS_DIRECTORY LINE_FRAGMENT);
    renderer->line_matrix_loc = glGetUniformLocation(renderer->line_program, "matrix");
    renderer->line_position_loc = glGetAttribLocation(renderer->line_program, "position");

    renderer->item_position_buffer = 0;
    renderer->item_normal_buffer = 0;
    renderer->item_uv_buffer = 0;

    glfwGetFramebufferSize(window, &renderer->width, &renderer->height);

    return 0;
}

void render_chunks(renderer_t* renderer, Chunk* chunks, int chunk_count, state_t* state) {
    int p = chunked(state->x);
    int q = chunked(state->z);
    float matrix[16];
    matrix_update_3d(
            matrix, renderer->width, renderer->height,
            state->x, state->y, state->z, state->rx, state->ry,
            renderer->fov, renderer->ortho);
    glUseProgram(renderer->block_program);
    glUniformMatrix4fv(renderer->matrix_loc, 1, GL_FALSE, matrix);
    glUniform3f(renderer->camera_loc, state->x, state->y, state->z);
    glUniform1i(renderer->sampler_loc, 0);
    glUniform1f(renderer->timer_loc, glfwGetTime());
    for (int i = 0; i < chunk_count; i++) {
        Chunk *chunk = chunks + i;
        if (chunk_distance(chunk, p, q) > RENDER_CHUNK_RADIUS) {
            continue;
        }
        if (!chunk_visible(chunk, matrix)) {
            continue;
        }
        draw_chunk(chunk, renderer->position_loc, renderer->normal_loc, renderer->uv_loc);
    }
}

void render_wireframe(renderer_t* renderer, state_t* state, int hx, int hy, int hz) {
    float matrix[16];
    matrix_update_3d(
            matrix, renderer->width, renderer->height,
            state->x, state->y, state->z, state->rx, state->ry,
            renderer->fov, renderer->ortho);
    glUseProgram(renderer->line_program);
    glLineWidth(1);
    glEnable(GL_COLOR_LOGIC_OP);
    glUniformMatrix4fv(renderer->line_matrix_loc, 1, GL_FALSE, matrix);
    GLuint buffer = make_cube_buffer(hx, hy, hz, 0.51);
    draw_lines(buffer, renderer->line_position_loc, 3, 48);
    glDeleteBuffers(1, &buffer);
    glDisable(GL_COLOR_LOGIC_OP);
}

void render_crosshairs(renderer_t* renderer) {
    float matrix[16];
    matrix_update_2d(matrix, renderer->width, renderer->height);
    glUseProgram(renderer->line_program);
    glLineWidth(4);
    glEnable(GL_COLOR_LOGIC_OP);
    glUniformMatrix4fv(renderer->line_matrix_loc, 1, GL_FALSE, matrix);
    GLuint buffer = make_line_buffer(renderer->width, renderer->height);
    draw_lines(buffer, renderer->line_position_loc, 2, 4);
    glDeleteBuffers(1, &buffer);
    glDisable(GL_COLOR_LOGIC_OP);
}

void render_selected_item(renderer_t* renderer, int update_item, int block_type) {
    float matrix[16];
    matrix_update_item(matrix, renderer->width, renderer->height);
    if (update_item) {
        make_single_cube(
                &renderer->item_position_buffer, &renderer->item_normal_buffer, &renderer->item_uv_buffer,
                block_type);
    }
    glUseProgram(renderer->block_program);
    glUniformMatrix4fv(renderer->matrix_loc, 1, GL_FALSE, matrix);
    glUniform3f(renderer->camera_loc, 0, 0, 5);
    glUniform1i(renderer->sampler_loc, 0);
    glUniform1f(renderer->timer_loc, glfwGetTime());
    glDisable(GL_DEPTH_TEST);
    draw_single_cube(
            renderer->item_position_buffer, renderer->item_normal_buffer, renderer->item_uv_buffer,
            renderer->position_loc, renderer->normal_loc, renderer->uv_loc);
    glEnable(GL_DEPTH_TEST);
}

static void draw_chunk(Chunk *chunk, GLuint position_loc, GLuint normal_loc, GLuint uv_loc) {
    glEnableVertexAttribArray(position_loc);
    glEnableVertexAttribArray(normal_loc);
    glEnableVertexAttribArray(uv_loc);
    glBindBuffer(GL_ARRAY_BUFFER, chunk->position_buffer);
    glVertexAttribPointer(position_loc, 3, GL_FLOAT, GL_FALSE, 0, 0);
    glBindBuffer(GL_ARRAY_BUFFER, chunk->normal_buffer);
    glVertexAttribPointer(normal_loc, 3, GL_FLOAT, GL_FALSE, 0, 0);
    glBindBuffer(GL_ARRAY_BUFFER, chunk->uv_buffer);
    glVertexAttribPointer(uv_loc, 2, GL_FLOAT, GL_FALSE, 0, 0);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glDrawArrays(GL_TRIANGLES, 0, chunk->faces * 6);
    glDisableVertexAttribArray(position_loc);
    glDisableVertexAttribArray(normal_loc);
    glDisableVertexAttribArray(uv_loc);
}

static void draw_lines(GLuint buffer, GLuint position_loc, int size, int count) {
    glEnableVertexAttribArray(position_loc);
    glBindBuffer(GL_ARRAY_BUFFER, buffer);
    glVertexAttribPointer(position_loc, size, GL_FLOAT, GL_FALSE, 0, 0);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glDrawArrays(GL_LINES, 0, count);
    glDisableVertexAttribArray(position_loc);
}

static int chunk_visible(Chunk *chunk, float *matrix) {
    for (int dp = 0; dp <= 1; dp++) {
        for (int dq = 0; dq <= 1; dq++) {
            for (int y = 0; y < 128; y += 16) {
                float vec[4] = {
                        (chunk->p + dp) * CHUNK_SIZE - dp,
                        y,
                        (chunk->q + dq) * CHUNK_SIZE - dq,
                        1};
                vector_multiply(vec, matrix, vec);
                if (vec[3] >= 0) {
                    return 1;
                }
            }
        }
    }
    return 0;
}

static GLuint make_cube_buffer(float x, float y, float z, float n) {
    float data[144];
    make_cube_wireframe(data, x, y, z, n);
    GLuint buffer = make_buffer(
            GL_ARRAY_BUFFER, sizeof(data), data
    );
    return buffer;
}

static GLuint make_line_buffer(int width, int height) {
    int x = width / 2;
    int y = height / 2;
    int p = 10;
    float data[] = {
            x, y - p, x, y + p,
            x - p, y, x + p, y
    };
    GLuint buffer = make_buffer(
            GL_ARRAY_BUFFER, sizeof(data), data
    );
    return buffer;
}

static void draw_single_cube(GLuint position_buffer, GLuint normal_buffer, GLuint uv_buffer,
                             GLuint position_loc, GLuint normal_loc, GLuint uv_loc) {
    glEnableVertexAttribArray(position_loc);
    glEnableVertexAttribArray(normal_loc);
    glEnableVertexAttribArray(uv_loc);
    glBindBuffer(GL_ARRAY_BUFFER, position_buffer);
    glVertexAttribPointer(position_loc, 3, GL_FLOAT, GL_FALSE, 0, 0);
    glBindBuffer(GL_ARRAY_BUFFER, normal_buffer);
    glVertexAttribPointer(normal_loc, 3, GL_FLOAT, GL_FALSE, 0, 0);
    glBindBuffer(GL_ARRAY_BUFFER, uv_buffer);
    glVertexAttribPointer(uv_loc, 2, GL_FLOAT, GL_FALSE, 0, 0);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glDrawArrays(GL_TRIANGLES, 0, 6 * 6);
    glDisableVertexAttribArray(position_loc);
    glDisableVertexAttribArray(normal_loc);
    glDisableVertexAttribArray(uv_loc);
}

static void make_single_cube(GLuint *position_buffer, GLuint *normal_buffer, GLuint *uv_buffer, int w) {
    int faces = 6;
    glDeleteBuffers(1, position_buffer);
    glDeleteBuffers(1, normal_buffer);
    glDeleteBuffers(1, uv_buffer);
    GLfloat *position_data = malloc(sizeof(GLfloat) * faces * 18);
    GLfloat *normal_data = malloc(sizeof(GLfloat) * faces * 18);
    GLfloat *uv_data = malloc(sizeof(GLfloat) * faces * 12);
    make_cube(
            position_data,
            normal_data,
            uv_data,
            1, 1, 1, 1, 1, 1,
            0, 0, 0, 0.5, w);
    *position_buffer = make_buffer(
            GL_ARRAY_BUFFER,
            sizeof(GLfloat) * faces * 18,
            position_data
    );
    *normal_buffer = make_buffer(
            GL_ARRAY_BUFFER,
            sizeof(GLfloat) * faces * 18,
            normal_data
    );
    *uv_buffer = make_buffer(
            GL_ARRAY_BUFFER,
            sizeof(GLfloat) * faces * 12,
            uv_data
    );
    free(position_data);
    free(normal_data);
    free(uv_data);
}
