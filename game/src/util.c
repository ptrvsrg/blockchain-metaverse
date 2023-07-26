#include "util.h"

#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <GL/glew.h>

#include "lodepng.h"
#include "matrix.h"

static char *load_file(const char *path) {
    FILE *file = fopen(path, "rb");
    if (!file) {
        fprintf(stderr, "fopen %s failed: %d %s\n",
                path, errno, strerror(errno));
        exit(1);
    }
    // Get file content's size
    fseek(file, 0, SEEK_END);
    int length = ftell(file);
    rewind(file);
    // Allocate space and read in the data
    char *data = calloc(length + 1, sizeof(char));
    if (!data) {
        fclose(file);
        fprintf(stderr, "calloc failed: %d %s\n",
                errno, strerror(errno));
        exit(1);
    }
    fread(data, 1, length, file);
    fclose(file);
    return data;
}

void load_png_texture(const char *file_name) {
    unsigned error;
    unsigned char *image;
    unsigned width;
    unsigned height;
    error = lodepng_decode32_file(&image, &width, &height, file_name);
    if (error) {
        fprintf(stderr, "error %u: %s\n", error, lodepng_error_text(error));
        exit(1);
    }
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA,
                 GL_UNSIGNED_BYTE, image);
    free(image);
}

GLuint make_buffer(GLenum target, GLsizei size, const void *data) {
    GLuint buffer;
    glGenBuffers(1, &buffer);
    glBindBuffer(target, buffer);
    glBufferData(target, size, data, GL_STATIC_DRAW);
    glBindBuffer(target, 0);
    return buffer;
}

static GLuint make_shader(GLenum type, const char *source) {
    GLuint shader = glCreateShader(type);
    glShaderSource(shader, 1, &source, NULL);
    glCompileShader(shader);
    // Get shader status, so we can print an error if compiling it failed
    GLint status;
    glGetShaderiv(shader, GL_COMPILE_STATUS, &status);
    if (status == GL_FALSE) {
        GLint length;
        glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &length);
        GLchar *info = calloc(length, sizeof(GLchar));
        glGetShaderInfoLog(shader, length, NULL, info);
        fprintf(stderr, "glCompileShader failed:\n%s\n", info);
        free(info);
    }
    return shader;
}

static GLuint load_shader(GLenum type, const char *path) {
    char *data = load_file(path);
    GLuint result = make_shader(type, data);
    free(data);
    return result;
}

static GLuint make_GPU_program(GLuint vertex_shader, GLuint fragment_shader) {
    GLuint program = glCreateProgram();
    glAttachShader(program, vertex_shader);
    glAttachShader(program, fragment_shader);
    glLinkProgram(program);
    GLint status;
    glGetProgramiv(program, GL_LINK_STATUS, &status);
    if (status == GL_FALSE) {
        GLint length;
        glGetProgramiv(program, GL_INFO_LOG_LENGTH, &length);
        GLchar *info = calloc(length, sizeof(GLchar));
        glGetProgramInfoLog(program, length, NULL, info);
        fprintf(stderr, "glLinkProgram failed: %s\n", info);
        free(info);
    }
    glDetachShader(program, vertex_shader);
    glDetachShader(program, fragment_shader);
    glDeleteShader(vertex_shader);
    glDeleteShader(fragment_shader);
    return program;
}

GLuint load_GPU_program(const char *vertex_path, const char *fragments_path) {
    GLuint vertex_shader = load_shader(GL_VERTEX_SHADER, vertex_path);
    GLuint fragment_shader = load_shader(GL_FRAGMENT_SHADER, fragments_path);
    GLuint program = make_GPU_program(vertex_shader, fragment_shader);
    return program;
}
