#ifndef _map_h_
#define _map_h_

/**
 * @brief Макрос для проверки, является ли запись пустой.
 * @param e Указатель на запись.
 * @return 1, если запись пустая, иначе 0.
 */
#define EMPTY_ENTRY(e) (!(e)->x && !(e)->y && !(e)->z && !(e)->w)

/**
 * @brief Макрос для итерации по всем записям карты.
 * @param map Указатель на карту.
 * @param entry Имя переменной для записи.
 */
#define MAP_FOR_EACH(map, entry) \
    for (unsigned int i = 0; i <= map->mask; i++) { \
        MapEntry *entry = map->data + i; \
        if (EMPTY_ENTRY(entry)) { \
            continue; \
        }

/**
 * @brief Макрос для завершения итерации по записям карты.
 */
#define END_MAP_FOR_EACH }

/**
 * @brief Структура, представляющая запись в карте.
 */
typedef struct {
    int x; /**< Координата x записи. */
    int y; /**< Координата y записи. */
    int z; /**< Координата z записи. */
    int w; /**< Идентификатор блока. */
} MapEntry;

/**
 * @brief Структура, представляющая карту.
 */
typedef struct {
    unsigned int mask; /**< Маска для размера карты. */
    unsigned int size; /**< Размер карты. */
    MapEntry *data; /**< Массив данных карты. */
} Map;

/**
 * @brief Функция для выделения памяти для карты.
 * @param map Указатель на карту.
 */
void map_alloc(Map *map);

/**
 * @brief Функция для освобождения памяти, занятой картой.
 * @param map Указатель на карту.
 */
void map_free(Map *map);

/**
 * @brief Функция для установки значения в указанные координаты карты.
 * @param map Указатель на карту.
 * @param x Координата x.
 * @param y Координата y.
 * @param z Координата z.
 * @param w Значение записи.
 */
void map_set(Map *map, int x, int y, int z, int w);

/**
 * @brief Функция для получения значения из указанных координат карты.
 * @param map Указатель на карту.
 * @param x Координата x.
 * @param y Координата y.
 * @param z Координата z.
 * @return Значение записи.
 */
int map_get(Map *map, int x, int y, int z);

#endif
