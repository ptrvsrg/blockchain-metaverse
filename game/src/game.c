#include <math.h>
#include <stdlib.h>
#include <time.h>

#include "game.h"
#include "config.h"
#include "db.h"
#include "block.h"
#include "map.h"
#include "matrix.h"
#include "noise.h"
#include "util.h"
#include "renderer.h"
#include "sync_queue.h"
#include "chunk.h"

state_t state;

// Используются в callback'ах, которые вызывает OpenGL, поэтому глобальные
static int exclusive = 1; /**< kinda focus on game window */
static int left_click = 0; /**< состояние нажатия ЛКМ */
static int right_click = 0; /**< состояние нажатия ПКМ */
static int block_type = 1; /**< тип выбранного блока */
static int previous_block_type = 0; /**< тип предыдущего блока */

static void on_key(GLFWwindow *window, int key, int scancode, int action, int mods);
static void on_mouse_button(GLFWwindow *window, int button, int action, int mods);
static void on_scroll(GLFWwindow *window, double xdelta, double ydelta);
static GLuint make_line_buffer(int width, int height);
static void get_sight_vector(float rx, float ry, float *vx, float *vy, float *vz);
static void get_motion_vector(int sz, int sx, float rx, float ry, float *vx, float *vy, float *vz);
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
static Chunk *find_chunk(Chunk *chunks, int chunk_count, int p, int q);
/**
 * @brief Возвращет высоту самого высокого блока по координатам плоскости (x, z)
 * 
 * @param x координата в мире
 * @param y координата в мире
 * 
 * @result возвращает y координату самого высокого блока
*/
static int highest_block(Chunk *chunks, int chunk_count, float x, float z);
static int _hit_test(Map *map, float max_distance, int previous, float x, float y, float z,
                     float vx, float vy, float vz, int *hx, int *hy, int *hz);
static int hit_test(Chunk *chunks, int chunk_count, int previous, float x, float y, float z,
                    float rx, float ry, int *bx, int *by, int *bz);
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
static int collide(Chunk *chunks, int chunk_count, int height, float *x, float *y, float *z);
static int player_intersects_block(int height, float x, float y, float z, int hx, int hy, int hz);
/**
 * @brief генерация мира в чанке с координатами (p, q)
 * 
 * @param map указатель на карту
 * @param p координата чанка
 * @param q координата чанка
*/
static void make_world(Map *map, int p, int q);
static void make_single_cube(GLuint *position_buffer, GLuint *normal_buffer, GLuint *uv_buffer, int w);
static void draw_single_cube(GLuint position_buffer, GLuint normal_buffer, GLuint uv_buffer,
                             GLuint position_loc, GLuint normal_loc, GLuint uv_loc);
static void exposed_faces(Map *map, int x, int y, int z, int *f1, int *f2, int *f3, int *f4, int *f5, int *f6);
/**
 * @brief Обновляет чанк (информацию для рендера)
 * 
 * @param chunck чанк, который надо обновить
*/
static void update_chunk(Chunk *chunk);
/**
 * @attention использует базу данных (генерация и подгрузка из БД)
 * 
 * @brief подгружает чанк по заданным координатам
 * 
 * @param p координата чанка
 * @param q координата чанка
*/
static void make_chunk(Chunk *chunk, int p, int q);
static void draw_lines(GLuint buffer, GLuint position_loc, int size, int count);
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
static void ensure_chunks(Chunk *chunks, int *chunk_count, int p, int q, int force);
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
static void _set_block(Chunk *chunks, int chunk_count, int p, int q, int x, int y, int z, int w);
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
static void set_block(Chunk *chunks, int chunk_count, int x, int y, int z, int w);


static int player_intersects_obstacle(Chunk *chunks, int chunk_count, int height, float x, float y, float z) {
    int result = 0;
    int p = floorf(roundf(x) / CHUNK_SIZE);
    int q = floorf(roundf(z) / CHUNK_SIZE);
    Chunk *chunk = find_chunk(chunks, chunk_count, p, q);
    if (chunk) {
        Map* map = &chunk->map;
        int nx = roundf(x);
        int ny = roundf(y);
        int nz = roundf(z);
        for (int i = 0; i < height; i++) {
            if (is_obstacle(map_get(map, nx, ny - i, nz))) {
                return 1;
            }
        }
        return 0;
    }

    return result;
}

