#ifndef SESSIONLESS_H
#define SESSIONLESS_H

#include <cstddef>
#include <stdint.h>

#define SHA256_SIZE_BYTES 32
#define PRIVATE_KEY_SIZE_BYTES SHA256_SIZE_BYTES
#define PUBLIC_KEY_SIZE_BYTES 33
#define SIGNATURE_SIZE_BYTES 64

struct Keys
{
    uint8_t publicKey[PUBLIC_KEY_SIZE_BYTES];
    uint8_t privateKey[PRIVATE_KEY_SIZE_BYTES];
};

namespace sessionless
{
    //bool generateKeys(Keys &keys);
    int generateKeys(Keys &keys);

    bool sign(const unsigned char *message,
              const size_t msgLengthBytes,
              const unsigned char *privateKey,
              unsigned char *signature);

    bool verifySignature(const unsigned char *signature,
                         const unsigned char *publicKey,
                         const unsigned char *message,
                         const size_t msgLengthBytes);
};

#endif
