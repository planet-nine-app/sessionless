<div align="center">
    <h1> Sessionless </h1>
    <a href="https://sessionless.org/" aria-label="Visit Sessionless Dot Org">
        <img src="assets/sessionless.svg" alt="Sessionless Logo" width="50%" height="50%"></img>
    </a>
</div>

## Sessionless

Sessionless is an authentication protocol, which can also be referred to as the SessionLess Authentication Protocol (SLAP).
It is meant to be a (the following aren't links, they're on-hover texts) [general purpose][ht1] [transport-agnostic][ht2] [multi-language][ht3] [multi-platform][ht4] authentication protocol with no [shared-secret][ht5] or [personally identifying information][ht6] shared between participants. 

### Getting Started

You will want to checkout the language, and if necessary the platform, you're interested in in the repo for installation steps.
In general, everything is available in the standard package manager system of the given language (i.e. maven for java, and pip for python).

#### Methods

There are four methods that each implementation has.
In addition there are two optional method based on how the platform stores sensitive data.
For the most part these optional methods are only available in client-side platforms like mobile, and consoles.

The methods are as follows (see [API] for more details):

**Client**

* `generateKeys` - generates a private/public key pair on the secp256k1 elliptical curve
* `sign` - creates a signature of a message string with a private key

**Server**

* `verifySignature` - verifies a signature with a public key
* `generateUUID` - generates a uuidv4 uuid

**Optional**

* `getKeys` - on platforms where secure storage is well-defined we've implemented getKeys within Sessionless rather than having it passed into generateKeys.
* `associate` - convenience method for verifying two signatures to associate a public key with another

A typical registration is for a client to `generateKeys`, and then use those keys to `sign` a message, and send to the server.
The server can then `verifySignature` on the payload received from the client, and grant a uuid with `generateUUID`.

#### Languages supported

The following languages are currently supported:

* [JavaScript][javascript]
* [TypeScript][typescript]
* [Rust][rust]
* [Java][java]
* [Kotlin][kotlin]
* [Python][python]
* [Swift][swift]
* [C#][c#]
* [C++][c++]

Of these nine, seven have server examples (sorry vapor, and c++, haven't gotten to you just yet). 
And those server examples can all be run if you've installed every runtime under the sun, and then you can run the rainbow test.

![The rainbow test of all the servers utilizing the same sessionless protocol](https://github.com/planet-nine-app/sessionless/blob/main/rainbowtest.gif)

#### Building with Sessionless

Like https (I use the comparison simply for familiarity, and not try to say Sessionless is on the same tier), Sessionless is a protocol that you likely won't just use directly.
Instead it's meant to be the protocol layer of an authentication _implementation_.
One example implementation can be found [here][here].
More will come, and if you make one feel free to make a pr to link it here.

#### Want to learn more?

There are links to more reading [for devs], and [for ux'ers], and some videos coming soon.

#### A brief note from zach-planet-nine

With Sessionless, and any subesequent open source projects I start, I'm trying to make them accessible to all parts of the tech industry. 
Right now that means focusing on dev, UX, and product, but qa, analytics, customer success, etc. are all welcome.
Trust me, there's plenty of work to go around.

To this end, I'm going to try my best to provide domain-specific documentation as an entry point. 
This is very much a wip, and, as far as I know, not something that there's an established pattern for so all feedback welcome.

| Dev          | UX          | Product       |
|--------------|-------------|---------------|
| [README-DEV] | [README-UX] | [README-PROD] |


[README-DEV]: ./README-DEV.md
[for devs]: ./README-DEV.md
[README-UX]: ./README-UX.md
[for ux'ers]: ./README-UX.md
[README-PROD]: ./README-PROD.md
[API]: ./README-DEV.md#api
[here]: https://www.github.com/planet-nine-app/allyabase
[javascript]: ./src/javascript/README.md
[typescript]: ./src/javascript/README.md
[rust]: ./src/rust/README.md
[java]: ./src/java/README.md
[kotlin]: ./src/kotlin/README.md
[python]: ./src/python/README.md
[swift]: ./src/swift/README.md
[c#]: ./src/csharp/README.md
[c++]: ./src/cpp/README.md

[ht1]: ## "Many auth protocols are client-server, where the client supplies some secret information to authenticate requests.
But there are other authentication needs, such as between processes on one machine, or server-server relationships. 
Sessionless works for all of these."
[ht2]: ## "Many auth protocols rely on https for encryption of tokens and jwts.
Sessionless sends no sensitive data so it can be used through unencrypted transports like BLE, NFC, straight TCP, etc."
[ht3]: ## "Long ago when I talked through this idea with a cryptography expert, he said the biggest barrier to adoption was consistent language support for a given cryptographic approach. 
Luckily bitcoin and ethereum have led to widespread implementation of the secp256k1 elliptical curve. 
If your language of choice isn't here yet, and you can track down secp256k1 in that language, let us know and we'll add it (or feel free to add it yourself!)"
[ht4]: ## "Randomness and storage are the two things to figure out with cryptographic stuff, and those are largely platform dependent. 
So we have typically made one implementation which works for servers in a language, and then other implementations which work for the clients."
[ht5]: ## "A shared secret is anything that is known between a client and a server that, along with an identifier, is used to authenticate a user.
The two most common shared secrets are passwords and sessions"
[ht6]: ## "That's right. Not even email."
