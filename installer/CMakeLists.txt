cmake_minimum_required(VERSION 3.23)

project(bedrockRInstaller)

find_program(GLIB_COMPILE_RESOURCES NAMES glib-compile-resources REQUIRED)

# 2. Define paths for your GResource XML and generated C file
set(GRESOURCE_XML "${CMAKE_CURRENT_SOURCE_DIR}/res/app.gresource.xml")
set(GRESOURCE_C   "${CMAKE_CURRENT_BINARY_DIR}/resources.cpp")
message(STATUS ${CMAKE_CURRENT_SOURCE_DIR})
message(STATUS ${CMAKE_CURRENT_BINARY_DIR})
message(STATUS ${GLIB_COMPILE_RESOURCES})
message(STATUS ${GRESOURCE_C})

# 3. Compile the GResource into C source code
add_custom_command(
        OUTPUT ${GRESOURCE_C}
        WORKING_DIRECTORY "C:/msys64/ucrt64/share/themes/windows/gtk-4.0"
        COMMAND ${GLIB_COMPILE_RESOURCES}
        --target=${GRESOURCE_C}
        --generate-source
        ${GRESOURCE_XML}
        VERBATIM
        USES_TERMINAL
)

add_executable(bedrockRInstaller
        src/main.cpp)

find_package (PkgConfig REQUIRED)
pkg_check_modules (GTK4 REQUIRED IMPORTED_TARGET gtk4)

target_link_libraries(bedrockRInstaller PRIVATE PkgConfig::GTK4)