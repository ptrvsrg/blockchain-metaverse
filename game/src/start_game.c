#include "tinycthread.h"
#include "start_game.h"
#include "game.h"
#include "sync_queue.h"

#include <stdio.h>

void start_game(void) {
    // queue_init(&in_blockchain_queue);
    // queue_init(&out_blockchain_queue);

    // thrd_t blockchain_listen_thread;
    // thrd_create(&blockchain_listen_thread, out_queue_listener, NULL);

    run();
    queue_disable(&in_blockchain_queue);
    queue_disable(&out_blockchain_queue);

    // listen_blockchain = 0;
    // thrd_join(blockchain_listen_thread, NULL);

    // queue_destroy(&in_blockchain_queue);
    // queue_destroy(&out_blockchain_queue);
}
