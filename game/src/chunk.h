#ifndef _chunk_h_
#define _chunk_h_

#include "map.h"
#include "GL/glew.h"

/**
 * @struct Структура представляющая информацию для рендера чанка
*/
typedef struct {
    Map map; /**< карта чанка */
    int p; /**< координата p чанка */
    int q; /**< координата q чанка */
    int faces; /**< информация для рендера */
    GLuint position_buffer; /**< информация для рендера */
    GLuint normal_buffer; /**< информация для рендера */
    GLuint uv_buffer; /**< информация для рендера */
} Chunk;

#endif // _chunk_h_
