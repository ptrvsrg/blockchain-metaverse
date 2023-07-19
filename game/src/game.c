#include <math.h>
#include <stdlib.h>
#include <time.h>

#include "game.h"
#include "config.h"
#include "db.h"
#include "block.h"
#include "util.h"
#include "renderer.h"
#include "sync_queue.h"
#include "chunk.h"
#include "game_utils.h"

state_t state;

// Используются в callback'ах, которые вызывает OpenGL, поэтому глобальные
static int left_click = 0; /**< состояние нажатия ЛКМ */
static int right_click = 0; /**< состояние нажатия ПКМ */
static int block_type = 1; /**< тип выбранного блока */

static void on_key(GLFWwindow *window, int key, int scancode, int action, int mods);
static void on_mouse_button(GLFWwindow *window, int button, int action, int mods);
static void on_scroll(GLFWwindow *window, double xdelta, double ydelta);

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

static int create_window(GLFWwindow** window) {
    int width = WINDOW_WIDTH;
    int height = WINDOW_HEIGHT;
    GLFWmonitor *monitor = NULL;
    if (FULLSCREEN) {
        int mode_count;
        monitor = glfwGetPrimaryMonitor();
        const GLFWvidmode *modes = glfwGetVideoModes(monitor, &mode_count);
        width = modes[mode_count - 1].width;
        height = modes[mode_count - 1].height;
    }
    *window = glfwCreateWindow(width, height, WINDOW_TITLE, monitor, NULL);
    if (!*window) {
        glfwTerminate();
        return -1;
    }
    glfwMakeContextCurrent(*window);
    glfwSwapInterval(VSYNC);
    glfwSetInputMode(*window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    glfwSetKeyCallback(*window, on_key);
    glfwSetMouseButtonCallback(*window, on_mouse_button);
    glfwSetScrollCallback(*window, on_scroll);
}

int run(state_t loaded_state) {
    if (!glfwInit()) {
        return -1;
    }
    GLFWwindow* window;
    if (-1 == create_window(&window)) {
        return -1;
    }

    renderer_t renderer;
    if (-1 == init_renderer(&renderer, window)) {
        return -1;
    }
    
    if (db_init()) {
        return -1;
    }

    Chunk chunks[MAX_CHUNKS];
    int chunk_count = 0;

    state = loaded_state;
    ensure_chunks(chunks, &chunk_count,
                  chunked(state.x),
                  chunked(state.z), 1);
    if (player_intersects_obstacle(chunks, chunk_count, 2, state.x, state.y, state.z)) {
        state.y = highest_block(chunks, chunk_count, state.x, state.z) + 2;
    }

    int previous_block_type = 0;
    float dy = 0;
    double px = 0;
    double py = 0;
    glfwGetCursorPos(window, &px, &py);
    double previous = glfwGetTime();
    while (!glfwWindowShouldClose(window)) {
        double now = glfwGetTime();
        double dt = MIN(now - previous, 0.2);
        previous = now;
        int exclusive = glfwGetInputMode(window, GLFW_CURSOR) == GLFW_CURSOR_DISABLED;
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
            int hw = hit_test(chunks, chunk_count, 0, &state,
                              &hx, &hy, &hz);
            if (hy > 0 && is_destructable(hw)) {
                set_block(chunks, chunk_count, hx, hy, hz, 0);
                int p = chunked(hx);
                int q = chunked(hz);
                enqueue_block(p, q, hx, hy, hz, BLOCK_EMPTY);
            }
        }

        if (right_click) {
            right_click = 0;
            int hx, hy, hz;
            int hw = hit_test(chunks, chunk_count, 1, &state,
                              &hx, &hy, &hz);
            if (is_obstacle(hw)) {
                if (!player_intersects_block(2, state.x, state.y, state.z, hx, hy, hz)) {
                    set_block(chunks, chunk_count, hx, hy, hz, block_type);
                    int p = chunked(hx);
                    int q = chunked(hz);
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

        int p = chunked(state.x);
        int q = chunked(state.z);
        ensure_chunks(chunks, &chunk_count, p, q, 0);

        renderer.ortho = glfwGetKey(window, 'F');
        renderer.fov = glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) ? 15.0 : 65.0;
        glfwGetFramebufferSize(window, &renderer.width, &renderer.height);
        glViewport(0, 0, renderer.width, renderer.height);
        // Rendering
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        render_chunks(&renderer, chunks, chunk_count, &state);
        // get block that player is pointing to
        int block_x, block_y, block_z;
        int block_w = hit_test(
            chunks, chunk_count, 0,
            &state,
            &block_x, &block_y, &block_z);
        if (is_obstacle(block_w)) {
            render_wireframe(&renderer, &state, block_x, block_y, block_z);
        }
        // updates matrix to draw crosshairs
        render_crosshairs(&renderer);
        // updates matrix to drow selected item
        if (block_type != previous_block_type) {
            previous_block_type = block_type;
            render_selected_item(&renderer, 1, block_type);
        }
        else {
            render_selected_item(&renderer, 0, block_type);
        }
        glfwSwapBuffers(window);
    }
    db_close();
    glfwTerminate();
}


static void on_key(GLFWwindow *window, int key, int scancode, int action, int mods) {
    if (action != GLFW_PRESS) {
        return;
    }
    int exclusive = glfwGetInputMode(window, GLFW_CURSOR) == GLFW_CURSOR_DISABLED;
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
    int exclusive = glfwGetInputMode(window, GLFW_CURSOR) == GLFW_CURSOR_DISABLED;
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
