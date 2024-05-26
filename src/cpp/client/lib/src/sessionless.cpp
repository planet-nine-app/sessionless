#include "sessionless.hpp"
#include <secp256k1.h>
#include <random>
#include <ctime>

// Creating context is expensive, create one global context
static secp256k1_context *ctx = secp256k1_context_create(SECP256K1_CONTEXT_NONE);

bool sessionless::generateKeys(Keys &keys)
{
    unsigned char seed[SHA256_SIZE_BYTES] = {};

    // TODO: This isn't cryptographically secure randomness. Do not ship this
    srand(time(NULL));
    for (int i = 0; i < SHA256_SIZE_BYTES; i++)
    {
        seed[i] = rand() % 256;
        keys.privateKey[i] = rand() % 256;
    }

    if (!secp256k1_context_randomize(ctx, seed))
    {
        return false;
    }

    if (!secp256k1_ec_seckey_verify(ctx, keys.privateKey))
    {
        return false;
    }

    secp256k1_pubkey publicKeyStruct;
    if (!secp256k1_ec_pubkey_create(ctx, &publicKeyStruct, keys.privateKey))
    {
        return false;
    }

    size_t len = PUBLIC_KEY_SIZE_BYTES;
    secp256k1_ec_pubkey_serialize(ctx, keys.publicKey, &len,
                                  &publicKeyStruct, SECP256K1_EC_COMPRESSED);
    if (len != PUBLIC_KEY_SIZE_BYTES)
    {
        return false;
    }

    return true;
};

bool sessionless::sign(const unsigned char *message,
                       const size_t msgLengthBytes,
                       const unsigned char *privateKey,
                       unsigned char *signature)
{
    secp256k1_ecdsa_signature ecdsa_signature;
    unsigned char msgHash[SHA256_SIZE_BYTES];
    unsigned char tag[12] = "sessionless";

    int ret = secp256k1_tagged_sha256(ctx, msgHash, tag, sizeof(tag), message, msgLengthBytes);

    if (!secp256k1_ecdsa_sign(ctx, &ecdsa_signature, msgHash, privateKey, NULL, NULL))
    {
        return false;
    }

    secp256k1_ecdsa_signature_serialize_compact(ctx, signature, &ecdsa_signature);

    return true;
};

bool sessionless::verifySignature(const unsigned char *signature,
                                  const unsigned char *publicKey,
                                  const unsigned char *message,
                                  const size_t msgLengthBytes)
{
    unsigned char msgHash[SHA256_SIZE_BYTES];
    const unsigned char tag[12] = "sessionless";

    int ret = secp256k1_tagged_sha256(ctx, msgHash, tag, sizeof(tag), message, msgLengthBytes);

    secp256k1_pubkey pubkey;
    if (!secp256k1_ec_pubkey_parse(ctx, &pubkey, publicKey, PUBLIC_KEY_SIZE_BYTES))
    {
        return false;
    }

    secp256k1_ecdsa_signature sig;
    if (!secp256k1_ecdsa_signature_parse_compact(ctx, &sig, signature))
    {
        return false;
    }

    if (!secp256k1_ecdsa_verify(ctx, &sig, msgHash, &pubkey))
    {
        return false;
    }

    return true;
};
