use std::collections::HashMap;
use http_body_util::BodyExt;
use serde::de::DeserializeOwned;
use sessionless::hex::FromHex;
use sessionless::{PublicKey, Sessionless, Signature};
use tokio::sync::Mutex;
use crate::{request, SESSIONLESS};
use super::*;

lazy_static! {
    static ref DATABASE: Mutex<HashMap<String, PublicKey>> = Mutex::new(HashMap::new());
}

fn get_header<'a>(head: &'a Parts, key: &str) -> anyhow::Result<&'a str> {
    head.headers
        .get(key)
        .ok_or_else(|| anyhow!("Missing 'signature' header!"))?
        .to_str()
        .map_err(|err| anyhow!("Invalid 'signature' header: {}", err))
}

async fn load_payload<P: DeserializeOwned>(data: impl AsRef<[u8]>) -> anyhow::Result<P> {
    serde_json::from_slice::<P>(
        data.as_ref()
    ).map_err(|err| anyhow!("Invalid payload: {}", err))
}

pub async fn register(head: &Parts, body: Incoming, builder: &mut response::Builder) -> anyhow::Result<()> {
    let body = &*body.collect().await?.to_bytes();
    let payload = load_payload::<request::PayloadRegister>(body).await?;

    let public_key = PublicKey::from_hex(payload.public_key.as_bytes())?;
    let signature = Signature::from_hex(get_header(head, "signature")?)?;

    SESSIONLESS.get()
        .ok_or_else(|| anyhow!("Sessionless was not initialized!"))?
        .verify(body, &public_key, &signature)
        .map_err(|err| anyhow!("Failed to verify the payload: {}", err))?;

    let uuid = Sessionless::generate_uuid().to_string();
    let _ = DATABASE.lock().await.insert(
        uuid.clone(),
        public_key,
    );

    builder.status = 200;
    builder.set_body(response::Register {
        uuid,
        welcome_message: "Welcome to this example!".to_string(),
    });

    Ok(())
}

pub async fn cool_stuff(head: &Parts, body: Incoming, builder: &mut response::Builder) -> anyhow::Result<()> {
    let body = &*body.collect().await?.to_bytes();
    let payload = load_payload::<request::PayloadCoolStuff>(body).await?;
    
    let public_key = DATABASE.lock().await
        .get(&payload.uuid)
        .map(|pub_key| *pub_key)
        .ok_or_else(|| anyhow!("User {} not found!", payload.uuid))?;

    let signature = Signature::from_hex(get_header(head, "signature")?)?;

    SESSIONLESS.get()
        .ok_or_else(|| anyhow!("Sessionless was not initialized!"))?
        .verify(body, &public_key, &signature)
        .map_err(|err| anyhow!("Failed to verify the payload: {}", err))?;

    builder.status = 200;
    builder.set_body(response::CoolStuff {
        double_cool: "double cool".to_string(),
    });
    
    Ok(())
}
