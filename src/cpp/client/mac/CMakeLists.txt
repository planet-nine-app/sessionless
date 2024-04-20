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
FetchContent_MakeAvailable(${SECP256K1_LIB_NAME})

#################################################
# Sessionless

set(TARGET sessionless)
project(${TARGET})
set(CMAKE_CXX_STANDARD 17)
add_executable(${TARGET})
target_sources(${TARGET} PRIVATE main.cpp sessionless.cpp)
target_link_libraries(${TARGET} PRIVATE ${SECP256K1_LIB_NAME})
