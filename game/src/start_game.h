#ifndef _start_game_h_
#define _start_game_h_

#include "sync_queue.h"

/**
 * @brief Очередь в которую пишутся изменения, которые будут переданы в Neo
*/
extern sync_queue_t in_blockchain_queue;

/**
 * @brief Очередь в которой появляются изменения присланные из Neo
 */
extern sync_queue_t out_blockchain_queue;

/**
 * @brief Инициализирует очереди, запускает поток, прослушивающий @c out_blockchain_queue
 * @attention Не возвращает управление до завершения игры
 */
void start_game(void);

#endif