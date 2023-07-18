#ifndef _state_h_
#define _state_h_

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

#endif // _state_h_
