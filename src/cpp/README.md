### Overview

The c++ implementation of Sessionless wraps the c bitcoin-core secp256k1 library.
It has been tested on Mac, and Linux (and Arduino--coming soon). 
c++ doesn't really have a package manager, so you'll want to include sessionless from git.
Check the CMakeLists.txt in src/cpp/client/example for instructions on how to do this.

### Usage

If you somehow get here before I update c++, it's not quite up to the protocol spec yet since it uses sha256 to hash messages instead of keccak256.
The update is hanging out up on the `arduino` branch, and should be merged soon.
