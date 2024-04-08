mod cool_stuff;
mod register;

pub use cool_stuff::*;
pub use register::*;

use crate::response::Builder;
use crate::SESSIONLESS;
use anyhow::anyhow;
use http_body_util::BodyExt;
use hyper::body::Incoming;
use hyper::http::request::Parts;
use serde::de::DeserializeOwned;
use sessionless::hex::FromHex;
use sessionless::{PublicKey, Sessionless, Signature};
use std::collections::HashMap;
use tokio::sync::Mutex;

lazy_static! {
    static ref DATABASE: Mutex<HashMap<String, PublicKey>> = Mutex::new(HashMap::new());
}

pub trait EndpointAPI {
    async fn handle(head: &Parts, body: Incoming, builder: &mut Builder) -> anyhow::Result<()>;
}

fn get_header<'a>(head: &'a Parts, key: &str) -> anyhow::Result<&'a str> {
    head.headers
        .get(key)
        .ok_or_else(|| anyhow!("Missing 'signature' header!"))?
        .to_str()
        .map_err(|err| anyhow!("Invalid 'signature' header: {}", err))
}

async fn load_payload<P: DeserializeOwned>(data: impl AsRef<[u8]>) -> anyhow::Result<P> {
    serde_json::from_slice::<P>(data.as_ref()).map_err(|err| anyhow!("Invalid payload: {}", err))
}
