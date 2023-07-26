cmake_minimum_required(VERSION 3.22.1)

project(craft)

set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}/lib)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}/lib)

# config file
if(NOT DEFINED ${TEXTURES_DIRECTORY})
        # set(TEXTURES_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/texture)
        set(TEXTURES_DIRECTORY ./texture)
endif()

if(NOT DEFINED ${SHADERS_DIRECTORY})
        # set(SHADERS_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/shaders)
        set(SHADERS_DIRECTORY ./shaders)
endif()

configure_file(src/config.h.in
        ${CMAKE_CURRENT_BINARY_DIR}/include/config.h)

# JNATIVE library
FILE(GLOB JNATIVE_SOURCE_FILES JNI/*.c)
add_library(jnative SHARED ${JNATIVE_SOURCE_FILES})

# JNI
find_package(JNI REQUIRED)
target_include_directories(jnative PUBLIC ${JNI_INCLUDE_DIRS})

# CRAFT library
FILE(GLOB SOURCE_FILES src/*.c)
add_library(
    craft SHARED
    ${SOURCE_FILES})
target_include_directories(craft
        PUBLIC ${CMAKE_CURRENT_BINARY_DIR}/include)
target_include_directories(jnative
        PUBLIC src)

# GLEW library
find_package(OpenGL REQUIRED)
find_package(X11 REQUIRED)
FILE(GLOB GLEW_SOURCE_FILES deps/glew/src/*.c)
SET(GLEW_INCLUDE_DIR deps/glew/include)
add_library(glew SHARED
        ${GLEW_SOURCE_FILES})
target_include_directories(glew
        PUBLIC ${GLEW_INCLUDE_DIR})
target_link_libraries(glew
        PUBLIC ${OPENGL_LIBRARIES}
        PUBLIC ${X11_LIBRARIES})

# LodePNG library
FILE(GLOB LODEPNG_SOURCE_FILES deps/lodepng/*.c)
SET(LODEPNG_INCLUDE_DIR deps/lodepng)
add_library(lodepng SHARED
        ${LODEPNG_SOURCE_FILES})
target_include_directories(lodepng
        PUBLIC ${LODEPNG_INCLUDE_DIR})

# noise library
FILE(GLOB NOISE_SOURCE_FILES deps/noise/*.c)
SET(NOISE_INCLUDE_DIR deps/noise)
add_library(noise SHARED
        ${NOISE_SOURCE_FILES})
target_include_directories(noise
        PUBLIC ${NOISE_INCLUDE_DIR})

# TinyCThread library
FILE(GLOB TINYCTHREAD_SOURCE_FILES deps/tinycthread/*.c)
        SET(TINYCTHREAD_INCLUDE_DIR deps/tinycthread)
add_library(tinycthread SHARED
        ${TINYCTHREAD_SOURCE_FILES})
target_include_directories(craft
        PUBLIC ${TINYCTHREAD_INCLUDE_DIR})

# SQLite library
FILE(GLOB SQLITE_SOURCE_FILES deps/sqlite/*.c)
SET(SQLITE_INCLUDE_DIR deps/sqlite)
add_library(sqlite SHARED
        ${SQLITE_SOURCE_FILES})
target_include_directories(sqlite
        PUBLIC ${SQLITE_INCLUDE_DIR})

# GLFW library
SET(GLFW_INCLUDE_DIR deps/glfw/include)
add_subdirectory(deps/glfw)
target_include_directories(craft
        PRIVATE ${GLFW_INCLUDE_DIR})

# Linking
if(APPLE)
    target_link_libraries(craft
            PRIVATE glfw
            PRIVATE glew
            PRIVATE lodepng
            PRIVATE noise
            PRIVATE tinycthread
            PRIVATE sqlite
            PRIVATE ${GLFW_LIBRARIES})
elseif (UNIX)
    target_link_libraries(craft
            PRIVATE dl
            PRIVATE glfw
            PRIVATE glew
            PRIVATE lodepng
            PRIVATE noise
            PRIVATE tinycthread
            PRIVATE sqlite
            PRIVATE ${GLFW_LIBRARIES})
elseif (MINGW)
        set(CMAKE_SHARED_LIBRARY_PREFIX "")
        target_link_libraries(craft
            PRIVATE ws2_32.lib
            PRIVATE glfw
            PRIVATE glew
            PRIVATE lodepng
            PRIVATE noise
            PRIVATE tinycthread
            PRIVATE sqlite
            PRIVATE ${GLFW_LIBRARIES})
endif()

target_link_libraries(jnative
            PRIVATE craft
)
