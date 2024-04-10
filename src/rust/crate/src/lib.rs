//! Rust implementation of the [Sessionless](https://github.com/planet-nine-app/sessionless) protocol.
//!
//! Sessionless is an authentication protocol that uses the cryptography employed by Bitcoin and Ethereum to authenticate messages sent between a client and a server.
//!
//! This crate allows to create a context with which it's possible to sign and verify messages to communicate with other Sessionless instances.
//!
//! Enable the `uuid` feature to be able to generate random UUIDs _(useful for the server)_.

pub extern crate secp256k1;

#[cfg(test)]
mod tests;

/// Module for the hex encoding.
pub mod hex;

use secp256k1::{rand::rngs::OsRng, All, Message, Secp256k1};
use sha3::{digest::Output, Digest, Keccak256, Keccak256Core};

pub type Signature = secp256k1::ecdsa::Signature;
pub type PrivateKey = secp256k1::SecretKey;
pub type PublicKey = secp256k1::PublicKey;

/// Sessionless context structure used to perform all signature operations.
/// It contains the `secp256k1` engine and private-public key pair.
///
/// Do not create a new context object for each operation, as construction is
/// far slower than all other API calls.
pub struct Sessionless {
    ctx: Secp256k1<All>,
    private_key: PrivateKey,
    public_key: PublicKey,
}

impl Sessionless {
    /// Creates a new randomized Sessionless context.
    ///
    /// Private key is generated using `OsRng`.
    pub fn new() -> Self {
        let ctx = Secp256k1::new();
        let (private_key, public_key) = ctx.generate_keypair(&mut OsRng);

        Self {
            ctx,
            private_key,
            public_key,
        }
    }

    /// Creates a randomized Sessionless context, but with a designated key pair.
    pub fn from_private_key(private_key: PrivateKey) -> Self {
        let ctx = Secp256k1::new();
        let public_key = private_key.public_key(&ctx);

        Self {
            ctx,
            private_key,
            public_key,
        }
    }

    #[cfg(test)]
    /// Creates a Sessionless context with predefined output.
    ///
    /// Used ONLY for testing purposes.
    pub fn new_predefined() -> Self {
        let mut ctx = Secp256k1::new();
        ctx.seeded_randomize(&[0u8; 32]);

        let mut step_rng = secp256k1::rand::rngs::mock::StepRng::new(0, 1);
        let (private_key, public_key) = ctx.generate_keypair(&mut step_rng);

        Self {
            ctx,
            private_key,
            public_key,
        }
    }

    /// Constructs a signature for `message` using the context's secret key.
    pub fn sign(&self, message: impl AsRef<[u8]>) -> Signature {
        let message_hash = self.hash_message(message);
        self.ctx.sign_ecdsa(&message_hash, &self.private_key)
    }

    /// Checks if the `signature` is valid for the `message` using the `public key`
    /// which was a part of the context used to construct that signature.
    pub fn verify(
        &self,
        message: impl AsRef<[u8]>,
        public_key: &PublicKey,
        signature: &Signature,
    ) -> Result<(), secp256k1::Error> {
        let message_hash = self.hash_message(message);
        self.ctx.verify_ecdsa(&message_hash, signature, public_key)
    }

    /// Returns the context's public key which can be shared publicly.
    pub fn public_key(&self) -> &PublicKey {
        &self.public_key
    }

    /// Returns the context's public key which should never be shared with anyone.
    pub fn private_key(&self) -> &PrivateKey {
        &self.private_key
    }

    /// Creates a random UUID.
    #[cfg(feature = "uuid")]
    pub fn generate_uuid() -> uuid::Uuid {
        uuid::Uuid::new_v4()
    }

    fn hash_message(&self, message: impl AsRef<[u8]>) -> Message {
        let mut hasher = Keccak256::new();
        hasher.update(message.as_ref());
        let digest: Output<Keccak256Core> = hasher.finalize();

        // Won't panic if Keccak256 was used to hash the message.
        Message::from_digest_slice(&digest).unwrap()
    }
}