static int render(
    Chunk* chunks, int chunck_count, state_t* state,
    renderer_t* renderer, GLFWwindow* window, int width, int height
);

static void destroy_renderer(renderer_t* renderer);

static int enqueue_block(int p, int q, int x, int y, int z, int w) {
    sync_queue_entry_t entry;
    entry.m_chunk_x = p;
    entry.m_chunk_z = q;
    entry.m_block_x = x;
    entry.m_block_y = y;
    entry.m_block_z = z;
    entry.m_block_id = w;

    return enqueue(&in_blockchain_queue, entry);
}

int run(state_t loaded_state) {
    if (!glfwInit()) {
        return -1;
    }
    // Инициализация окна
    GLFWwindow* window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_TITLE, NULL, NULL);
    if (!window) {
        glfwTerminate();
        return -1;
    }
    glfwMakeContextCurrent(window);
    glfwSwapInterval(VSYNC);
    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    glfwSetKeyCallback(window, on_key);
    glfwSetMouseButtonCallback(window, on_mouse_button);
    glfwSetScrollCallback(window, on_scroll);

    int width;
    int height;
    glfwGetWindowSize(window, &width, &height);

    Chunk chunks[MAX_CHUNKS];
    int chunk_count = 0;

    renderer_t renderer;
    if (-1 == init_renderer(&renderer)) {
        return -1;
    }
    
    if (db_init()) {
        return -1;
    }

    // state_t state;
    // state.x = ((double) rand() / (double) RAND_MAX - 0.5) * 10000;
    // state.z = ((double) rand() / (double) RAND_MAX - 0.5) * 10000;
    // state.y = 0;
    // state.rx = 0;
    // state.ry = 0;
    // int loaded = db_load_state(&state.x, &state.y, &state.z, &state.rx, &state.ry);
    state = loaded_state;
    ensure_chunks(chunks, &chunk_count,
                  floorf(roundf(state.x) / CHUNK_SIZE),
                  floorf(roundf(state.z) / CHUNK_SIZE), 1);
    if (player_intersects_obstacle(chunks, chunk_count, 2, state.x, state.y, state.z)) {
        state.y = highest_block(chunks, chunk_count, state.x, state.z) + 2;
    }

    float dy = 0;
    double px = 0;
    double py = 0;
    glfwGetCursorPos(window, &px, &py);
    double previous = glfwGetTime();
    while (!glfwWindowShouldClose(window)) {
        double now = glfwGetTime();
        double dt = MIN(now - previous, 0.2);
        previous = now;

        if (exclusive && (px || py)) {
            double mx, my;
            glfwGetCursorPos(window, &mx, &my);
            float m = 0.0025;
            state.rx += (mx - px) * m;
            state.ry -= (my - py) * m;
            if (state.rx < 0) {
                state.rx += RADIANS(360);
            }
            if (state.rx >= RADIANS(360)) {
                state.rx -= RADIANS(360);
            }
            state.ry = MAX(state.ry, -RADIANS(90));
            state.ry = MIN(state.ry, RADIANS(90));
            px = mx;
            py = my;
        } else {
            glfwGetCursorPos(window, &px, &py);
        }

        if (left_click) {
            left_click = 0;
            int hx, hy, hz;
            int hw = hit_test(chunks, chunk_count, 0, state.x, state.y, state.z, state.rx, state.ry,
                              &hx, &hy, &hz);
            if (hy > 0 && is_destructable(hw)) {
                set_block(chunks, chunk_count, hx, hy, hz, 0);
                int p = floorf((float) hx / CHUNK_SIZE);
                int q = floorf((float) hz / CHUNK_SIZE);
                enqueue_block(p, q, hx, hy, hz, BLOCK_EMPTY);
            }
        }

        if (right_click) {
            right_click = 0;
            int hx, hy, hz;
            int hw = hit_test(chunks, chunk_count, 1, state.x, state.y, state.z, state.rx, state.ry,
                              &hx, &hy, &hz);
            if (is_obstacle(hw)) {
                if (!player_intersects_block(2, state.x, state.y, state.z, hx, hy, hz)) {
                    set_block(chunks, chunk_count, hx, hy, hz, block_type);
                    int p = floorf((float) hx / CHUNK_SIZE);
                    int q = floorf((float) hz / CHUNK_SIZE);
                    enqueue_block(p, q, hx, hy, hz, block_type);
                }
            }
        }

        sync_queue_entry_t entry;
        if (try_dequeue(&out_blockchain_queue, &entry) != QUEUE_FAILURE) {
            set_block(chunks, chunk_count,
                entry.m_block_x, entry.m_block_y, entry.m_block_z, entry.m_block_id
            );
            if (player_intersects_block(2, state.x, state.y, state.z,
                entry.m_block_x, entry.m_block_y, entry.m_block_z)
                && is_obstacle(entry.m_block_id)
            ) {
                state.y = highest_block(chunks, chunk_count, state.x, state.z) + 2;
            }
        }

        int sz = 0;
        int sx = 0;
        int ortho = glfwGetKey(window, 'F');
        int fov = glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) ? 15.0 : 65.0;
        if (glfwGetKey(window, 'Q')) break;
        if (glfwGetKey(window, 'W')) sz--;
        if (glfwGetKey(window, 'S')) sz++;
        if (glfwGetKey(window, 'A')) sx--;
        if (glfwGetKey(window, 'D')) sx++;
        if (dy == 0 && glfwGetKey(window, GLFW_KEY_SPACE)) {
            dy = 8;
        }
        float vx, vy, vz;
        get_motion_vector(sz, sx, state.rx, state.ry, &vx, &vy, &vz);
        if (glfwGetKey(window, 'Z')) {
            vx = -1;
            vy = 0;
            vz = 0;
        }
        if (glfwGetKey(window, 'X')) {
            vx = 1;
            vy = 0;
            vz = 0;
        }
        if (glfwGetKey(window, 'C')) {
            vx = 0;
            vy = -1;
            vz = 0;
        }
        if (glfwGetKey(window, 'V')) {
            vx = 0;
            vy = 1;
            vz = 0;
        }
        if (glfwGetKey(window, 'B')) {
            vx = 0;
            vy = 0;
            vz = -1;
        }
        if (glfwGetKey(window, 'N')) {
            vx = 0;
            vy = 0;
            vz = 1;
        }
        float speed = 5;
        int step = 8;
        float ut = dt / step;
        vx = vx * ut * speed;
        vy = vy * ut * speed;
        vz = vz * ut * speed;
        for (int i = 0; i < step; i++) {
            dy -= ut * 25;
            dy = MAX(dy, -250);
            state.x += vx;
            state.y += vy + dy * ut;
            state.z += vz;
            if (collide(chunks, chunk_count, 2, &state.x, &state.y, &state.z)) {
                dy = 0;
            }
        }
        glfwPollEvents();

        int p = floorf(roundf(state.x) / CHUNK_SIZE);
        int q = floorf(roundf(state.z) / CHUNK_SIZE);
        ensure_chunks(chunks, &chunk_count, p, q, 0);

        render(chunks, chunk_count, &state, &renderer, window, width, height);

    }
    // db_save_state(state.x, state.y, state.z, state.rx, state.ry);
    db_close();
    destroy_renderer(&renderer);
}

