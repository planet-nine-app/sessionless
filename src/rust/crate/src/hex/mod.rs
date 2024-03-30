extern crate hex as hex_core;

pub mod error;
mod impls;

use crate::*;
use error::*;
use hex_core::*;

pub trait IntoHex {
    fn to_hex(&self) -> String;

    fn into_hex(self) -> String where Self: Sized {
        self.to_hex()
    }
}

pub trait FromHex {
    type Error: std::error::Error;

    fn from_hex(bytes: impl AsRef<[u8]>) -> Result<Self, HexError<Self::Error>> where
        Self: Sized;
}
