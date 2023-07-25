cmake_minimum_required(VERSION 3.22.1)

project(craft)

# config file
if(NOT DEFINED ${TEXTURES_DIRECTORY})
        set(TEXTURES_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/texture)
endif()

if(NOT DEFINED ${SHADERS_DIRECTORY})
        set(SHADERS_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/shaders)
endif()

configure_file(src/config.h.in
        ${CMAKE_CURRENT_BINARY_DIR}/include/config.h)

        
# JNATIVE library
FILE(GLOB JNATIVE_SOURCE_FILES JNI/*.c)
add_library(jnative SHARED ${JNATIVE_SOURCE_FILES})

# JNI
find_package(JNI REQUIRED)
target_include_directories(jnative PUBLIC ${JNI_INCLUDE_DIRS})

#
FILE(GLOB SOURCE_FILES src/*.c)
add_library(
    craft SHARED
    ${SOURCE_FILES}
    deps/glew/src/glew.c
    deps/lodepng/lodepng.c
    deps/noise/noise.c
    deps/sqlite/sqlite3.c
    deps/tinycthread/tinycthread.c)

target_include_directories(craft
    PUBLIC ${CMAKE_CURRENT_BINARY_DIR}/include)

add_definitions(-std=c99 -O3)

add_subdirectory(deps/glfw)
include_directories(deps/glew/include)
include_directories(deps/glfw/include)
include_directories(deps/lodepng)
include_directories(deps/noise)
include_directories(deps/sqlite)
include_directories(deps/tinycthread)
include_directories(src)

if(APPLE)
    target_link_libraries(craft glfw ${GLFW_LIBRARIES})
elseif(UNIX)
    target_link_libraries(craft glfw dl ${GLFW_LIBRARIES})
else()
    set(CMAKE_SHARED_LIBRARY_PREFIX "")
    set(CMAKE_STATIC_LIBRARY_PREFIX "")
    target_link_libraries(craft glfw ws2_32.lib ${GLFW_LIBRARIES})
endif()

target_link_libraries(jnative craft)
