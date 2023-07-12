#ifndef _sync_queue_h_
#define _sync_queue_h_

#include "tinycthread.h"

/**
 * @brief Структура, представляющая запись в синхронизированной очереди.
 */
typedef struct sync_queue_entry_t {
    int m_chunk_x;
    int m_chunk_z;
    int m_block_x;
    int m_block_y;
    int m_block_z;
    int m_block_id;
} sync_queue_entry_t;

/**
 * @brief Структура, представляющая узел в синхронизированной очереди.
 */
typedef struct sync_queue_node_t sync_queue_node_t;
struct sync_queue_node_t {
    sync_queue_entry_t data;
    sync_queue_node_t *next;
};

/**
 * @brief Структура, представляющая синхронизированную очередь.
 */
typedef struct sync_queue_t {
    sync_queue_node_t *front;
    sync_queue_node_t *rear;

    mtx_t mutex;
    cnd_t cond;
} sync_queue_t;

/**
 * @brief Инициализирует синхронизированную очередь.
 *
 * @param queue Указатель на синхронизированную очередь, которую необходимо инициализировать.
 */
void queue_init(sync_queue_t *queue);

/**
 * @brief Уничтожает синхронизированную очередь и освобождает выделенные ресурсы.
 *
 * @param queue Указатель на синхронизированную очередь, которую необходимо уничтожить.
 */
void queue_destroy(sync_queue_t *queue);

/**
 * @brief Добавляет элемент в конец синхронизированной очереди.
 *
 * @param queue Указатель на синхронизированную очередь.
 * @param entry Запись, которую нужно добавить в очередь.
 */
void enqueue(sync_queue_t *queue, sync_queue_entry_t entry);

/**
 * @brief Удаляет и возвращает первый элемент из синхронизированной очереди.
 *
 * @param queue Указатель на синхронизированную очередь.
 * @return Запись, которая была удалена из очереди.
 */
sync_queue_entry_t dequeue(sync_queue_t *queue);

#endif
