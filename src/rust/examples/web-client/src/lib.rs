mod utils;

use once_cell::sync::OnceCell;
use sessionless::hex::IntoHex;
use sessionless::Sessionless;
use wasm_bindgen::prelude::*;
use crate::utils::try_get_context;

static SESSIONLESS: OnceCell<Sessionless> = OnceCell::new();

#[wasm_bindgen]
pub fn init() {
    SESSIONLESS.get_or_init(|| Sessionless::new());
}

#[wasm_bindgen]
pub fn get_public_key() -> Result<String, String> {
    let ctx = try_get_context()?;
    Ok(ctx.public_key().into_hex())
}

#[wasm_bindgen]
pub fn get_private_key() -> Result<String, String> {
    let ctx = try_get_context()?;
    Ok(ctx.private_key().into_hex())
}

#[wasm_bindgen]
pub fn sign(message: &str) -> Result<String, String> {
    let ctx = try_get_context()?;
    Ok(ctx.sign(message).into_hex())
}
