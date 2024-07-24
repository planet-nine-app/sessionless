#include "hash_types.hpp"
#include "keccak.hpp"
#include "sessionless.h"
#include "secp256k1.h"
#include "secp256k1_preallocated.h"
#include <stdio.h>
#include <string.h>
#include <random>
#include <ctime>
#include "keccak.h"
#include "hash_types.h"


// Creating context is expensive, create one global context
static secp256k1_context *ctx = secp256k1_context_create(SECP256K1_CONTEXT_VERIFY | SECP256K1_CONTEXT_SIGN);


bool sessionless::generateKeys(Keys &keys)
{
    size_t context_size = secp256k1_context_preallocated_size(SECP256K1_CONTEXT_VERIFY | SECP256K1_CONTEXT_SIGN);

    ctx = secp256k1_context_create(SECP256K1_CONTEXT_VERIFY | SECP256K1_CONTEXT_SIGN);

    unsigned char seed[SHA256_SIZE_BYTES] = {};

    // TODO: This isn't cryptographically secure randomness. Do not ship this
    srand(time(NULL));

    for (int i = 0; i < SHA256_SIZE_BYTES; i++)
    {
        seed[i] = rand() % 256;
        keys.privateKey[i] = rand() % 256;
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

bool sessionless::sign(std::string message,
                       const unsigned char *privateKey,
                       unsigned char *signature)
{
    secp256k1_ecdsa_signature ecdsa_signature;

    const uint8_t *bytes = reinterpret_cast<const uint8_t*>(message.data());

    ethash::hash256 keccak = ethash::keccak256(bytes, message.size());
  
    unsigned char bytesAsChar[32];
    for(int i = 0; i < 32; i++) {
      bytesAsChar[i] = keccak.bytes[i];
    }

    uint8_t *nonce {};

    if (!secp256k1_ecdsa_sign(ctx, &ecdsa_signature, bytesAsChar, privateKey, secp256k1_nonce_function_default, nonce))
    {
        return false;
    }

    secp256k1_ecdsa_signature_serialize_compact(ctx, signature, &ecdsa_signature);

    return true;
};

bool sessionless::verifySignature(const unsigned char *signature,
                                  const unsigned char *publicKey,
                                  std::string message)
{
    const uint8_t *bytes = reinterpret_cast<const uint8_t*>(message.data());

    ethash::hash256 keccak = ethash::keccak256(bytes, message.size());
  
    unsigned char bytesAsChar[32];
    for(int i = 0; i < 32; i++) {
      bytesAsChar[i] = keccak.bytes[i];
    }

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

    if (!secp256k1_ecdsa_verify(ctx, &sig, bytesAsChar, &pubkey))
    {
        return false;
    }

    return true;
};
