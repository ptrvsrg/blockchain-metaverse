#include "tinycthread.h"
#include "start_game.h"

sync_queue_t in_blockchain_queue;
sync_queue_t out_blockchain_queue;

/**
 * @brief Условие прослушивания @c out_blockchain_queue.
 * 
 * Используется для завершения потока, который слушает @c out_blockchain_queue
*/
static int listen_blockchain = 1;

/**
 * @brief В цикле получает изменения из @c out_blockchain_queue, пока listen_blockchain != 0
 * 
 * Используется для запуска потока
*/
static int out_queue_listener(void*);

void start_game(void) {
    queue_init(&in_blockchain_queue);
    queue_init(&out_blockchain_queue);

    thrd_t blockchain_listen_thread;
    thrd_create(&blockchain_listen_thread, out_queue_listener, NULL);

    thrd_join(blockchain_listen_thread, NULL);

    queue_destroy(&in_blockchain_queue);
    queue_destroy(&out_blockchain_queue);
}

static int out_queue_listener(void*) {
    while (listen_blockchain) {
        // работа с полученными данными
        dequeue(&out_blockchain_queue);
    }

    return 0;
}
