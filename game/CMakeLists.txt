cmake_minimum_required(VERSION 3.22.1)

project(craft)

# Executable file
FILE(GLOB SOURCE_FILES src/*.c)
add_executable(
    craft
    ${SOURCE_FILES})

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
target_include_directories(noise
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
            PRIVATE ${GLFW_LIBRARIES}
            PRIVATE ${CURL_LIBRARIES})
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
