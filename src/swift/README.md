### Overview

[PLEASE NOTE I THINK THIS MIGHT BE BROKEN RIGHT NOW. HOPEFULLY I REMEMBER TO REMOVE THIS MESSAGE WHEN I FIX IT]

The Swift implementation of Sessionless is available as a local pod from this repo. 
I think the new hotness is spm, so I'll likely distribute like that instead, but for now you can just grab the client-ios folder from this repo, and link it in your Podfile.
See any of the examples' Podfiles for how to do this.

### Usage

Sessionless defines a public Keys struct with methods for turning the private/public key tuple into data and string for usage in storage. 
The private key is kept private to Sessionless to minimize accidental sharing of it.

The methods available are:

* `generateKeys() -> Keys?`

* `sign(message: String) -> String?`

* `verifySignature(signature: String, message: String, publicKey: String) -> Bool`

* `generateUUID() -> String`
