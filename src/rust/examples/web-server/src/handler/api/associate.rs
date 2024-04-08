use super::*;
use crate::database::Database;
use crate::response::ResponseSuccess;
use simple_base64 as base64;

#[derive(Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct PayloadAssociate {
    pub message1: String,
    pub signature1: String,
    pub message2: String,
    pub signature2: String,
    pub pub_key: String,
}

#[derive(Deserialize, Serialize)]
struct Message {
    uuid: String,
    timestamp: String,
}

pub struct Associate;

impl EndpointAPI for Associate {
    async fn handle(_head: &Parts, body: Incoming, builder: &mut Builder) -> anyhow::Result<()> {
        let body = &*body.collect().await?.to_bytes();
        let payload = load_payload::<PayloadAssociate>(body).await?;

        let message1 = load_payload::<Message>(
            base64::decode(&payload.message1)
                .map_err(|err| anyhow!("message1 not encoded with base64: {}", err))?,
        )
        .await?;

        let db_pub_key = Database::get_user_pub_key(&message1.uuid)
            .await
            .ok_or_else(|| anyhow!("User {} not found!", message1.uuid))?;

        let payload_pub_key = PublicKey::from_hex(payload.pub_key)?;

        let message2 = load_payload::<Message>(
            base64::decode(&payload.message2)
                .map_err(|err| anyhow!("message1 not encoded with base64: {}", err))?,
        )
        .await?;

        let signature1 = Signature::from_hex(payload.signature1)?;
        let signature2 = Signature::from_hex(payload.signature2)?;

        let sl = SESSIONLESS
            .get()
            .ok_or_else(|| anyhow!("Sessionless was not initialized!"))?;

        sl.verify(payload.message1, &db_pub_key, &signature1)
            .map_err(|err| anyhow!("Failed to verify the message1: {}", err))?;

        sl.verify(payload.message2, &payload_pub_key, &signature2)
            .map_err(|err| anyhow!("Failed to verify the message2: {}", err))?;

        Database::associate_key(&message1.uuid, message2.uuid, payload_pub_key).await;

        builder.status = 200;
        builder.set_body(ResponseSuccess { success: true });

        Ok(())
    }
}
