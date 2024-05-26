cmake_minimum_required(VERSION 3.16)

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

set(TARGET sessionless)
set(CMAKE_BUILD_TYPE Release)
project(${TARGET})
set(CMAKE_CXX_STANDARD 17)
add_library(${TARGET} STATIC src/sessionless.cpp)
target_include_directories(${TARGET} PUBLIC src)
target_link_libraries(${TARGET} PRIVATE ${SECP256K1_LIB_NAME})
