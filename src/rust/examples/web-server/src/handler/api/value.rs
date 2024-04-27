use super::*;
use crate::database::Database;
use crate::response;
use crate::response::ResponseSuccess;
use simple_base64 as base64;

#[derive(Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct PayloadValuePost {
    pub uuid: String,
    pub timestamp: String,
    pub value: String,
}

#[derive(Deserialize, Serialize)]
struct Message {
    uuid: String,
    timestamp: String,
}

pub struct Value;

impl Value {
    async fn get(head: &Parts, _body: Incoming, builder: &mut Builder) -> anyhow::Result<()> {
        let params = get_query_params(head);
        let message_encoded = params
            .get("message")
            .ok_or_else(|| anyhow!("Missing 'message' query parameter"))?;

        let message = load_payload::<Message>(
            base64::decode(message_encoded)
                .map_err(|err| anyhow!("Message not encoded with base64: {}", err))?,
        )
        .await?;

        let signature = Signature::from_hex(get_header(head, "signature")?)?;
        let user_public_key = Database::get_user_pub_key(&message.uuid)
            .await
            .ok_or_else(|| anyhow!("User {} not found", message.uuid))?;

        SESSIONLESS
            .get()
            .ok_or_else(|| anyhow!("Sessionless was not initialized!"))?
            .verify(message_encoded, &user_public_key, &signature)
            .map_err(|err| anyhow!("Failed to verify the message: {}", err))?;

        match Database::get_value(message.uuid).await {
            None => {
                builder.status = 404;
                builder.set_body(response::Error {
                    status: builder.status,
                    message: "Value not found!".to_string(),
                });
            }
            Some(value) => {
                builder.status = 200;
                builder.set_body(value);
            }
        }

        Ok(())
    }

    async fn post(head: &Parts, body: Incoming, builder: &mut Builder) -> anyhow::Result<()> {
        let body = &*body.collect().await?.to_bytes();
        let payload = load_payload::<PayloadValuePost>(body).await?;

        let signature = Signature::from_hex(get_header(head, "signature")?)?;
        let public_key_asc = Database::get_user_by_associated_key(&payload.uuid)
            .await
            .ok_or_else(|| anyhow!("Associated user {} not found!", payload.uuid))?;

        SESSIONLESS
            .get()
            .ok_or_else(|| anyhow!("Sessionless was not initialized!"))?
            .verify(body, &public_key_asc, &signature)
            .map_err(|err| anyhow!("Failed to verify the payload: {}", err))?;

        Database::save_value(&payload.uuid, payload.value)
            .await
            .ok_or_else(|| anyhow!("User {} not found!", payload.uuid))?;

        builder.status = 200;
        builder.set_body(ResponseSuccess { success: true });

        Ok(())
    }
}

impl EndpointAPI for Value {
    async fn handle(head: &Parts, body: Incoming, builder: &mut Builder) -> anyhow::Result<()> {
        match head.method.as_str() {
            "GET" => Value::get(head, body, builder).await,
            "POST" => Value::post(head, body, builder).await,
            _ => {
                builder.status = 405;
                Err(anyhow!("Method not allowed"))
            }
        }
    }
}
