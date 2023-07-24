cmake_minimum_required(VERSION 2.8)

project(craft)

FILE(GLOB SOURCE_FILES src/*.c)

# JNATIVE library
FILE(GLOB JNATIVE_SOURCE_FILES JNI/*.c)
add_library(jnative SHARED ${JNATIVE_SOURCE_FILES})

# JNI
find_package(JNI REQUIRED)
target_include_directories(jnative PUBLIC ${JNI_INCLUDE_DIRS})

add_library(
    craft SHARED
    ${SOURCE_FILES}
    deps/glew/src/glew.c
    deps/lodepng/lodepng.c
    deps/noise/noise.c
    deps/sqlite/sqlite3.c
    deps/tinycthread/tinycthread.c)

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
