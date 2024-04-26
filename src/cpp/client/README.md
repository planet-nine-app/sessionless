# Sessionless C/C++

## Dependencies 
* [secp256k1](https://github.com/bitcoin-core/secp256k1)

## Evironment setup 
CMake and C++ compiler are required.

## Build the demo
When using CMake this will download the dependencies and build the demo.
```shell
mkdir build
cmake -B ./build -S .
cd build
make
```

## Run the demo
```shell
cd build
./sessionless
```