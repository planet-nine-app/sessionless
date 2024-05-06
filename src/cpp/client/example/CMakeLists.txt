cmake_minimum_required(VERSION 3.16)

###########################################################
# Sessionless library dependency
include(FetchContent)
FetchContent_Declare(
  sessionless
  SOURCE_DIR     "${CMAKE_CURRENT_LIST_DIR}/../lib"

  # For this example in sessionless repo is in the same directory
  # For an external executable fetch content from the sessionless
  # git repo using options below instead of SOURCE_DIR
  # GIT_REPOSITORY git@github.com:planet-nine-app/sessionless.git
  # GIT_TAG        main
  # SOURCE_SUBDIR  src/cpp/client/lib
)
FetchContent_MakeAvailable(sessionless)


###########################################################
# Example executable
set(TARGET example)
project(${TARGET})
set(CMAKE_CXX_STANDARD 17)
add_executable(${TARGET})
target_sources(${TARGET} PRIVATE main.cpp)
target_link_libraries(${TARGET} PRIVATE sessionless)
