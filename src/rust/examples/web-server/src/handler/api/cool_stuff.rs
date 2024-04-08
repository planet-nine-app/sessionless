use super::*;

#[derive(Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct PayloadCoolStuff {
    pub timestamp: String,
    pub coolness: String,
    pub uuid: String,
}

#[derive(Serialize)]
#[serde(rename_all = "camelCase")]
pub struct ResponseCoolStuff {
    pub double_cool: String,
}

pub struct CoolStuff;

impl EndpointAPI for CoolStuff {
    async fn handle(head: &Parts, body: Incoming, builder: &mut Builder) -> anyhow::Result<()> {
        let body = &*body.collect().await?.to_bytes();
        let payload = load_payload::<PayloadCoolStuff>(body).await?;

        let public_key = DATABASE
            .lock()
            .await
            .get(&payload.uuid)
            .copied()
            .ok_or_else(|| anyhow!("User {} not found!", payload.uuid))?;

        let signature = Signature::from_hex(get_header(head, "signature")?)?;

        SESSIONLESS
            .get()
            .ok_or_else(|| anyhow!("Sessionless was not initialized!"))?
            .verify(body, &public_key, &signature)
            .map_err(|err| anyhow!("Failed to verify the payload: {}", err))?;

        builder.status = 200;
        builder.set_body(ResponseCoolStuff {
            double_cool: "double cool".to_string(),
        });

        Ok(())
    }
}
