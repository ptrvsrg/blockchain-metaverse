cmake_minimum_required(VERSION 3.22.1)

project(craft)

# Executable file
FILE(GLOB SOURCE_FILES src/*.c)
add_executable(
    craft
    ${SOURCE_FILES})

# GLEW library
FILE(GLOB GLEW_SOURCE_FILES deps/glew/src/*.c)
SET(GLEW_INCLUDE_DIR deps/glew/include)
add_library(glew STATIC
        ${GLEW_SOURCE_FILES})
target_include_directories(glew
        PUBLIC ${GLEW_INCLUDE_DIR})

# LodePNG library
FILE(GLOB LODEPNG_SOURCE_FILES deps/lodepng/*.c)
SET(LODEPNG_INCLUDE_DIR deps/lodepng)
add_library(lodepng STATIC
        ${LODEPNG_SOURCE_FILES})
target_include_directories(lodepng
        PUBLIC ${LODEPNG_INCLUDE_DIR})

# noise library
FILE(GLOB NOISE_SOURCE_FILES deps/noise/*.c)
SET(NOISE_INCLUDE_DIR deps/noise)
add_library(noise STATIC
        ${NOISE_SOURCE_FILES})
target_include_directories(noise
        PUBLIC ${NOISE_INCLUDE_DIR})

# SQLite library
FILE(GLOB SQLITE_SOURCE_FILES deps/sqlite/*.c)
SET(SQLITE_INCLUDE_DIR deps/sqlite)
add_library(sqlite STATIC
        ${SQLITE_SOURCE_FILES})
target_include_directories(sqlite
        PUBLIC ${SQLITE_INCLUDE_DIR})

# TinycThread library
FILE(GLOB TINYCTHREAD_SOURCE_FILES deps/tinycthread/*.c)
SET(TINYCTHREAD_INCLUDE_DIR deps/tinycthread)
add_library(tinycthread STATIC
        ${TINYCTHREAD_SOURCE_FILES})
target_include_directories(tinycthread
        PUBLIC ${TINYCTHREAD_INCLUDE_DIR})

# GLFW library
SET(GLFW_INCLUDE_DIR deps/glfw/include)
add_subdirectory(deps/glfw)
target_include_directories(craft
        PRIVATE ${GLFW_INCLUDE_DIR})

# cURL library
if(MINGW)
    set(CMAKE_LIBRARY_PATH ${CMAKE_LIBRARY_PATH}
        "C:/Program Files/CURL/lib" "C:/Program Files (x86)/CURL/lib")
    set(CMAKE_INCLUDE_PATH ${CMAKE_INCLUDE_PATH}
        "C:/Program Files/CURL/include" "C:/Program Files (x86)/CURL/include")
endif()
find_package(CURL REQUIRED)
target_include_directories(craft
        PRIVATE ${CURL_INCLUDE_DIR})

# Linking
if(APPLE)
    target_link_libraries(craft
            PRIVATE glfw
            PRIVATE glew
            PRIVATE lodepng
            PRIVATE noise
            PRIVATE sqlite
            PRIVATE tinycthread
            PRIVATE ${GLFW_LIBRARIES}
            PRIVATE ${CURL_LIBRARIES})
elseif (UNIX)
    target_link_libraries(craft
            PRIVATE dl
            PRIVATE glfw
            PRIVATE glew
            PRIVATE lodepng
            PRIVATE noise
            PRIVATE sqlite
            PRIVATE tinycthread
            PRIVATE ${GLFW_LIBRARIES}
            PRIVATE ${CURL_LIBRARIES})
elseif (MINGW)
    target_link_libraries(craft
            PRIVATE ws2_32.lib
            PRIVATE glfw
            PRIVATE glew
            PRIVATE lodepng
            PRIVATE noise
            PRIVATE sqlite
            PRIVATE tinycthread
            PRIVATE ${GLFW_LIBRARIES}
            PRIVATE ${CURL_LIBRARIES})
endif()
