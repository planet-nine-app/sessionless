use super::*;
use super::error::*;

// ------------ PRIMITIVES ------------
impl IntoHex for &[u8] {
    fn to_hex(&self) -> String {
        encode(self)
    }
}

// ------------ SIGNATURE ------------
impl IntoHex for Signature {
    fn to_hex(&self) -> String {
        self.serialize_der().as_ref().to_hex()
    }
}

impl FromHex for Signature {
    type Error = secp256k1::Error;

    fn from_hex(bytes: impl AsRef<[u8]>) -> Result<Self, HexError<Self::Error>> {
        let data = decode(bytes.as_ref())?;
        Signature::from_der(&data).map_err(HexError::Other)
    }
}

// ------------ KEYS ------------
impl IntoHex for PrivateKey {
    fn to_hex(&self) -> String {
        self.secret_bytes().into_hex()
    }
}

impl FromHex for PrivateKey {
    type Error = secp256k1::Error;

    fn from_hex(bytes: impl AsRef<[u8]>) -> Result<Self, HexError<Self::Error>> {
        let data = decode(bytes.as_ref())?;
        PrivateKey::from_slice(&data).map_err(HexError::Other)
    }
}

impl IntoHex for PublicKey {
    fn to_hex(&self) -> String {
        self.serialize().into_hex()
    }
}

impl FromHex for PublicKey {
    type Error = secp256k1::Error;

    fn from_hex(bytes: impl AsRef<[u8]>) -> Result<Self, HexError<Self::Error>> {
        let data = decode(bytes.as_ref())?;
        PublicKey::from_slice(&data).map_err(HexError::Other)
    }
}

// ------------ OTHER ------------
#[cfg(feature = "uuid")]
impl IntoHex for uuid::Uuid {
    fn to_hex(&self) -> String {
        self.as_bytes().as_slice().to_hex()
    }

    fn into_hex(self) -> String where Self: Sized {
        self.into_bytes().as_slice().to_hex()
    }
}
