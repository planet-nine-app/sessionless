
### Updated `CMakeLists.txt`

cmake_minimum_required(VERSION 3.16)

# Define flags for building for Pico and Arduino
option(BUILD_FOR_PICO "Build for Raspberry Pi Pico" OFF)
option(BUILD_FOR_ARDUINO "Build for Arduino" OFF)

if(BUILD_FOR_PICO)
    # Set the Pico SDK path (update this path to match your setup)
    if(NOT DEFINED PICO_SDK_PATH)
        set(PICO_SDK_PATH "C:/path/to/pico-sdk")
    endif()

    # Include the Pico SDK
    include(${PICO_SDK_PATH}/external/pico_sdk_import.cmake)

    # Initialize the Pico SDK
    pico_sdk_init()
endif()

# Project name and settings
project(sessionless)
set(CMAKE_C_STANDARD 11)
set(CMAKE_CXX_STANDARD 17)

#################################################
# secp256k1 library

set(SECP256K1_LIB_NAME secp256k1)
include(FetchContent)
FetchContent_Declare(
  ${SECP256K1_LIB_NAME}
  GIT_REPOSITORY https://github.com/bitcoin-core/secp256k1.git
  GIT_TAG        v0.4.1
)

set(SECP256K1_BUILD_TESTS OFF)
set(SECP256K1_BUILD_EXHAUSTIVE_TESTS OFF)
set(SECP256K1_BUILD_BENCHMARK OFF)

set(CMAKE_BUILD_TYPE Release)
set(BUILD_SHARED_LIBS FALSE)

FetchContent_MakeAvailable(${SECP256K1_LIB_NAME})

#################################################
# Sessionless library

if(BUILD_FOR_PICO)
    add_executable(sessionless
        src/sessionless.cpp
    )

    target_include_directories(sessionless PUBLIC src)
    target_link_libraries(sessionless PRIVATE ${SECP256K1_LIB_NAME} pico_stdlib)

    # Enable USB output, disable UART output
    pico_enable_stdio_usb(sessionless 1)
    pico_enable_stdio_uart(sessionless 0)

    # Create map/bin/hex/uf2 files
    pico_add_extra_outputs(sessionless)

elseif(BUILD_FOR_ARDUINO)
    add_executable(sessionless
        src/sessionless.cpp
    )

    target_include_directories(sessionless PUBLIC src)
    target_link_libraries(sessionless PRIVATE ${SECP256K1_LIB_NAME})

    # Add Arduino specific configurations here if necessary

else()
    set(TARGET sessionless)
    set(CMAKE_BUILD_TYPE Release)
    add_library(${TARGET} STATIC src/sessionless.cpp)
    target_include_directories(${TARGET} PUBLIC src)
    target_link_libraries(${TARGET} PRIVATE ${SECP256K1_LIB_NAME})
endif()
