# Sessionless C/C++ Library

## Dependencies
* [secp256k1](https://github.com/bitcoin-core/secp256k1)

  > _**NOTE:**_ when using CMake the dependencies are downloaded automatically

## Environment Setup
CMake and a C++ compiler are required.

### Additional Setup for Building on Raspberry Pi Pico

If you are building for the Raspberry Pi Pico, you need to set up the following additional dependencies:

1. **Install Python3:**
  - Download and install Python3 from [python.org](https://www.python.org/downloads/).

2. **Install ARM GCC Compiler:**
  - Download and install the ARM GCC compiler. You can get it from the [Arm Developer website](https://developer.arm.com/tools-and-software/open-source-software/developer-tools/gnu-toolchain/gnu-rm).

3. **Set Up the Pico SDK:**
  - Clone the Pico SDK repository:
    ```sh
    git clone -b master https://github.com/raspberrypi/pico-sdk.git
    ```

4. **Set Environment Variables:**
  - Set the `PICO_SDK_PATH` environment variable to point to the location where you cloned the Pico SDK.
  - Ensure the ARM GCC compiler and Python3 are in your system's PATH.

   Example for setting environment variables on Windows:
    ```sh
    set PICO_SDK_PATH=C:\path\to\pico-sdk
    set PATH=%PATH%;C:\path\to\arm-gcc\bin;C:\path\to\python3
    ```

   Example for setting environment variables on Unix-like systems:
    ```sh
    export PICO_SDK_PATH=/path/to/pico-sdk
    export PATH=$PATH:/path/to/arm-gcc/bin:/path/to/python3
    ```

## Build the Demo

### Standard Build
```shell
cmake -B ./build -S .
cd build
make
