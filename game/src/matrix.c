#include "matrix.h"

#include <math.h>

#include "util.h"

void normalize(float *x, float *y, float *z) {
    float d = sqrtf((*x) * (*x) + (*y) * (*y) + (*z) * (*z));
    *x /= d;
    *y /= d;
    *z /= d;
}

void matrix_identity(float *matrix) {
    matrix[0] = 1;
    matrix[1] = 0;
    matrix[2] = 0;
    matrix[3] = 0;
    matrix[4] = 0;
    matrix[5] = 1;
    matrix[6] = 0;
    matrix[7] = 0;
    matrix[8] = 0;
    matrix[9] = 0;
    matrix[10] = 1;
    matrix[11] = 0;
    matrix[12] = 0;
    matrix[13] = 0;
    matrix[14] = 0;
    matrix[15] = 1;
}

void matrix_translate(float *matrix, float dx, float dy, float dz) {
    matrix[0] = 1;
    matrix[1] = 0;
    matrix[2] = 0;
    matrix[3] = 0;
    matrix[4] = 0;
    matrix[5] = 1;
    matrix[6] = 0;
    matrix[7] = 0;
    matrix[8] = 0;
    matrix[9] = 0;
    matrix[10] = 1;
    matrix[11] = 0;
    matrix[12] = dx;
    matrix[13] = dy;
    matrix[14] = dz;
    matrix[15] = 1;
}

void matrix_rotate(float *matrix, float x, float y, float z, float angle) {
    normalize(&x, &y, &z);
    float s = sinf(angle);
    float c = cosf(angle);
    float m = 1 - c;
    matrix[0] = m * x * x + c;
    matrix[1] = m * x * y - z * s;
    matrix[2] = m * z * x + y * s;
    matrix[3] = 0;
    matrix[4] = m * x * y + z * s;
    matrix[5] = m * y * y + c;
    matrix[6] = m * y * z - x * s;
    matrix[7] = 0;
    matrix[8] = m * z * x - y * s;
    matrix[9] = m * y * z + x * s;
    matrix[10] = m * z * z + c;
    matrix[11] = 0;
    matrix[12] = 0;
    matrix[13] = 0;
    matrix[14] = 0;
    matrix[15] = 1;
}

void vector_multiply(float *vector, float *a, float *b) {
    float result[4];
    for (int i = 0; i < 4; i++) {
        float total = 0;
        for (int j = 0; j < 4; j++) {
            int p = j * 4 + i;
            int q = j;
            total += a[p] * b[q];
        }
        result[i] = total;
    }
    for (int i = 0; i < 4; i++) {
        vector[i] = result[i];
    }
}

void matrix_multiply(float *matrix, float *a, float *b) {
    float result[16];
    for (int c = 0; c < 4; c++) {
        for (int r = 0; r < 4; r++) {
            int index = c * 4 + r;
            float total = 0;
            for (int i = 0; i < 4; i++) {
                int p = i * 4 + r;
                int q = c * 4 + i;
                total += a[p] * b[q];
            }
            result[index] = total;
        }
    }
    for (int i = 0; i < 16; i++) {
        matrix[i] = result[i];
    }
}

void matrix_frustum(float *matrix, float left, float right, float bottom, float top, float znear, float zfar) {
    float temp, temp2, temp3, temp4;
    temp = 2.0 * znear;
    temp2 = right - left;
    temp3 = top - bottom;
    temp4 = zfar - znear;
    matrix[0] = temp / temp2;
    matrix[1] = 0.0;
    matrix[2] = 0.0;
    matrix[3] = 0.0;
    matrix[4] = 0.0;
    matrix[5] = temp / temp3;
    matrix[6] = 0.0;
    matrix[7] = 0.0;
    matrix[8] = (right + left) / temp2;
    matrix[9] = (top + bottom) / temp3;
    matrix[10] = (-zfar - znear) / temp4;
    matrix[11] = -1.0;
    matrix[12] = 0.0;
    matrix[13] = 0.0;
    matrix[14] = (-temp * zfar) / temp4;
    matrix[15] = 0.0;
}

void matrix_perspective(float *matrix, float fov, float aspect, float znear, float zfar) {
    float ymax, xmax;
    ymax = znear * tanf(fov * PI / 360.0);
    xmax = ymax * aspect;
    matrix_frustum(matrix, -xmax, xmax, -ymax, ymax, znear, zfar);
}

void matrix_ortho(float *matrix, float left, float right, float bottom, float top, float near, float far) {
    matrix[0] = 2 / (right - left);
    matrix[1] = 0;
    matrix[2] = 0;
    matrix[3] = 0;
    matrix[4] = 0;
    matrix[5] = 2 / (top - bottom);
    matrix[6] = 0;
    matrix[7] = 0;
    matrix[8] = 0;
    matrix[9] = 0;
    matrix[10] = -2 / (far - near);
    matrix[11] = 0;
    matrix[12] = -(right + left) / (right - left);
    matrix[13] = -(top + bottom) / (top - bottom);
    matrix[14] = -(far + near) / (far - near);
    matrix[15] = 1;
}

void matrix_update_2d(float *matrix, int width, int height) {
    matrix_ortho(matrix, 0, width, 0, height, -1, 1);
}

void matrix_update_3d(float *matrix, int width, int height, float x, float y, float z,
                      float rx, float ry, float fov, int ortho) {
    float a[16];
    float b[16];
    float aspect = (float) width / height;
    matrix_identity(a);
    matrix_translate(b, -x, -y, -z);
    matrix_multiply(a, b, a);
    matrix_rotate(b, cosf(rx), 0, sinf(rx), ry);
    matrix_multiply(a, b, a);
    matrix_rotate(b, 0, 1, 0, -rx);
    matrix_multiply(a, b, a);
    if (ortho) {
        int size = 32;
        matrix_ortho(b, -size * aspect, size * aspect, -size, size, -256, 256);
    } else {
        matrix_perspective(b, fov, aspect, 0.1, 1024.0);
    }
    matrix_multiply(a, b, a);
    matrix_identity(matrix);
    matrix_multiply(matrix, a, matrix);
}

void matrix_update_item(float *matrix, int width, int height) {
    float a[16];
    float b[16];
    float aspect = (float) width / height;
    float size = 64;
    float box = height / size / 2;
    float xoffset = 1 - size / width * 2;
    float yoffset = 1 - size / height * 2;
    matrix_identity(a);
    matrix_rotate(b, 0, 1, 0, PI / 4);
    matrix_multiply(a, b, a);
    matrix_rotate(b, 1, 0, 0, -PI / 10);
    matrix_multiply(a, b, a);
    matrix_ortho(b, -box * aspect, box * aspect, -box, box, -1, 1);
    matrix_multiply(a, b, a);
    matrix_translate(b, -xoffset, -yoffset, 0);
    matrix_multiply(a, b, a);
    matrix_identity(matrix);
    matrix_multiply(matrix, a, matrix);
}