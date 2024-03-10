cmake_minimum_required(VERSION 3.4)
project (Sessionless)
add_executable(Sessionless sessionless.hpp sessionless.cpp main.cpp)
# set(CMAKE_INSTALL_RPATH "@loader_path")
# set(CMAKE_BUILD_WITH_INSTALL_RPATH TRUE)
# set(CMAKE_INSTALL_RPATH_USE_LINK_PATH TRUE)
target_include_directories(Sessionless PUBLIC
  ${PROJECT_SOURCE_DIR}/include
)
# target_link_libraries(Sessionless ${PROJECT_SOURCE_DIR}/secp256k1.c.o)
target_link_libraries(Sessionless ${PROJECT_SOURCE_DIR}/libsecp256k1.dylib)


