#ifndef _sync_queue_h_
#define _sync_queue_h_

#include "tinycthread.h"

#define QUEUE_SUCCESS 0
#define QUEUE_FAILURE -1

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
    int m_block_old_id;
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

    int enabled;
} sync_queue_t;


/**
 * @brief Очередь в которую пишутся изменения, которые будут переданы в Neo
*/
extern sync_queue_t in_blockchain_queue;

/**
 * @brief Очередь в которой появляются изменения присланные из Neo
 */
extern sync_queue_t out_blockchain_queue;


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
 * @brief Делает очердь доступной.
 *
 * @param queue Указатель на синхронизированную очередь.
 */
void queue_enable(sync_queue_t *queue);

/**
 * @brief Делает очередь недоступной.
 *
 * @param queue Указатель на синхронизированную очередь.
 */
void queue_disable(sync_queue_t *queue);

/**
 * @brief Добавляет элемент в конец синхронизированной очереди.
 *
 * @param queue Указатель на синхронизированную очередь.
 * @param entry Запись, которую нужно добавить в очередь.
 * @return @c QUEUE_SUCCESS, если элемент был успешно помещен в очередь,
 * @c QUEUE_FAILURE, если очередь была уничтожена.
 */
int enqueue(sync_queue_t *queue, sync_queue_entry_t entry);

/**
 * @brief Удаляет и возвращает первый элемент из синхронизированной очереди.
 * 
 * Блокируется пока очередь пуста или пока очередь не будет уничтожена.
 *
 * @param queue Указатель на синхронизированную очередь.
 * @param out_entry Указатель, куда будет записан удаленный из очереди элемент.
 * @return @c QUEUE_SUCCESS, если элемент был успешно извелечен,
 * @c QUEUE_FAILURE, если очередь была уничтожена.
 */
int dequeue(sync_queue_t *queue, sync_queue_entry_t* out_entry);

/**
 * @brief Удаляет и возвращает первый элемент из синхронизированной очереди, если он есть.
 *
 * @param queue Указатель на синхронизированную очередь.
 * @param out_entry Указатель, куда будет записан удаленный из очереди элемент.
 * @return @c QUEUE_SUCCESS, если элемент был успешно извелечен,
 * @c QUEUE_FAILURE, если элемента нет или очередь была уничтожена.
 */
int try_dequeue(sync_queue_t *queue, sync_queue_entry_t* out_entry);

#endif
