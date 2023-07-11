#include <math.h>
#include <stdio.h>
#include <stdlib.h>

#include "lodepng.h"

double rand_double() {
    return (double)rand() / (double)RAND_MAX;
}

char *load_file(const char *path) {
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
    fread(data, 1, length, file);
    fclose(file);
    return data;
}

GLuint make_buffer(GLenum target, GLsizei size, const void *data) {
    GLuint buffer;
    glGenBuffers(1, &buffer);
    glBindBuffer(target, buffer);
    glBufferData(target, size, data, GL_STATIC_DRAW);
    glBindBuffer(target, 0);
    return buffer;
}

// Create a shader program from its source code
// Arguments:
// - type: vertex or fragment shader
// - source: program source code string
// Returns:
// - OpenGL shader handle
GLuint make_shader(GLenum type, const char *source) {
    GLuint shader = glCreateShader(type);
    glShaderSource(shader, 1, &source, NULL);
    glCompileShader(shader);
    // Get shader status so we can print an error if compiling it failed
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

// Load a shader program from a file
// Arguments:
// - type: vertex or fragment shader
// - path: file path to load the shader program from
// Returns:
// - OpenGL shader handle
GLuint load_shader(GLenum type, const char *path) {
    char *data = load_file(path);
    GLuint result = make_shader(type, data);
    free(data);
    return result;
}


// Arguments:
// - shader1: a shader handle to attach
// - shader2: a shader handle to attach
// Returns:
// - returns OpenGL program handle
// - deletes shader1 and shader2 from the context
GLuint make_program(GLuint shader1, GLuint shader2) {
    GLuint program = glCreateProgram();
    glAttachShader(program, shader1);
    glAttachShader(program, shader2);
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
    glDetachShader(program, shader1);
    glDetachShader(program, shader2);
    glDeleteShader(shader1);
    glDeleteShader(shader2);
    return program;
}

// Loads a shader program from files.
// Arguments:
// - path1 : vertex shader file path
// - path2 : fragment shader file path
// Returns:
// - OpenGL program handle
GLuint load_program(const char *path1, const char *path2) {
    GLuint shader1 = load_shader(GL_VERTEX_SHADER, path1);
    GLuint shader2 = load_shader(GL_FRAGMENT_SHADER, path2);
    GLuint program = make_program(shader1, shader2);
    return program;
}

// Flip an image vertically
// Notes: assumes 4 channels per image pixel.
// Arguments:
// - data: image pixel data
// - width: image width
// - height: image height
// Returns:
// - no return value
// - modifies data
void flip_image_vertical(
    unsigned char *data, unsigned int width, unsigned int height)
{
    unsigned int size = width * height * 4;
    unsigned int stride = sizeof(char) * width * 4;
    unsigned char *new_data = malloc(sizeof(unsigned char) * size);
    for (unsigned int i = 0; i < height; i++) {
        unsigned int j = height - i - 1;
        memcpy(new_data + j * stride, data + i * stride, stride);
    }
    memcpy(data, new_data, size);
    free(new_data);
}

// Loads a PNG file as a 2D texture for the current OpenGL texture context.
// Arguments:
// - file_name: the png file to load the texture from
// Returns:
// - no return value
// - modifies OpenGL state by loading the image data into the current 2D texture
void load_png_texture(const char *file_name) {
    unsigned int error;
    unsigned char *data;
    unsigned int width, height;
    error = lodepng_decode32_file(&data, &width, &height, file_name);
    if (error) {
        fprintf(stderr, "load_png_texture %s failed, error %u: %s\n",
                file_name, error, lodepng_error_text(error));
        exit(1);
    }
    flip_image_vertical(data, width, height);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA,
        GL_UNSIGNED_BYTE, data);
    // Free the data because lodepng_decode32_file() allocated it,
    // and we copied the data over to OpenGL.
    free(data);
}
