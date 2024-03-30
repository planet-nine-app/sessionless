use std::fmt::{Debug, Display, Formatter};
use super::*;

pub use hex_core::FromHexError;

/// Possible errors when decoding a hex string into a specific type.
#[derive(Debug)]
pub enum HexError<E: std::error::Error> {
    /// Invalid hex string format.
    HexFormat(FromHexError),
    /// Some error with constructing the requested type.
    Other(E),
}

impl<E: std::error::Error> From<FromHexError> for HexError<E> {
    fn from(err: FromHexError) -> Self {
        Self::HexFormat(err)
    }
}

impl<E: std::error::Error> Display for HexError<E> {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        match self {
            Self::HexFormat(hex_err) => Display::fmt(hex_err, f),
            Self::Other(err) => Display::fmt(err, f),
        }
    }
}

impl<E: std::error::Error> std::error::Error for HexError<E> {}
