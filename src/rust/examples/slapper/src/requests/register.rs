use sessionless::hex::IntoHex;
use sessionless::{Sessionless, Signature};
use serde::{Deserialize, Serialize};
use crate::requests::{Payload, Request, Response};
use crate::utils::Color;

pub struct RegisterRequest;

impl Request for RegisterRequest {
    type Input = ();
    type Output = RegisterResponse;

    fn endpoint() -> &'static str {
        "register"
    }

    fn execute(color: Color, sessionless: &Sessionless, _: Self::Input) -> anyhow::Result<Self::Output> {
        let message = RegisterPayload {
            pub_key: sessionless.public_key().to_hex(),
            entered_text: "Foo",
            timestamp: "right now",
            signature: None,
        };

        let request_builder = Self::prepare(message, color, sessionless)?;
        let response = Self::send::<RegisterResponse>(request_builder)?;

        Ok(response)
    }
}

#[derive(Debug, Serialize)]
struct RegisterPayload<'a> {
    pub_key: String,
    entered_text: &'a str,
    timestamp: &'a str,
    #[serde(skip_serializing_if = "Option::is_none")]
    signature: Option<String>,
}

impl Payload for RegisterPayload<'_> {
    fn update_signature(&mut self, signature: Option<Signature>) {
        self.signature = signature.map(|sig| sig.to_hex())
    }
}

#[derive(Deserialize)]
#[serde(rename_all="camelCase")]
pub struct RegisterResponse {
    pub uuid: String,
    #[allow(dead_code)]  // todo: Find out why this field is never used.
    welcome_message: String
}

impl Response for RegisterResponse {}
