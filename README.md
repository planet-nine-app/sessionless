### Overview

This repository contains implementations in various languages, and on various platforms of the Sessionless authentication protocol. Sessionless uses the cryptography employed by bitcoin and ethereum to authenticate messages sent between a client and a server. A private key is created and stored on the client, and then used to sign messages, which are then verified by the server via the public key associated with the client. Because this message verifying proves the provinence of the message, no other secret need be shared thus removing the need for sessions. 

It is a practical implementation of delegatable anonymous credentials as described [here](https://www.microsoft.com/en-us/research/wp-content/uploads/2009/08/anoncred.pdf), [here](https://www.planetnineapp.com/digital-identity-for-smart-cities), and [here](https://www.sciencedirect.com/science/article/abs/pii/S1389128623005315).

### Getting Started

This repo is organized by language, and is a work in progress. The APIs for client and server are defined below. If you'd like to contribute see the section at the bottom of this README. If you want to implement this system in your app, you'll likely want to start at your language's package manager (npm, cocoapods, maven, etc). Links for those can be found at the end of this doc. 

The below is written in JavaScript, and is the case for a primary sessionless system. For examples in your language of choice, check the README in the language directory, or the documentation on the package manager's page. 

A note on clients. One of the appeals of Sessionless is that it works on any device that can safely store a secret (yes no client can safely store a secret, but we store sessions all over the place in clients, and in practice there's no real difference in the _storage_ of a session or private key. The benefit of the Sessionless private key is it never has to leave the device, where as a session does. See [is Sessionless secure?](https://github.com/planet-nine-app/sessionless/blob/main/docs/Is%20Sessionless%20Secure.md) for more details). Because Sessionless lets you pass messages through other devices, it can even be used to authenticate devices that aren't connected to the internet. All of this doesn't work on web though, because on web the only "safe" place to store data is in the _server_ generated cookie. To use Sessionless with web you'll want to replace some part of your session with the private key for the user, and do the signing on your server when a client makes a request. Since cookie-based auth has a lot of large entrenched players it may take some time before this functionality works out of the box. The maintainers of Sessionless plan on making this as seemless as possible, but recognize it's an uphill battle. 

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
  timestamp: new Date().gettime() // Replay attacks are the only real attack surface for sessionless requests once they leave the client
                                  // so giving them a short ttl is suggested. Sessionless doesn't enforce this today, but it might in the future.
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

Private key recovery is important, but in a primary system you have options. You could implement username and password recovery, or SSO, or private key cold storage, or simply not care about persisting a user beyond a single device. 

### Secondary Systems

The real power of sessionless comes from the fact that since no secret is shared with every API call, calls can be passed off to untrusted devices without fear of credentials getting stolen. This means we can combine messages into one, and through that mechanism we can associate a public key in a secondary system with a user in a primary system. 

### A Note On Primary And Secondary Systems And Why Sessionless Is Different

There are a few distributed identity approaches out there, but to my knowledge (and please let me know if I'm wrong since this stuff fascinates me), they're all trying to be a system where you have a singular canonical identity that the other identities tie into. Sessionless doesn't try to do that. The primary/secondary distinction is mostly one regarding whether the system provides account recovery of its own. In fact it's very possible that one primary system may be a secondary system to another primary system and vice versa. 

It's kind of like the matrix where you need a phone to get in or out. The primary systems are your phones where you enter the system, and where you can remove yourself through revokation. The secondary systems just let you do Sessionless things once you're in.

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

`associateKeys(associationMessage: String, primarySignature: String, secondarySignature: String, publicKey: String)`: associates a secondary's key with the user's primary key.

`revokeKey(message: String, signature: String, publicKey: String)`: Revokes a gateway's key from the user.

### Contributing

To add to this repo, feel free to make a pull request. The following criteria will be used to determine whether to mergeor not:

* Is the crypto library used well known for that language and actively maintained?
* Has the README been updated for that specific language?

### Further Reading

* [What makes cryptography hard, and how does Sessionless make it easier?](https://github.com/planet-nine-app/sessionless/blob/main/docs/Cryptography.md)
* [What makes this an authentication/identity system?](https://github.com/planet-nine-app/sessionless/blob/main/docs/Authentication%20and%20Identity.md)
* [How does this work, and why should I trust it?](https://github.com/planet-nine-app/sessionless/blob/main/docs/How%20does%20this%20work.md)
* [What's the primary/secondary thing (read this for how this relates to OAuth2.0)?](https://github.com/planet-nine-app/sessionless/blob/main/docs/Primary%20and%20Secondary.md)
* [Is this secure?](https://github.com/planet-nine-app/sessionless/blob/main/docs/Is%20Sessionless%20Secure.md)
* [How does this compare to Web 3.0?](https://github.com/planet-nine-app/sessionless/blob/main/docs/Web%203.md)
