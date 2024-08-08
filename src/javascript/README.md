# Sessionless

This folder holds the JavaScript, and TypeScript implementation of Sessionless. 
There are node, and React Native Expo versions currently.
The node version should work everywhere you want it to, there's just a need for platform-specific implementations due to how secure things are stored. 

## Usage

### Installation

Currently these are available only in npm, though other registries should come soon. 
So a standard `npm i sessionless-node` should work for you.

### Methods

Function | What it does
:--------|:------------
`generateKeys(saveKeys?: keys => void, getKeys?: () => Keys)` | Generates a private/public keypair and stores it in the platform's secure storage. Takes an optional `saveKeys` function for platforms that don't have clear-cut secure storage. 
`getKeys()` | Gets keys from secure storage.
`sign(message: String)` | Signs a message with the user's private key.
`verifySignature(message: String, signature: String, publicKey: String)` | Verifies a given signature with a public key.
`generateUUID()` | Creates a unique UUID for a user.
`associateKeys(associationMessage: String, primarySignature: String, secondarySignature: String, publicKey: String)` | Associates a secondary's key with the user's primary key.
`revokeKey(message: String, signature: String, publicKey: String)` | Revokes a gateway's key from the user.


