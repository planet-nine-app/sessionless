use anyhow::anyhow;
use sessionless::hex::IntoHex;
use sessionless::{Sessionless, Signature};
use serde::{Deserialize, Serialize};
use crate::requests::{Payload, Response, Request};
use crate::requests::register::RegisterResponse;
use crate::utils::Color;

pub struct CoolRequest;

impl Request for CoolRequest {
    type Input = RegisterResponse;
    type Output = ();

    fn endpoint() -> &'static str {
        "cool-stuff"
    }

    fn execute(color: Color, sessionless: &Sessionless, input: Self::Input) -> anyhow::Result<Self::Output> {
        let message = CoolPayload {
            uuid: input.uuid,
            coolness: "max",
            timestamp: "right now",
            signature: None,
        };

        let request_builder = Self::prepare(message, color, sessionless)?;
        let response = Self::send::<CoolResponse>(request_builder)?;

        if response.double_cool == "double cool" {
            info!("Aww yeah! The {:?} server thinks you're {}!", color, response.double_cool);
            Ok(())
        } else {
            Err(anyhow!(
                "{} request - Response is not 'double cool', but: {}",
                Self::endpoint(),
                response.double_cool
            ))
        }
    }
}

#[derive(Debug, Serialize)]
struct CoolPayload<'a> {
    uuid: String,
    coolness: &'a str,
    timestamp: &'a str,
    #[serde(skip_serializing_if = "Option::is_none")]
    signature: Option<String>,
}

impl Payload for CoolPayload<'_> {
    fn update_signature(&mut self, signature: Option<Signature>) {
        self.signature = signature.map(|sig| sig.to_hex())
    }
}

#[derive(Deserialize)]
#[serde(rename_all="camelCase")]
pub struct CoolResponse {
    pub double_cool: String
}

impl Response for CoolResponse {}