int render(
    Chunk* chunks, int chunk_count, state_t* state,
    renderer_t* renderer, GLFWwindow* window, int width, int height
) {
    int p = floorf(roundf(state->x) / CHUNK_SIZE);
    int q = floorf(roundf(state->z) / CHUNK_SIZE);
    int ortho = glfwGetKey(window, 'F');
    int fov = glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) ? 15.0 : 65.0;
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    matrix_update_3d(
        renderer->matrix, width, height,
        state->x, state->y, state->z, state->rx, state->ry,
        fov, ortho);

    render_chunks(chunks, chunk_count, state, renderer);

    // render focused block wireframe
    int hx, hy, hz;
    int hw = hit_test(
        chunks, chunk_count, 0,
        state->x, state->y, state->z, state->rx, state->ry,
        &hx, &hy, &hz);
    if (is_obstacle(hw)) {
        render_wireframe(renderer, hx, hy, hz);
    }

    matrix_update_2d(renderer->matrix, width, height);

    // render crosshairs
    glUseProgram(renderer->line_program);
    glLineWidth(4);
    glEnable(GL_COLOR_LOGIC_OP);
    glUniformMatrix4fv(renderer->line_matrix_loc, 1, GL_FALSE, renderer->matrix);
    GLuint buffer = make_line_buffer(width, height);
    draw_lines(buffer, renderer->line_position_loc, 2, 4);
    glDeleteBuffers(1, &buffer);
    glDisable(GL_COLOR_LOGIC_OP);

    // render selected item
    matrix_update_item(renderer->matrix, width, height);
    if (block_type != previous_block_type) {
        previous_block_type = block_type;
        make_single_cube(
                &renderer->item_position_buffer, &renderer->item_normal_buffer, &renderer->item_uv_buffer,
                block_type);
    }
    glUseProgram(renderer->block_program);
    glUniformMatrix4fv(renderer->matrix_loc, 1, GL_FALSE, renderer->matrix);
    glUniform3f(renderer->camera_loc, 0, 0, 5);
    glUniform1i(renderer->sampler_loc, 0);
    glUniform1f(renderer->timer_loc, glfwGetTime());
    glDisable(GL_DEPTH_TEST);
    draw_single_cube(
            renderer->item_position_buffer, renderer->item_normal_buffer, renderer->item_uv_buffer,
            renderer->position_loc, renderer->normal_loc, renderer->uv_loc);
    glEnable(GL_DEPTH_TEST);

    glfwSwapBuffers(window);
}

