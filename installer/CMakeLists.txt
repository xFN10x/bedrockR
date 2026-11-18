cmake_minimum_required(VERSION 3.23)

project(bedrockRInstaller)

add_executable(bedrockRInstaller WIN32
        src/main.cpp)

find_package (PkgConfig REQUIRED)
pkg_check_modules (GTK4 REQUIRED IMPORTED_TARGET gtk4)

target_link_libraries(bedrockRInstaller PRIVATE PkgConfig::GTK4)