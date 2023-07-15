#include "sync_queue.h"

#include <stdlib.h>

void queue_init(sync_queue_t *queue) {
    queue->front = NULL;
    queue->rear = NULL;
    mtx_init(&queue->mutex, mtx_plain);
    cnd_init(&queue->cond);

    queue->enabled = 1;
}

void queue_destroy(sync_queue_t *queue) {
    mtx_lock(&queue->mutex);
        sync_queue_node_t *current = queue->front;
        while (current != NULL) {
            sync_queue_node_t *next = current->next;
            free(current);
            current = next;
        }
        cnd_broadcast(&queue->cond);
    mtx_unlock(&queue->mutex);

    mtx_destroy(&queue->mutex);
    cnd_destroy(&queue->cond);

    queue->enabled = 0;
}

int enqueue(sync_queue_t *queue, sync_queue_entry_t data) {
    int result = QUEUE_FAILURE;
    mtx_lock(&queue->mutex);
        if (queue->enabled) {
            sync_queue_node_t *new_node = (sync_queue_node_t *) malloc(sizeof(sync_queue_node_t));
            new_node->data = data;
            new_node->next = NULL;
            if (queue->front == NULL && queue->rear == NULL) {
                queue->front = new_node;
            } else {
                queue->rear->next = new_node;
            }
            queue->rear = new_node;
            cnd_broadcast(&queue->cond);
            result = QUEUE_SUCCESS;
        }
    mtx_unlock(&queue->mutex);
    return result;
}

int dequeue(sync_queue_t *queue, sync_queue_entry_t* out) {
    int result = QUEUE_FAILURE;
    mtx_lock(&queue->mutex);
        while (queue->front == NULL && queue->enabled) {
            cnd_wait(&queue->cond, &queue->mutex);
        }
        if (queue->enabled) {
            sync_queue_node_t *temp = queue->front;
            *out = temp->data;
            if (queue->front == queue->rear) {
                queue->front = NULL;
                queue->rear = NULL;
            } else {
                queue->front = queue->front->next;
            }
            free(temp);
            result = QUEUE_SUCCESS;
        }
    mtx_unlock(&queue->mutex);

    return result;
}

int try_dequeue(sync_queue_t *queue, sync_queue_entry_t* out_entry) {
    int result = QUEUE_FAILURE;
    mtx_lock(&queue->mutex);
        if (queue->enabled && queue->front != NULL) {
            sync_queue_node_t *temp = queue->front;
            *out_entry = temp->data;
            if (queue->front == queue->rear) {
                queue->front = NULL;
                queue->rear = NULL;
            } else {
                queue->front = queue->front->next;
            }
            free(temp);
            result = QUEUE_SUCCESS;
        }
    mtx_unlock(&queue->mutex);

    return result;
}
