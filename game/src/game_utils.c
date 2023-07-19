#include "game_utils.h"
#include <math.h>
#include "util.h"

void get_sight_vector(float rx, float ry, float *vx, float *vy, float *vz) {
    float m = cosf(ry);
    *vx = cosf(rx - RADIANS(90)) * m;
    *vy = sinf(ry);
    *vz = sinf(rx - RADIANS(90)) * m;
}

void get_motion_vector(int sz, int sx, float rx, float ry, float *vx, float *vy, float *vz) {
    *vx = 0;
    *vy = 0;
    *vz = 0;
    if (sz == 0 && sx == 0) {
        return;
    }
    float strafe = atan2f(sz, sx);
    *vx = cosf(rx + strafe);
    *vy = 0;
    *vz = sinf(rx + strafe);
}
