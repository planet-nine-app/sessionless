#include "hash_types.hpp"
#include "keccak.hpp"
#include "sessionless.h"
#include "secp256k1.h"
#include "secp256k1_preallocated.h"
#include <stdio.h>
#include <string.h>
#include <random>
#include <ctime>
#include "Crypto.h"
#include "SHA3.h"
#include "keccak.h"
#include "hash_types.h"


// Creating context is expensive, create one global context
//static secp256k1_context *ctx = secp256k1_context_create(SECP256K1_CONTEXT_NONE);

//static secp256k1_context * ctx = NULL;
static secp256k1_context *ctx = secp256k1_context_create(SECP256K1_CONTEXT_VERIFY | SECP256K1_CONTEXT_SIGN);


//bool sessionless::generateKeys(Keys &keys)
int sessionless::generateKeys(Keys &keys)
{
    /*unsigned char seed[SHA256_SIZE_BYTES] = {};

    // TODO: This isn't cryptographically secure randomness. Do not ship this
    srand(time(NULL));

    for (int i = 0; i < SHA256_SIZE_BYTES; i++)
    {
        seed[i] = rand() % 256;
        keys.privateKey[i] = rand() % 256;
    }

    if (!secp256k1_context_randomize(ctx, seed))
    {
      printf("here");
      return 1;
        //return false;
    }*/

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
      printf("here2");
      return 2;
        //return false;
    }


    secp256k1_pubkey publicKeyStruct;

    if (!secp256k1_ec_pubkey_create(ctx, &publicKeyStruct, keys.privateKey))
    {
            return 1;

      printf("here3");
        //return false;
    }
                      
return 3;


    size_t len = PUBLIC_KEY_SIZE_BYTES;
    secp256k1_ec_pubkey_serialize(ctx, keys.publicKey, &len,
                                  &publicKeyStruct, SECP256K1_EC_COMPRESSED);
    if (len != PUBLIC_KEY_SIZE_BYTES)
    {
      printf("here4");
      return 4;
        //return false;
    }

    //return true;*/
    return 5;
};

bool sessionless::sign(const unsigned char *message,
                       const size_t msgLengthBytes,
                       const unsigned char *privateKey,
                       unsigned char *signature)
{
    //size_t context_size = secp256k1_context_preallocated_size(SECP256K1_CONTEXT_VERIFY | SECP256K1_CONTEXT_SIGN);

    //ctx = secp256k1_context_create(SECP256K1_CONTEXT_VERIFY | SECP256K1_CONTEXT_SIGN);
    
    secp256k1_ecdsa_signature ecdsa_signature;
    //unsigned char tag[12] = "sessionless";

    //int ret = secp256k1_tagged_sha256(ctx, msgHash, tag, sizeof(tag), message, msgLengthBytes);
    /*uint8_t bytes[sizeof(message) - 1];
    for(int i = 0; i < sizeof(message - 1); i++) {
      bytes[i] = uint8_t(message[i]);
    }*/

    std::string str = "Here is a message";
    const uint8_t *bytes = reinterpret_cast<const uint8_t*>(str.data());

    SHA3_256 sha3_256;
    Hash *hash = &sha3_256;
    unsigned char hashish[32];
    size_t hashSize = 32;
    hash->reset();
    hash->update(bytes, str.size());
    //hash->update(message, sizeof(message));
    hash->finalize(hashish, hashSize);

    ethash::hash256 keccak = ethash::keccak256(bytes, str.size());
    //msgHash = hashish;

    //const unsigned char *foo = keccak.bytes;
    unsigned char foo[32];
    for(int i = 0; i < 32; i++) {
      foo[i] = keccak.bytes[i];
    }

    uint8_t *nonce {};

    //if (!secp256k1_ecdsa_sign(ctx, &ecdsa_signature, msgHash, privateKey, NULL, NULL))
    //if (!secp256k1_ecdsa_sign(ctx, &ecdsa_signature, hashish, privateKey, NULL, NULL))
    //if (!secp256k1_ecdsa_sign(ctx, &ecdsa_signature, keccak.bytes, privateKey, secp256k1_nonce_function_rfc6979, NULL))
    //if (!secp256k1_ecdsa_sign(ctx, &ecdsa_signature, foo, privateKey, secp256k1_nonce_function_rfc6979, nonce))
    if (!secp256k1_ecdsa_sign(ctx, &ecdsa_signature, foo, privateKey, secp256k1_nonce_function_default, nonce))
    {
        return false;
    }

    //secp256k1_ecdsa_signature normalized_ecdsa_signature;

    //secp256k1_ecdsa_signature_normalize(ctx, &normalized_ecdsa_signature, &ecdsa_signature);


    secp256k1_ecdsa_signature_serialize_compact(ctx, signature, &ecdsa_signature);
    //secp256k1_ecdsa_signature_serialize_compact(ctx, signature, &normalized_ecdsa_signature);

    //for(int i = 0; i < sizeof(hashish); i++) {
      //signature[i] = hashish[i];
      //signature[i + sizeof(msgHash)] = hashish[i];
    //}
   //uint8_t byteArray[str.size()];
    //memcpy(byteArray, bytes, str.size());

    //uint8_t byteArray[32];
    //memcpy(byteArray, keccak.bytes, sizeof(keccak.bytes));
    //for(int i = 0; i < 32; i++) {
      //signature[i] = keccak.bytes[i];
    //}

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
