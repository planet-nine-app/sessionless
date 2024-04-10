extern crate hex as hex_core;

pub mod error;
mod impls;

use crate::*;
use error::*;
use hex_core::*;

/// Type implementing this trait can be encoded into a hex string.
pub trait IntoHex {
    fn to_hex(&self) -> String;

    fn into_hex(self) -> String
    where
        Self: Sized,
    {
        self.to_hex()
    }
}

/// Type implementing this trait can be constructed from a hex string.
pub trait FromHex {
    type Error: std::error::Error;

    fn from_hex(bytes: impl AsRef<[u8]>) -> Result<Self, HexError<Self::Error>>
    where
        Self: Sized;
}
