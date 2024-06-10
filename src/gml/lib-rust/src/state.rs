use std::collections::HashMap;
use std::sync::RwLock;
use sessionless::hex::IntoHex;
use sessionless::{PublicKey, Signature};
use super::*;

lazy_static! {
    static ref CONTEXTS: RwLock<HashMap<u64, Sessionless>> = RwLock::new(HashMap::new());
}

pub fn ctx_add(id: u64, ctx: Sessionless) -> GMLDouble {
    if let Ok(mut contexts) = CONTEXTS.write() {
        contexts.insert(id, ctx);
        id as GMLDouble
    } else {
        GMLDouble::from(0)
    }
}

pub fn ctx_sign(id: u64, message: impl AsRef<[u8]>) -> GMLString {
    if let Ok(contexts) = CONTEXTS.read() {
        if let Some(ctx) = contexts.get(&id) {
            let sign = ctx.sign(message);
            return sign.to_hex().into_gml().unwrap();
        }
    }

    GMLString::none()
}

pub fn ctx_verify(id: u64, message: impl AsRef<[u8]>, public_key: GMLString, sign: GMLString) -> GMLDouble {
    if let Ok(contexts) = CONTEXTS.read() {
        if let Some(ctx) = contexts.get(&id) {
            let public_key = if let Ok(key) = PublicKey::from_hex(
                public_key.as_str().unwrap_or_default()
            ) {
                key
            } else {
                return GMLDouble::from(0);
            };

            let sign = if let Ok(sign) = Signature::from_hex(
                sign.as_str().unwrap_or_default()
            ) {
                sign
            } else {
                return GMLDouble::from(0);
            };

            let result = match ctx.verify(message, &public_key, &sign) {
                Ok(_) => 1,
                Err(_) => 0,
            };

            return GMLDouble::from(result);
        }
    }

    GMLDouble::from(0)
}

pub fn ctx_public_key(id: u64) -> GMLString {
    if let Ok(contexts) = CONTEXTS.read() {
        if let Some(ctx) = contexts.get(&id) {
            return ctx.public_key().to_hex().into_gml().unwrap();
        }
    }

    GMLString::none()
}

pub fn ctx_private_key(id: u64) -> GMLString {
    if let Ok(contexts) = CONTEXTS.read() {
        if let Some(ctx) = contexts.get(&id) {
            return ctx.private_key().to_hex().into_gml().unwrap();
        }
    }

    GMLString::none()
}

pub fn ctx_remove(id: u64) -> GMLDouble {
    if let Ok(mut contexts) = CONTEXTS.write() {
        contexts.remove(&id);
        GMLDouble::from(1)
    } else {
        GMLDouble::from(0)
    }
}
