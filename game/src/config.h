#ifndef _config_h_
#define _config_h_

#define DB_NAME "craft.sqlite"  /* Имя базы данных */
#define FULLSCREEN 0            /* Режим окна */
#define VSYNC 1                 /* Минимальное количество обновлений экрана для ожидания до замены буферов */
#define WINDOW_WIDTH 1024       /* Ширина игрового окна */
#define WINDOW_HEIGHT 768       /* Высота игрового окна */
#define WINDOW_TITLE "Craft"    /* Заголовок игрового окна */
#define SCROLL_THRESHOLD 0.1    /* Порог прокрутки ввода */
#define BLOCK_COUNT 11          /* Общее количество типов блоков в игре */
#define CHUNK_SIZE 32           /* Размер каждого чанка в игровом мире */
#define MAX_CHUNKS 1024         /* Максимальное количество загружаемых одновременно чанков */
#define CREATE_CHUNK_RADIUS 6   /* Радиус вокруг игрока, в котором создаются новые чанки */
#define RENDER_CHUNK_RADIUS 6   /* Радиус вокруг игрока, в котором рендерятся чанки */
#define DELETE_CHUNK_RADIUS 8   /* Радиус вокруг игрока, в котором чанки удаляются для освобождения памяти */

#endif // _config_h_
