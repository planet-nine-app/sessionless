# Sessionless

This folder holds the Rust implementation of Sessionless. 
There is a hyper example, and a cli as well.

## Usage

### Installation

Currently available as a crate.
`cargo add sessionless`

### Methods

| Function                                                                                              | What it does                                              |
|:------------------------------------------------------------------------------------------------------|:----------------------------------------------------------|
| `Sessionless::new()`                                                                                  | Generates new context object with private/public keypair. |
| `Sessionless::public_key(&self)`                                                                      | Returns the public key.                                   |
| `Sessionless::private_key(&self)`                                                                     | Returns the private key.                                  |
| `Sessionless::sign(&self, message: impl AsRef<[u8]>)`                                                 | Signs a message with the user's private key.              |
| `Sessionless::verify(&self, message: impl AsRef<[u8]>, publicKey: &PublicKey, signature: &Signature)` | Verifies a given signature with a public key.             |
| `Sessionless::generate_uuid()`                                                                        | Creates a unique UUID for a user.                         |