void destroy_renderer(renderer_t* renderer) {
    glfwTerminate();
}



static void on_key(GLFWwindow *window, int key, int scancode, int action, int mods) {
    if (action != GLFW_PRESS) {
        return;
    }
    if (key == GLFW_KEY_ESCAPE) {
        if (exclusive) {
            exclusive = 0;
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }
    }
    if (key == 'E') {
        block_type = block_type % BLOCK_COUNT + 1;
    }
}

static void on_mouse_button(GLFWwindow *window, int button, int action, int mods) {
    if (action != GLFW_PRESS) {
        return;
    }
    if (button == GLFW_MOUSE_BUTTON_LEFT) {
        if (exclusive) {
            left_click = 1;
        } else {
            exclusive = 1;
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        }
    }
    if (button == GLFW_MOUSE_BUTTON_RIGHT) {
        if (exclusive) {
            right_click = 1;
        }
    }
}

static void on_scroll(GLFWwindow *window, double xdelta, double ydelta) {
    static double ypos = 0;
    ypos += ydelta;
    if (ypos < -SCROLL_THRESHOLD) {
        block_type = block_type % BLOCK_COUNT + 1;
        ypos = 0;
    }
    if (ypos > SCROLL_THRESHOLD) {
        block_type = (block_type - 1 + BLOCK_COUNT - 1) % BLOCK_COUNT + 1;
        ypos = 0;
    }
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

static void get_sight_vector(float rx, float ry, float *vx, float *vy, float *vz) {
    float m = cosf(ry);
    *vx = cosf(rx - RADIANS(90)) * m;
    *vy = sinf(ry);
    *vz = sinf(rx - RADIANS(90)) * m;
}

static void get_motion_vector(int sz, int sx, float rx, float ry, float *vx, float *vy, float *vz) {
    *vx = 0;
    *vy = 0;
    *vz = 0;
    if (!sz && !sx) {
        return;
    }
    float strafe = atan2f(sz, sx);
    *vx = cosf(rx + strafe);
    *vy = 0;
    *vz = sinf(rx + strafe);
}

static Chunk *find_chunk(Chunk *chunks, int chunk_count, int p, int q) {
    for (int i = 0; i < chunk_count; i++) {
        Chunk *chunk = chunks + i;
        if (chunk->p == p && chunk->q == q) {
            return chunk;
        }
    }
    return 0;
}

static int highest_block(Chunk *chunks, int chunk_count, float x, float z) {
    int result = -1;
    int nx = roundf(x);
    int nz = roundf(z);
    int p = floorf(roundf(x) / CHUNK_SIZE);
    int q = floorf(roundf(z) / CHUNK_SIZE);
    Chunk *chunk = find_chunk(chunks, chunk_count, p, q);
    if (chunk) {
        Map *map = &chunk->map;
        MAP_FOR_EACH(map, e)
            {
                if (is_obstacle(e->w) && e->x == nx && e->z == nz) {
                    result = MAX(result, e->y);
                }
            }END_MAP_FOR_EACH;
    }
    return result;
}

static int _hit_test(Map *map, float max_distance, int previous, float x, float y, float z,
                     float vx, float vy, float vz, int *hx, int *hy, int *hz) {
    int m = 8;
    int px = 0;
    int py = 0;
    int pz = 0;
    for (int i = 0; i < max_distance * m; i++) {
        int nx = roundf(x);
        int ny = roundf(y);
        int nz = roundf(z);
        if (nx != px || ny != py || nz != pz) {
            int hw = map_get(map, nx, ny, nz);
            if (hw > 0) {
                if (previous) {
                    *hx = px;
                    *hy = py;
                    *hz = pz;
                } else {
                    *hx = nx;
                    *hy = ny;
                    *hz = nz;
                }
                return hw;
            }
            px = nx;
            py = ny;
            pz = nz;
        }
        x += vx / m;
        y += vy / m;
        z += vz / m;
    }
    return 0;
}

static int hit_test(Chunk *chunks, int chunk_count, int previous, float x, float y, float z,
                    float rx, float ry, int *bx, int *by, int *bz) {
    int result = 0;
    float best = 0;
    int p = floorf(roundf(x) / CHUNK_SIZE);
    int q = floorf(roundf(z) / CHUNK_SIZE);
    float vx, vy, vz;
    get_sight_vector(rx, ry, &vx, &vy, &vz);
    for (int i = 0; i < chunk_count; i++) {
        Chunk *chunk = chunks + i;
        if (chunk_distance(chunk, p, q) > 1) {
            continue;
        }
        int hx, hy, hz;
        int hw = _hit_test(&chunk->map, 8, previous,
                           x, y, z, vx, vy, vz, &hx, &hy, &hz);
        if (hw > 0) {
            float d = sqrtf(
                    powf(hx - x, 2) + powf(hy - y, 2) + powf(hz - z, 2));
            if (best == 0 || d < best) {
                best = d;
                *bx = hx;
                *by = hy;
                *bz = hz;
                result = hw;
            }
        }
    }
    return result;
}

static int collide(Chunk *chunks, int chunk_count, int height, float *x, float *y, float *z) {
    int result = 0;
    int p = floorf(roundf(*x) / CHUNK_SIZE);
    int q = floorf(roundf(*z) / CHUNK_SIZE);
    Chunk *chunk = find_chunk(chunks, chunk_count, p, q);
    if (!chunk) {
        return result;
    }
    Map *map = &chunk->map;
    int nx = roundf(*x);
    int ny = roundf(*y);
    int nz = roundf(*z);
    float px = *x - nx;
    float py = *y - ny;
    float pz = *z - nz;
    float pad = 0.25;
    for (int dy = 0; dy < height; dy++) {
        if (px < -pad && is_obstacle(map_get(map, nx - 1, ny - dy, nz))) {
            *x = nx - pad;
        }
        if (px > pad && is_obstacle(map_get(map, nx + 1, ny - dy, nz))) {
            *x = nx + pad;
        }
        if (py < -pad && is_obstacle(map_get(map, nx, ny - dy - 1, nz))) {
            *y = ny - pad;
            result = 1;
        }
        if (py > pad && is_obstacle(map_get(map, nx, ny - dy + 1, nz))) {
            *y = ny + pad;
            result = 1;
        }
        if (pz < -pad && is_obstacle(map_get(map, nx, ny - dy, nz - 1))) {
            *z = nz - pad;
        }
        if (pz > pad && is_obstacle(map_get(map, nx, ny - dy, nz + 1))) {
            *z = nz + pad;
        }
    }
    return result;
}

static int player_intersects_block(int height, float x, float y, float z, int hx, int hy, int hz) {
    int nx = roundf(x);
    int ny = roundf(y);
    int nz = roundf(z);
    for (int i = 0; i < height; i++) {
        if (nx == hx && ny - i == hy && nz == hz) {
            return 1;
        }
    }
    return 0;
}

static void make_world(Map *map, int p, int q) {
    int pad = 1;
    for (int dx = -pad; dx < CHUNK_SIZE + pad; dx++) {
        for (int dz = -pad; dz < CHUNK_SIZE + pad; dz++) {
            int x = p * CHUNK_SIZE + dx;
            int z = q * CHUNK_SIZE + dz;
            float f = simplex2(x * 0.01, z * 0.01, 4, 0.5, 2);
            float g = simplex2(-x * 0.01, -z * 0.01, 2, 0.9, 2);
            int mh = g * 32 + 16;
            int h = f * mh;
            int w = 1;
            int t = 12;
            if (h <= t) {
                h = t;
                w = 2;
            }
            if (dx < 0 || dz < 0 || dx >= CHUNK_SIZE || dz >= CHUNK_SIZE) {
                w = -1;
            }
            // sand and grass terrain
            for (int y = 0; y < h; y++) {
                map_set(map, x, y, z, w);
            }
            if (w == 1) {
                // grass
                if (simplex2(-x * 0.1, z * 0.1, 4, 0.8, 2) > 0.6) {
                    map_set(map, x, h, z, 17);
                }
                // flowers
                if (simplex2(x * 0.05, -z * 0.05, 4, 0.8, 2) > 0.7) {
                    int w = 18 + simplex2(x * 0.1, z * 0.1, 4, 0.8, 2) * 7;
                    map_set(map, x, h, z, w);
                }
                // trees
                int ok = 1;
                if (dx - 4 < 0 || dz - 4 < 0 ||
                    dx + 4 >= CHUNK_SIZE || dz + 4 >= CHUNK_SIZE) {
                    ok = 0;
                }
                if (ok && simplex2(x, z, 6, 0.5, 2) > 0.84) {
                    for (int y = h + 3; y < h + 8; y++) {
                        for (int ox = -3; ox <= 3; ox++) {
                            for (int oz = -3; oz <= 3; oz++) {
                                int d = (ox * ox) + (oz * oz) + (y - (h + 4)) * (y - (h + 4));
                                if (d < 11) {
                                    map_set(map, x + ox, y, z + oz, 15);
                                }
                            }
                        }
                    }
                    for (int y = h; y < h + 7; y++) {
                        map_set(map, x, y, z, 5);
                    }
                }
            }
            // clouds
            for (int y = 64; y < 72; y++) {
                if (simplex3(x * 0.01, y * 0.1, z * 0.01, 8, 0.5, 2) > 0.75) {
                    map_set(map, x, y, z, 16);
                }
            }
        }
    }
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

static void exposed_faces(Map *map, int x, int y, int z, int *f1, int *f2, int *f3, int *f4, int *f5, int *f6) {
    *f1 = is_transparent(map_get(map, x - 1, y, z));
    *f2 = is_transparent(map_get(map, x + 1, y, z));
    *f3 = is_transparent(map_get(map, x, y + 1, z));
    *f4 = is_transparent(map_get(map, x, y - 1, z)) && (y > 0);
    *f5 = is_transparent(map_get(map, x, y, z + 1));
    *f6 = is_transparent(map_get(map, x, y, z - 1));
}

static void update_chunk(Chunk *chunk) {
    Map *map = &chunk->map;

    if (chunk->faces) {
        glDeleteBuffers(1, &chunk->position_buffer);
        glDeleteBuffers(1, &chunk->normal_buffer);
        glDeleteBuffers(1, &chunk->uv_buffer);
    }

    int faces = 0;
    MAP_FOR_EACH(map, e)
        {
            if (e->w <= 0) {
                continue;
            }
            int f1, f2, f3, f4, f5, f6;
            exposed_faces(map, e->x, e->y, e->z, &f1, &f2, &f3, &f4, &f5, &f6);
            int total = f1 + f2 + f3 + f4 + f5 + f6;
            if (is_plant(e->w)) {
                total = total ? 4 : 0;
            }
            faces += total;
        }END_MAP_FOR_EACH;

    GLfloat *position_data = malloc(sizeof(GLfloat) * faces * 18);
    GLfloat *normal_data = malloc(sizeof(GLfloat) * faces * 18);
    GLfloat *uv_data = malloc(sizeof(GLfloat) * faces * 12);
    int position_offset = 0;
    int uv_offset = 0;
    MAP_FOR_EACH(map, e)
        {
            if (e->w <= 0) {
                continue;
            }
            int f1, f2, f3, f4, f5, f6;
            exposed_faces(map, e->x, e->y, e->z, &f1, &f2, &f3, &f4, &f5, &f6);
            int total = f1 + f2 + f3 + f4 + f5 + f6;
            if (is_plant(e->w)) {
                total = total ? 4 : 0;
            }
            if (total == 0) {
                continue;
            }
            if (is_plant(e->w)) {
                float rotation = simplex3(e->x, e->y, e->z, 4, 0.5, 2) * 360;
                make_plant(
                        position_data + position_offset,
                        normal_data + position_offset,
                        uv_data + uv_offset,
                        e->x, e->y, e->z, 0.5, e->w, rotation);
            } else {
                make_cube(
                        position_data + position_offset,
                        normal_data + position_offset,
                        uv_data + uv_offset,
                        f1, f2, f3, f4, f5, f6,
                        e->x, e->y, e->z, 0.5, e->w);
            }
            position_offset += total * 18;
            uv_offset += total * 12;
        }END_MAP_FOR_EACH;

    GLuint position_buffer = make_buffer(
            GL_ARRAY_BUFFER,
            sizeof(GLfloat) * faces * 18,
            position_data
    );
    GLuint normal_buffer = make_buffer(
            GL_ARRAY_BUFFER,
            sizeof(GLfloat) * faces * 18,
            normal_data
    );
    GLuint uv_buffer = make_buffer(
            GL_ARRAY_BUFFER,
            sizeof(GLfloat) * faces * 12,
            uv_data
    );
    free(position_data);
    free(normal_data);
    free(uv_data);

    chunk->faces = faces;
    chunk->position_buffer = position_buffer;
    chunk->normal_buffer = normal_buffer;
    chunk->uv_buffer = uv_buffer;
}

static void make_chunk(Chunk *chunk, int p, int q) {
    chunk->p = p;
    chunk->q = q;
    chunk->faces = 0;
    Map *map = &chunk->map;
    map_alloc(map);
    make_world(map, p, q);
    // подгружает изменения карты из БД
    db_update_chunk(map, p, q);
    // обновляет информацию для рендера
    update_chunk(chunk);
}

static void draw_lines(GLuint buffer, GLuint position_loc, int size, int count) {
    glEnableVertexAttribArray(position_loc);
    glBindBuffer(GL_ARRAY_BUFFER, buffer);
    glVertexAttribPointer(position_loc, size, GL_FLOAT, GL_FALSE, 0, 0);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glDrawArrays(GL_LINES, 0, count);
    glDisableVertexAttribArray(position_loc);
}

static void ensure_chunks(Chunk *chunks, int *chunk_count, int p, int q, int force) {
    int count = *chunk_count;
    for (int i = 0; i < count; i++) {
        Chunk *chunk = chunks + i;
        if (chunk_distance(chunk, p, q) >= DELETE_CHUNK_RADIUS) {
            map_free(&chunk->map);
            glDeleteBuffers(1, &chunk->position_buffer);
            glDeleteBuffers(1, &chunk->normal_buffer);
            glDeleteBuffers(1, &chunk->uv_buffer);
            Chunk *other = chunks + (count - 1);
            chunk->map = other->map;
            chunk->p = other->p;
            chunk->q = other->q;
            chunk->faces = other->faces;
            chunk->position_buffer = other->position_buffer;
            chunk->normal_buffer = other->normal_buffer;
            chunk->uv_buffer = other->uv_buffer;
            count--;
        }
    }
    int n = CREATE_CHUNK_RADIUS;
    for (int i = -n; i <= n; i++) {
        for (int j = -n; j <= n; j++) {
            int a = p + i;
            int b = q + j;
            if (!find_chunk(chunks, count, a, b)) {
                make_chunk(chunks + count, a, b);
                count++;
                if (!force) {
                    *chunk_count = count;
                    return;
                }
            }
        }
    }
    *chunk_count = count;
}

static void _set_block(Chunk *chunks, int chunk_count, int p, int q, int x, int y, int z, int w) {
    Chunk *chunk = find_chunk(chunks, chunk_count, p, q);
    if (chunk) {
        Map *map = &chunk->map;
        map_set(map, x, y, z, w);
        update_chunk(chunk);
    }
    db_insert_block(p, q, x, y, z, w);
}

static void set_block(Chunk *chunks, int chunk_count, int x, int y, int z, int w) {
    int p = floorf((float) x / CHUNK_SIZE);
    int q = floorf((float) z / CHUNK_SIZE);
    _set_block(chunks, chunk_count, p, q, x, y, z, w);
    w = w ? -1 : 0;
    int p0 = x == p * CHUNK_SIZE;
    int q0 = z == q * CHUNK_SIZE;
    int p1 = x == p * CHUNK_SIZE + CHUNK_SIZE - 1;
    int q1 = z == q * CHUNK_SIZE + CHUNK_SIZE - 1;
    for (int dp = -1; dp <= 1; dp++) {
        for (int dq = -1; dq <= 1; dq++) {
            if (dp == 0 && dq == 0) continue;
            if (dp < 0 && !p0) continue;
            if (dp > 0 && !p1) continue;
            if (dq < 0 && !q0) continue;
            if (dq > 0 && !q1) continue;
            _set_block(chunks, chunk_count, p + dp, q + dq, x, y, z, w);
        }
    }
}
