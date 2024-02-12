Before we can answer the question of whether or not Sessionless is secure, we need to get a lay of the land regarding current authentication security. Here's the OWASP page on [session hijacking](https://owasp.org/www-community/attacks/Session_hijacking_attack). Common types of attacks include:

* Predictable session token
* Session Sniffing
* Client-side attacks (XSS, malicious JavaScript Codes, Trojans, etc)
* Man-in-the-middle attack
* Man-in-the-browser attack

Bitcoin suffered from predictable private keys in the beginning because you could make your key from any key phrase so common phrases like "password" or "key" ended up getting stolen. Sessionless uses cryptographically ensured random bytes for its private keys to mitigate this.

Can't do session sniffing because no sensitive data is transmitted over the network.

Unfortunately client-side attacks are always an issue on th client side of things.

Man-in-the-middle attacks don't work because there's no session coming along the network.

Man-in-the-browser attack... just don't use Sessionless in the browser as recommended. 

The second big type of attack on traditional authentication systems is going after the databases where that information is stored. Since things like passwords and sessions need to be stored somewhere, they become centralized in a single location. If someone gets into that location they can get all the passwords for everyone on the platform. 

With Sessionless though there's no need for passwords, and no need for sessions so there is no need for _any_ sensitive data to be stored in a database anywhere. It's possible to build a platform with no actual user data to worry about on the backend.

So from this side of things I feel pretty confident in saying that Sessionless mitigates much of the traditional risk with existing authentication systems. The other side is whether the cryptography used is safe or not. Here we've been careful to use the cryptographic libraries used by Bitcoin and Ethereum as they have been proven in a real world scenario to be reliable many times over. 

All of this said though, Sessionless is beta software, and you should use at your own risk. Open sourcing it is just the first step in getting it to a production-ready state.
