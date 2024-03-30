pub extern crate secp256k1;

#[cfg(test)]
mod tests;

pub mod hex;

use secp256k1::{Secp256k1, All, Message, rand::rngs::OsRng};
use sha3::{Digest, Keccak256, digest::{Output}, Keccak256Core};

pub type Signature = secp256k1::ecdsa::Signature;
pub type PrivateKey = secp256k1::SecretKey;
pub type PublicKey = secp256k1::PublicKey;

pub struct Sessionless {
    ctx: Secp256k1<All>,
    private_key: PrivateKey,
    public_key: PublicKey,
}

impl Sessionless {
    pub fn new() -> Self {
        let ctx = Secp256k1::new();
        let (private_key, public_key) = ctx.generate_keypair(&mut OsRng);

        Self {
            ctx,
            private_key,
            public_key,
        }
    }

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

    pub fn sign(&self, message: impl AsRef<[u8]>) -> Signature {
        let message_hash = self.hash_message(message);
        self.ctx.sign_ecdsa(&message_hash, &self.private_key).into()
    }

    pub fn verify(
        &self,
        message: impl AsRef<[u8]>,
        public_key: &PublicKey,
        signature: &Signature,
    ) -> Result<(), secp256k1::Error> {
        let message_hash = self.hash_message(message);
        self.ctx.verify_ecdsa(&message_hash, &signature, public_key)
    }

    pub fn public_key(&self) -> &PublicKey {
        &self.public_key
    }

    pub fn private_key(&self) -> &PrivateKey {
        &self.private_key
    }

    #[cfg(feature = "uuid")]
    pub fn generate_uuid() -> uuid::Uuid {
        uuid::Uuid::new_v4()
    }

    fn hash_message(&self, message: impl AsRef<[u8]>) -> Message {
        let mut hasher = Keccak256::new();
        hasher.update(message.as_ref());
        let digest: Output<Keccak256Core> = hasher.finalize();

        // Won't panic if Keccak256 was used to hash the message.
        Message::from_digest_slice(&*digest).unwrap()
    }
}
