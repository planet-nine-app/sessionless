# Sessionless

This is the node implementation of the Sessionless protocol--a protocol for providing auth without user data like emails, passwords, or sessions.
For examples of various server implementations, please see [here][examples].

### Usage

Start by installing sessionless-node:

`npm i sessionless-node --save`

sessionless-node provides both commonjs and esm modules so you can:

`const sessionless = require('sessionless-node');`

and 

`import sessionless from 'sessionless-node';`

Sessionless-node provides six methods:

* `generateKeys(saveKeys, getKeys)` - generates a new key pair, and returns them to the caller. 
saveKeys and getKeys are functions sessionless-node will use to save keys, and get keys for signing.

* `getKeys()` - calls the `getKeys` function passed to `generateKeys`

* `sign(message)` - signs an arbitrary message with the saved private key

* `verifySignature(signature, message, pubKey)` - returns true if the passed in public key verifies the signature of the passed in message.

* `generateUUID()` - generates and returns a new uuid

* `associate(primarySignature, primaryMessage, primaryPublicKey, secondarySignature, secondaryMessage, secondaryPublicKey)` - a convenience method provided for associating keys together. 
For more on associating keys check out [this section of the dev README][associate].

For more info, check out the docs [in the repo][readme-dev].

[readme-dev]: https://github.com/planet-nine-app/README-DEV.md
[examples]: https://github.com/planet-nine-app/sessionless
[associate]: https://github.com/planet-nine-app/sessionless/blob/main/README-DEV.md#primary-and-secondary-systems-and-why-sessionless-is-different
