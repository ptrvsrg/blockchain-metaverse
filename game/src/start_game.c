#include "tinycthread.h"
#include "start_game.h"
#include "game.h"

#include <stdio.h>

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
    // queue_init(&in_blockchain_queue);
    // queue_init(&out_blockchain_queue);

    // thrd_t blockchain_listen_thread;
    // thrd_create(&blockchain_listen_thread, out_queue_listener, NULL);

    run();
    mtx_lock(&out_blockchain_queue.mutex);
    out_blockchain_queue.enabled = 0;
    cnd_broadcast(&out_blockchain_queue.cond);
    mtx_unlock(&out_blockchain_queue.mutex);
    mtx_lock(&in_blockchain_queue.mutex);
    in_blockchain_queue.enabled = 0;
    cnd_broadcast(&in_blockchain_queue.cond);
    mtx_unlock(&in_blockchain_queue.mutex);

    // listen_blockchain = 0;
    // thrd_join(blockchain_listen_thread, NULL);

    // queue_destroy(&in_blockchain_queue);
    // queue_destroy(&out_blockchain_queue);
}

static int out_queue_listener(void*) {
    while (listen_blockchain) {
        // работа с полученными данными
        // dequeue(&out_blockchain_queue);
    }

    return 0;
}
