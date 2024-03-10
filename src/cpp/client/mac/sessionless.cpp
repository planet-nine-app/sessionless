#include <cstdio>
#include <string>
#include <random>
#include <bitset>
#include <iostream>
#include <sstream>
#include <iomanip>
#include "sessionless.hpp"

#include <secp256k1.h>

using namespace std;

SessionlessKeys Sessionless::generateKeys() {

  secp256k1_context* ctx = secp256k1_context_create(SECP256K1_CONTEXT_NONE);

  unsigned char private_key[32];
  unsigned char randomize[32];
  secp256k1_context *random_ctx;
  secp256k1_pubkey public_key;
  unsigned char compressed_public_key[33];
  int return_val;
  size_t len;

//TODO: This isn't cryptographically secure randomness. Do not ship this
  for(size_t i = 0; i < 32; i++) {
    randomize[i] = rand() % 256;
    private_key[i] = rand() % 256;
  }
 

  return_val = secp256k1_context_randomize(ctx, randomize);

  while (1) {
	  if (!private_key) {
		  printf("Failed to generate randomness\n");
      SessionlessKeys keys;
      keys.public_key = "error";
      keys.private_key = "error";
		return keys;
	    }
	  if (secp256k1_ec_seckey_verify(ctx, private_key)) {
		  break;
	  }
	}

  return_val = secp256k1_ec_pubkey_create(ctx, &public_key, private_key);

  len = sizeof(compressed_public_key);
  return_val = secp256k1_ec_pubkey_serialize(ctx, compressed_public_key, &len, &public_key, SECP256K1_EC_COMPRESSED);

  printf("public");
      //print_hex(compressed_public_key, sizeof(compressed_public_key));
  printf("private\n");
      //print_hex(private_key, sizeof(private_key));

    SessionlessKeys keys;
    stringstream stream;
    for(int i = 0; i < 33; ++i)
      stream << hex << (int)compressed_public_key[i];

    printf(stream.str().c_str());
    const string tmp = stream.str();   
    const char* publicKey = tmp.c_str();
    printf("\n");
    printf(publicKey);
    keys.public_key = publicKey;
    
    stringstream pstream;
    for(int i = 0; i < 32; ++i)
      pstream << hex << (int)private_key[i];

    printf("\n");
    printf(pstream.str().c_str());
    printf("\n");
    const char *privateKey = pstream.str().c_str();
    keys.private_key = privateKey;

    char *pubKey = new char[33];
    char *privKey = new char[32];

    strcpy(pubKey, publicKey);
    strcpy(privKey, privateKey);

  return {pubKey, privKey};
};

SessionlessKeys Sessionless::getKeys() {
  return getKeys_();
}

Signature Sessionless::sign(char *message) {
  secp256k1_context* ctx = secp256k1_context_create(SECP256K1_CONTEXT_NONE);
  int return_val;
  secp256k1_ecdsa_signature signature;
  unsigned char msg_hash[32];
  unsigned char tag[12] = "sessionless";
  unsigned char serialized_signature[64];
  const unsigned char *castedMessage = (const unsigned char *)message;

  SessionlessKeys keys = getKeys_();

  return_val = secp256k1_tagged_sha256(ctx, msg_hash, tag, sizeof(tag), castedMessage, sizeof(castedMessage));

  return_val = secp256k1_ecdsa_sign(ctx, &signature, msg_hash, (const unsigned char *)keys.private_key, NULL, NULL);

  return_val = secp256k1_ecdsa_signature_serialize_compact(ctx, serialized_signature, &signature);

  stringstream sigstream;
    for(int i = 0; i < 64; ++i)
      sigstream << hex << (int)serialized_signature[i];

  printf("\n\n");
  printf(sigstream.str().c_str());

  char *sig = new char[64];
  strcpy(sig, sigstream.str().c_str());  

  return {sig};
};

int Sessionless::verifySignature(const char *signature, char *message, const char *publicKey) {
  secp256k1_context* ctx = secp256k1_context_create(SECP256K1_CONTEXT_NONE);
  unsigned char msg_hash[32];
  unsigned char tag[12] = "sessionless";
  const unsigned char *castedMessage = (const unsigned char *)message;
  int return_val;

  return_val = secp256k1_tagged_sha256(ctx, msg_hash, tag, sizeof(tag), castedMessage, sizeof(castedMessage));

  SessionlessKeys keys = getKeys_();

  return secp256k1_ecdsa_verify(ctx, (secp256k1_ecdsa_signature *)signature, msg_hash, (const secp256k1_pubkey *)keys.public_key);;
};
  
char * Sessionless::generateUUID() {

  return "foo";
};

/*struct sessionless_keys {
  string public_key;
  string private_key;
};

class Sessionless : public SessionlessInterface
{
  secp256k1_context* ctx = secp256k1_context_create(SECP256K1_CONTEXT_NONE);

  public:
    SessionlessInterface* makeSessionless() {
      return new Sessionless();
    }

    virtual int generate_keys() {

      unsigned char private_key[32];
      unsigned char randomize[32];
      secp256k1_context *random_ctx;
      secp256k1_pubkey public_key;
      unsigned char compressed_public_key[33];
      int return_val;
      size_t len;

//TODO: This isn't cryptographically secure randomness. Do not ship this
      for(size_t i = 0; i < 32; i++) {
        randomize[i] = rand() % 256;
        private_key[i] = rand() % 256;
      }
 

      return_val = secp256k1_context_randomize(ctx, randomize);

      while (1) {
	    if (!private_key) {
		printf("Failed to generate randomness\n");
		return 1;
	    }
	    if (secp256k1_ec_seckey_verify(ctx, private_key)) {
		break;
	    }
	}

      return_val = secp256k1_ec_pubkey_create(ctx, &public_key, private_key);

      len = sizeof(compressed_public_key);
      return_val = secp256k1_ec_pubkey_serialize(ctx, compressed_public_key, &len, &public_key, SECP256K1_EC_COMPRESSED);

      printf("public");
      //print_hex(compressed_public_key, sizeof(compressed_public_key));
      printf("private");
      //print_hex(private_key, sizeof(private_key));

      return 0;
    }
};

SessionlessInterface* makeSessionless() {
    return new Sessionless();
}*/
