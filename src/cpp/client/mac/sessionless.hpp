#ifndef INCLUDE_SESSIONLESS_HPP
#define INCLUDE_SESSIONLESS_HPP

#include <string>
#include <iostream>
#include <functional>

using namespace std;

struct SessionlessKeys {
  char *public_key = new char[33];
  char *private_key = new char[32];
};

struct Signature {
  char *signature = new char[250];
};

class Sessionless
{
public:
  using GetKeys = SessionlessKeys (*)();
  //SessionlessInterface * makeSessionless();
  Sessionless(GetKeys getKeys) : getKeys_(getKeys) {}
  SessionlessKeys generateKeys(SessionlessKeys keys);
  SessionlessKeys getKeys();
  Signature sign(char *message, Signature signature);
  int verifySignature(const char *signature, char *message, const char *publicKey);
  char *generateUUID();
  //virtual ~SessionlessInterface();

private: 
  GetKeys getKeys_;
};

#endif




