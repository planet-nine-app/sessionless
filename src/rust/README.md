# Sessionless

This folder holds the Rust implementation of Sessionless. 
There is a hyper example, and a cli as well.

## Usage

### Installation

Currently available as a crate.
`crate add sessionless`

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


