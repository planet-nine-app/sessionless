#ifndef SESSIONLESS_HPP
#define SESSIONLESS_HPP

#include <array>
#include <cstddef>

static constexpr size_t SHA256_SIZE_BYTES = 32;
static constexpr size_t PRIVATE_KEY_SIZE_BYTES = SHA256_SIZE_BYTES;
static constexpr size_t PUBLIC_KEY_SIZE_BYTES = 33;
static constexpr size_t SIGNATURE_SIZE_BYTES = 64;

using PublicKey = std::array<unsigned char, PUBLIC_KEY_SIZE_BYTES>;
using PrivateKey = std::array<unsigned char, PRIVATE_KEY_SIZE_BYTES>;
using Signature = std::array<unsigned char, SIGNATURE_SIZE_BYTES>;

struct Keys
{
    PublicKey publicKey;
    PrivateKey privateKey;
};

namespace sessionless
{
    bool generateKeys(Keys &keys);
    bool sign(const unsigned char *message, const size_t length, const PrivateKey privateKey, Signature &signature);
    bool verifySignature(Signature signature, PublicKey publicKey, const unsigned char *message, const size_t length);
};

#endif
