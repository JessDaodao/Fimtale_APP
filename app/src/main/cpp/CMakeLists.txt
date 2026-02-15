cmake_minimum_required(VERSION 3.22.1)

project("fimtale")

add_library(
        fimtale
        SHARED
        native-lib.cpp)

find_library(
        log-lib
        log)

target_link_libraries(
        fimtale
        ${log-lib})
