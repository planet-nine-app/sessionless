#ifndef INCLUDE_SESSIONLESS_HPP
#define INCLUDE_SESSIONLESS_HPP

#include <string>
#include <iostream>
#include <functional>

using namespace std;

struct SessionlessKeys {
  const char *public_key;
  const char *private_key;
};

struct Signature {
  const char *signature;
};

class Sessionless
{
public:
  using GetKeys = SessionlessKeys (*)();
  //SessionlessInterface * makeSessionless();
  Sessionless(GetKeys getKeys) : getKeys_(getKeys) {}
  SessionlessKeys generateKeys();
  SessionlessKeys getKeys();
  Signature sign(char *message);
  int verifySignature(const char *signature, char *message, const char *publicKey);
  char *generateUUID();
  //virtual ~SessionlessInterface();

private: 
  GetKeys getKeys_;
};

#endif




