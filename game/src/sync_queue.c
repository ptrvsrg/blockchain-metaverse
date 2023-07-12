#include "sync_queue.h"

#include <stdlib.h>

#include "tinycthread.h"

typedef struct sync_queue_node_t sync_queue_node_t;
struct sync_queue_node_t {
    sync_queue_entry_t data;
    sync_queue_node_t *next;
};

struct sync_queue_t {
    sync_queue_node_t *front;
    sync_queue_node_t *rear;
    mtx_t mutex;
    cnd_t cond;
};

void init_queue(sync_queue_t *queue) {
    queue->front = NULL;
    queue->rear = NULL;
    mtx_init(&queue->mutex, mtx_plain);
    cnd_init(&queue->cond);
}

void destroy_queue(sync_queue_t *queue) {
    mtx_lock(&queue->mutex);
        sync_queue_node_t *current = queue->front;
        while (current != NULL) {
            sync_queue_node_t *next = current->next;
            free(current);
            current = next;
        }
    mtx_unlock(&queue->mutex);

    mtx_destroy(&queue->mutex);
    cnd_destroy(&queue->cond);
}

void enqueue(sync_queue_t *queue, sync_queue_entry_t data) {
    sync_queue_node_t *new_node = (sync_queue_node_t *) malloc(sizeof(sync_queue_node_t));
    new_node->data = data;
    new_node->next = NULL;

    mtx_lock(&queue->mutex);
        if (queue->front == NULL && queue->rear == NULL) {
            queue->front = new_node;
        } else {
            queue->rear->next = new_node;
        }
        queue->rear = new_node;
        cnd_broadcast(&queue->cond);
    mtx_unlock(&queue->mutex);
}

sync_queue_entry_t dequeue(sync_queue_t *queue) {
    mtx_lock(&queue->mutex);
        while (queue->front == NULL) {
            cnd_wait(&queue->cond, &queue->mutex);
        }
        sync_queue_node_t *temp = queue->front;
        sync_queue_entry_t entry = temp->data;
        if (queue->front == queue->rear) {
            queue->front = NULL;
            queue->rear = NULL;
        } else {
            queue->front = queue->front->next;
        }
    mtx_unlock(&queue->mutex);

    free(temp);

    return entry;
}
