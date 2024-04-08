use super::*;

#[derive(Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct PayloadRegister {
    pub public_key: String,
    pub timestamp: String,
    pub entered_text: String,
}

#[derive(Serialize)]
#[serde(rename_all = "camelCase")]
pub struct ResponseRegister {
    pub uuid: String,
    pub welcome_message: String,
}

pub struct Register;

impl EndpointAPI for Register {
    async fn handle(head: &Parts, body: Incoming, builder: &mut Builder) -> anyhow::Result<()> {
        let body = &*body.collect().await?.to_bytes();
        let payload = load_payload::<PayloadRegister>(body).await?;

        let public_key = PublicKey::from_hex(payload.public_key.as_bytes())?;
        let signature = Signature::from_hex(get_header(head, "signature")?)?;

        SESSIONLESS
            .get()
            .ok_or_else(|| anyhow!("Sessionless was not initialized!"))?
            .verify(body, &public_key, &signature)
            .map_err(|err| anyhow!("Failed to verify the payload: {}", err))?;

        let uuid = Sessionless::generate_uuid().to_string();
        let _ = DATABASE.lock().await.insert(uuid.clone(), public_key);

        builder.status = 200;
        builder.set_body(ResponseRegister {
            uuid,
            welcome_message: "Welcome to this example!".to_string(),
        });

        Ok(())
    }
}
