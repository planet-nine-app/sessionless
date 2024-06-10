mod state;

#[macro_use]
extern crate lazy_static;

use async_load::types::*;
use sessionless::{PrivateKey, Sessionless};
use sessionless::hex::{FromHex, IntoHex};

#[no_mangle]
#[allow(unused_variables)]
pub unsafe extern "C" fn slap_ctx_new() -> GMLDouble {
    let ctx = Sessionless::new();
    let ctx_id = ((&ctx) as *const Sessionless) as u64;

    state::ctx_add(ctx_id, ctx)
}

#[no_mangle]
#[allow(unused_variables)]
pub unsafe extern "C" fn slap_ctx_new_from_key(private_key: GMLString) -> GMLDouble {
    let private_key = match PrivateKey::from_hex(
        private_key.as_str().unwrap_or_default().as_bytes()
    ) {
        Ok(key) => key,
        Err(_) => {
            return GMLDouble::from(0);
        }
    };

    let ctx = Sessionless::from_private_key(private_key);
    let ctx_id = ((&ctx) as *const Sessionless) as u64;

    state::ctx_add(ctx_id, ctx)
}

#[no_mangle]
#[allow(unused_variables)]
pub unsafe extern "C" fn slap_ctx_sign(ctx_id: GMLDouble, message: GMLString) -> GMLString {
    state::ctx_sign(
        ctx_id as u64,
        message.as_str().unwrap_or_default(),
    )
}

#[no_mangle]
#[allow(unused_variables)]
pub unsafe extern "C" fn slap_ctx_verify(ctx_id: GMLDouble, message: GMLString, public_key: GMLString, sign: GMLString) -> GMLDouble {
    state::ctx_verify(
        ctx_id as u64,
        message.as_str().unwrap_or_default(),
        public_key,
        sign,
    )
}

#[no_mangle]
#[allow(unused_variables)]
pub unsafe extern "C" fn slap_ctx_get_key_public(ctx_id: GMLDouble) -> GMLString {
    state::ctx_public_key(ctx_id as u64)
}

#[no_mangle]
#[allow(unused_variables)]
pub unsafe extern "C" fn slap_ctx_get_key_private(ctx_id: GMLDouble) -> GMLString {
    state::ctx_private_key(ctx_id as u64)
}

#[no_mangle]
#[allow(unused_variables)]
pub unsafe extern "C" fn slap_ctx_remove(ctx_id: GMLDouble) -> GMLDouble {
    state::ctx_remove(ctx_id as u64)
}

#[no_mangle]
#[allow(unused_variables)]
pub unsafe extern "C" fn slap_uuid_new() -> GMLString {
    Sessionless::generate_uuid().to_hex().into_gml().unwrap()
}
