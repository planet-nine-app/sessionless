#include "sessionless.hpp"
#include <iostream>
#include <iomanip>
#include <string>
#include <sstream>

std::string toHexString(const unsigned char *buffer, const size_t length)
{
    std::stringstream ss;
    ss << std::hex;

    for (auto i = 0; i < length; ++i)
    {
        // Format for zero padded hex string
        ss << std::setw(2)
           << std::setfill('0')
           << (int)buffer[i];
    }
    return ss.str();
}

int main(int argc, char *argv[])
{
    std::cout << "Sessionless demo..." << std::endl;

    Keys keys;
    if (!sessionless::generateKeys(keys))
    {
        std::cout << "Failed to generate keys" << std::endl;
        return -1;
    }

    std::cout << "public key -> "
              << toHexString(keys.publicKey, sizeof(keys.publicKey)) << std::endl;
    std::cout << "private key -> "
              << toHexString(keys.privateKey, sizeof(keys.privateKey)) << std::endl;

    const char *msg = "Here is a message";
    unsigned char signature[SIGNATURE_SIZE_BYTES];
    if (!sessionless::sign((unsigned char *)msg, sizeof(msg), keys.privateKey, signature))
    {
        std::cout << "Failed to sign message" << std::endl;
        return -1;
    }

    std::cout << "Message -> " << msg << std::endl
              << "Message signature -> " << toHexString(signature, sizeof(signature)) << std::endl;

    std::cout << "Verifying signature..." << std::endl;
    if (!sessionless::verifySignature(signature, keys.publicKey, (unsigned char *)msg, sizeof(msg)))
    {
        std::cout << "Failed to verify signature" << std::endl;
        return -1;
    }

    std::cout << "Signature verified" << std::endl;
    return 0;
}
