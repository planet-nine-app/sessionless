mod associate;
mod cool_stuff;
mod register;
mod value;

pub use associate::*;
pub use cool_stuff::*;
pub use register::*;
pub use value::*;

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

fn get_query_params(head: &Parts) -> HashMap<String, String> {
    let params: HashMap<String, String> = head
        .uri
        .query()
        .map(|v| {
            url::form_urlencoded::parse(v.as_bytes())
                .into_owned()
                .collect()
        })
        .unwrap_or_else(HashMap::new);

    params
}

async fn load_payload<P: DeserializeOwned>(data: impl AsRef<[u8]>) -> anyhow::Result<P> {
    serde_json::from_slice::<P>(data.as_ref()).map_err(|err| anyhow!("Invalid payload: {}", err))
}
