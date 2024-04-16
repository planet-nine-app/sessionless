#ifndef INCLUDE_SESSIONLESS_HPP
#define INCLUDE_SESSIONLESS_HPP

#include <string>
#include <iostream>
#include <functional>

using namespace std;

struct SessionlessKeys
{
  string public_key;
  string private_key;
};

struct Signature
{
  const string signature;
};

class Sessionless
{
public:
  using GetKeys = SessionlessKeys (*)();
  // SessionlessInterface * makeSessionless();
  Sessionless(GetKeys getKeys) : getKeys_(getKeys) {}
  SessionlessKeys generateKeys();
  SessionlessKeys getKeys();
  Signature sign(string &message);
  int verifySignature(const string &signature, string &message, const string &publicKey);
  string generateUUID();
  // virtual ~SessionlessInterface();

private:
  GetKeys getKeys_;
};

#endif
