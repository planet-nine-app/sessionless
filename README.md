### Overview

This repository contains implementations in various languages, and on various platforms of the sessionless authentication protocol. Sessionless uses the cryptography employed by bitcoin and ethereum to authenticate messages sent between a client and a server. A private key is created and stored securely on the client, and then used to sign messages, which are then verified by the client via the public key associated with the client. Because this message verifying proves the provinence of the message, no other secret need be shared thus removing the need for sessions. 

### Getting Started

This repo is organized by language, and is a work in progress. The APIs for client and server are defined here <add link>, and see below for how to contribute. If you want to implement this system in your app, you'll likely want to start at your languages package manager (npm, cocoapods, maven, etc). Links for those can be found at the end of this doc. 

The below is written in JavaScript, and is the case for a primary sessionless system. For examples in your language of choice, check the README in the language directory, or the documentation on the package manager's page. 

#### Client

On the client you will first want to:

```
import sessionless from 'sessionless';

sessionless.generateKeys();
```

Then when it comes time to register a new user:

```
const publicKey = sessionless.getKeys().publicKey;
const payload = {
  publicKey
};
payload.signature = sessionless.sign(JSON.stringify(payload));
const user = await registerUser(payload); // left to the implementer
saveUser(user); // how you save the user is up to you, and not part of sessionless
```

For your api calls you'll want to add a signature:
```
let payload = {
  foo: 'bar'
};

payload.signature = sessionless.sign(JSON.stringify(payload));
```

#### Server

On the server, registering a user creates a tuple with a UUID and a publicKey, which will need to be persisted.
Subsequent calls from sessionless will only include the UUID. Persistence and retrieval is left to the implementer.

Registering a new user:

```
const payload = requestFromClient.payload;
const signature = payload.signature;

const publicKey = payload.publicKey;

const message = getMessageForPayload(payload); // This is left to the implementer. getMessage should return the message signed on the client.

if(sessionless.verifySignature(message, signature, publicKey)) {
  const uuid = sessionless.generateUUID();
  saveUser(uuid, publicKey); // saveUser left to the implementer
  const user = {
    uuid, 
    anythingElse
  };
  respondToClient(user); // left to the implementer
}
```

```
const payload = requestFromClient.payload;
const signature = payload.signature;

const publicKey = getUserPublicKey(payload.UUID); // This is left to the implementer

const message = getMessageForPayload(payload); // This is left to the implementer. getMessage should return the message signed on the client.

if(sessionless.verifySignature(message, signature, publicKey)) {
  doCoolStuff();  // This left to the implementer
}
```

Private key management is important, but in a primary system you have options. You could implement username and password recovery, or SSO, or private key cold storage, or simply not care about persisting a user beyond a single device. 

### Secondary Systems

The real power of sessionless comes from the fact that since no secret is shared with every API call, calls can be passed off to untrusted devices without fear of credentials getting stolen. This means we can combine messages into one, and through that mechanism we can associate a public key in a secondary system with a user in a primary system. 

On a client:

```
const messageFromPrimary = receivedMessage;

const myMessage = {
  publicKey
};

myMessage.signature = sessionles.sign(JSON.stringify(myMessage));

const bothMessages = {
  messageFromPrimary,
  myMessage
};

sendMessageToPrimary(bothMessages);
```

Associating a public key with a primary system gives the secondary system the ability to interact with the primary without additional login or sharing of credentials. If the primary system also returns a UUID then that secondary system can use that as authentication for their system. It could even share that public key with a third system, and make authenticated calls there _without any additional integration_. 

### Message Passing

One of the really interesting features of this protocol is since no sensitive data need be passed for any network call, signed (that is authenticated) messages can be sent via or through untrusted machines. That means you can build a platform where users can make API calls via a second user, and that second user can be notified of the result. For an example of a system that does this please read here <include link to MAGIC>.

### API

At its core, sessionless is a loose wrapper around the secp256k1 elliptic curve used by Bitcoin and Ethereum. Though the cryptography itself is hard, the methods we need to use to use it are just a few. Since this is a multi-language repo, the below is written in pseudocode. For language-specific typing please refer to the README's in the language directories themselves. 

`generateKeys(saveKeys?: keys => void, getKeys?: () => Keys)`: generates a private/public keypair and stores it in the platform's secure storage. Takes an optional save keys function for platforms that don't have clear cut secure storage. 

`getKeys()`: gets keys from secure storage

`sign(message: String)`: signs a message with the user's private key 

`verifySignature(message: String, signature: String, publicKey: String)`: verifies a given signature with a public key

`generateUUID()`: creates a unique UUID for a user 

`associateKeys(associationMessage: String, primarySignature: String, secondarySignature: String, publicKey: String)`: associates a gateway's key with the user's primary key.

`revokeKey(message: String, signature: String, publicKey: String)`: Revokes a gateway's key from the user.

### Contributing

To add to this repo, feel free to make a pull request. The following criteria will be used to determine whether to mergeor not:

* Are there comprehensive tests for all client and server code that covers the five APIs?
* Is the crypto library used well known for that language and actively maintained?
* Has the README been updated for that specific language?

### Further Reading

* [What makes this an authentication/identity system?](https://github.com/planet-nine-app/sessionless/blob/main/docs/Authentication%20and%20Identity.md)
* [How does this work, and why should I trust it?](https://github.com/planet-nine-app/sessionless/blob/main/docs/How%20does%20this%20work.md)
* [What's the primary/secondary thing (read this for how this relates to OAuth2.0)?](https://github.com/planet-nine-app/sessionless/blob/main/docs/Is%20Sessionless%20Secure.md)
* [Is this secure?](https://github.com/planet-nine-app/sessionless/blob/main/docs/Primary%20and%20Secondary.md)
* [How does this compare to Web 3.0?](https://github.com/planet-nine-app/sessionless/blob/main/docs/Web%203.md)
