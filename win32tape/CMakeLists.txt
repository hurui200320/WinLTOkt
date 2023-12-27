cmake_minimum_required(VERSION 3.27)
project(win32tape C CXX)

set(CMAKE_CXX_STANDARD 20)
# we use `__declspec(dllexport)` instead of this option
#set(CMAKE_WINDOWS_EXPORT_ALL_SYMBOLS ON)
set(CMAKE_SYSTEM_NAME Windows)

add_library(win32tape SHARED win32tape.cpp)
add_custom_command(TARGET win32tape
        POST_BUILD
        COMMAND ${CMAKE_COMMAND} -E copy $<TARGET_FILE:win32tape> "${CMAKE_CURRENT_SOURCE_DIR}/.."
        COMMENT "Copy dll file to parent directory" VERBATIM
)
add_executable(main Main.cpp)
target_link_libraries(main win32tape)
