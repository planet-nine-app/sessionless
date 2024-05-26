#ifndef SESSIONLESS_HPP
#define SESSIONLESS_HPP

#include <cstddef>

#define SHA256_SIZE_BYTES 32
#define PRIVATE_KEY_SIZE_BYTES SHA256_SIZE_BYTES
#define PUBLIC_KEY_SIZE_BYTES 33
#define SIGNATURE_SIZE_BYTES 64

struct Keys
{
    unsigned char publicKey[PUBLIC_KEY_SIZE_BYTES];
    unsigned char privateKey[PRIVATE_KEY_SIZE_BYTES];
};

namespace sessionless
{
    bool generateKeys(Keys &keys);

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
