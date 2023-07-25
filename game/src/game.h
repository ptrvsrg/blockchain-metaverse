#include <GL/glew.h>
#include "config.h"

#define MAX_CHUNKS 1024
#define MAX_PLAYERS 128
#define MAX_TEXT_LENGTH 256
#define MAX_NAME_LENGTH 32

#define LEFT 0
#define CENTER 1
#define RIGHT 2

typedef struct {
    float x;
    float y;
    float z;
    float rx;
    float ry;
    float t;
} State;

typedef struct {
    int id;
    char name[MAX_NAME_LENGTH];
    State state;
    State state1;
    State state2;
    GLuint buffer;
} Player;

extern Player players[];

int run(State state);
