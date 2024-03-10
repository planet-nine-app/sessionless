#include <stdio.h>
#include <string>
#include <iostream>
#include "sessionless.hpp"

using namespace std;

SessionlessKeys keys;

SessionlessKeys getKeys() {
  return keys;
}

int main(int argc, char* argv[]) {
  printf("hello world");
  //SessionlessInterface *interface = new SessionlessInterface();
  //sessionless = interface->makeSessionless();
  //sessionless = makeSessionless();
  Sessionless sessionless(getKeys);
  keys = sessionless.generateKeys();
  Signature signature = sessionless.sign("Here is a message");
  int verified = sessionless.verifySignature((const char *)signature.signature, "Here is a message", sessionless.getKeys().public_key);
  printf("\n\n");
  printf("verified: ");
  cout << to_string(verified);
  printf("\n\nheyoooo\n\n");
  printf(keys.public_key);
  printf("\nfoobar");
}
