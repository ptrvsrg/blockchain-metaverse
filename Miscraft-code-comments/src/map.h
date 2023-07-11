#ifndef _map_h_
#define _map_h_

#define EMPTY_ENTRY(e) (!(e)->x && !(e)->y && !(e)->z && !(e)->w)

#define MAP_FOR_EACH(map, entry) \
    for (unsigned int i = 0; i <= map->mask; i++) { \
        MapEntry *entry = map->data + i; \
        if (EMPTY_ENTRY(entry)) { \
            continue; \
        }

#define END_MAP_FOR_EACH }

// Data value entry in the Map structure
// UNION of:
// - value
// OR:
// - x block x position
// - y block y position
// - z block z position
// - w: block id (or light value?)
typedef union {
    unsigned int value;
    struct {
        unsigned char x;
        unsigned char y;
        unsigned char z;
        char w;
    } e;
} MapEntry;

typedef struct {
    int dx;
    int dy;
    int dz;
    unsigned int mask;
    unsigned int size;
    MapEntry *data;
} Map;

void map_alloc(Map *map, int dx, int dy, int dz, int mask);
void map_free(Map *map);
void map_copy(Map *dst, Map *src);
void map_grow(Map *map);
int map_set(Map *map, int x, int y, int z, int w);
int map_get(Map *map, int x, int y, int z);

#endif
