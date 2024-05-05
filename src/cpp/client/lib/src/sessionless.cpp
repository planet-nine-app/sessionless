#include "sessionless.hpp"
#include <secp256k1.h>
#include <random>
#include <ctime>
#include <iostream>

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

    secp256k1_context *ctx = secp256k1_context_create(SECP256K1_CONTEXT_NONE);

    if (!secp256k1_context_randomize(ctx, seed))
    {
        std::cout << "Failed to randomize context" << std::endl;
        return false;
    }

    if (!secp256k1_ec_seckey_verify(ctx, keys.privateKey.data()))
    {
        std::cout << "Failed to verify secret key" << std::endl;
        return false;
    }

    secp256k1_pubkey publicKeyStruct;
    if (!secp256k1_ec_pubkey_create(ctx, &publicKeyStruct, keys.privateKey.data()))
    {
        std::cout << "Failed to create public key" << std::endl;
        return false;
    }

    size_t len = keys.publicKey.size();
    secp256k1_ec_pubkey_serialize(ctx, keys.publicKey.data(), &len,
                                  &publicKeyStruct, SECP256K1_EC_COMPRESSED);
    if (len != PUBLIC_KEY_SIZE_BYTES)
    {
        std::cout << "Invalid public key size" << std::endl;
        return false;
    }

    return true;
};

bool sessionless::sign(const unsigned char *message,
                       const size_t length,
                       const PrivateKey privateKey,
                       Signature &signature)
{
    secp256k1_ecdsa_signature ecdsa_signature;
    unsigned char msg_hash[SHA256_SIZE_BYTES];
    unsigned char tag[12] = "sessionless";

    secp256k1_context *ctx = secp256k1_context_create(SECP256K1_CONTEXT_NONE);

    int ret = secp256k1_tagged_sha256(ctx, msg_hash, tag, sizeof(tag), message, length);

    if (!secp256k1_ecdsa_sign(ctx, &ecdsa_signature, msg_hash, privateKey.data(), NULL, NULL))
    {
        std::cout << "Failed ecdsa sign" << std::endl;
        return false;
    }

    secp256k1_ecdsa_signature_serialize_compact(ctx, signature.data(), &ecdsa_signature);

    return true;
};

bool sessionless::verifySignature(Signature signature,
                                  PublicKey publicKey,
                                  const unsigned char *message,
                                  const size_t length)
{
    secp256k1_context *ctx = secp256k1_context_create(SECP256K1_CONTEXT_NONE);
    unsigned char msg_hash[SHA256_SIZE_BYTES];
    const unsigned char tag[12] = "sessionless";

    int ret = secp256k1_tagged_sha256(ctx, msg_hash, tag, sizeof(tag), message, length);

    secp256k1_pubkey pubkey;
    if (!secp256k1_ec_pubkey_parse(ctx, &pubkey, publicKey.data(), publicKey.size()))
    {
        std::cout << "Failed to parse public key" << std::endl;
        return false;
    }

    secp256k1_ecdsa_signature sig;
    if (!secp256k1_ecdsa_signature_parse_compact(ctx, &sig, signature.data()))
    {
        std::cout << "Failed to parse signature" << std::endl;
        return false;
    }

    if (!secp256k1_ecdsa_verify(ctx, &sig, msg_hash, &pubkey))
    {
        std::cout << "Signature verification failed" << std::endl;
        return false;
    }

    return true;
};
