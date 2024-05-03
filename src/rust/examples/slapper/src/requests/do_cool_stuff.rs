use anyhow::anyhow;
use sessionless::hex::IntoHex;
use sessionless::Sessionless;
use reqwest;
use serde::{Deserialize, Serialize};
use crate::requests::{RegisterResponse, Payload, Response};
use crate::utils::{Color, Placement};

#[derive(Debug, Serialize)]
struct CoolPayload<'a> {
    uuid: String,
    coolness: &'a str,
    timestamp: &'a str,
    #[serde(skip_serializing_if = "Option::is_none")]
    signature: Option<String>,
}

#[derive(Deserialize)]
#[serde(rename_all="camelCase")]
pub struct CoolResponse {
    pub double_cool: String
}

impl Payload for CoolPayload<'_> {}

impl Response for CoolResponse {}

pub fn do_cool_stuff(
    color: Color,
    sessionless: Sessionless,
    welcome_response: RegisterResponse
) -> anyhow::Result<()> {
    let client = reqwest::blocking::Client::new();

    let mut message = CoolPayload {
        uuid: welcome_response.uuid,
        coolness: "max",
        timestamp: "right now",
        signature: None,
    };

    let signature = message.sign(&sessionless).to_hex();

    let mut request_builder = client
        .post({
            let url = format!("{}/cool-stuff", color.get_url());
            debug!("{}", url);
            url
        })
        .header("Content-Type", "application/json")
        .header("Accept", "application/json");

    match color.get_signature_placement() {
        Placement::Payload => {
            message.signature = Some(signature);
            request_builder = request_builder
                .body(message.as_json());
        }
        Placement::Header => {
            request_builder = request_builder
                .header("signature", signature)
                .body(message.as_json());
        }
    }

    let coolness_response = match request_builder
        .send()
        .map_err(|err| anyhow!("CoolStuff request - Failure: {err}"))?
        .json::<CoolResponse>()
    {
        Ok(cr) => cr,
        Err(err) => return Err(
            anyhow!("CoolStuff request - Invalid JSON response: {err}")
        )
    };

    if coolness_response.double_cool == "double cool" {
        info!("Aww yeah! The {:?} server thinks you're {}!", color, coolness_response.double_cool);
    } else {
        return Err(
            anyhow!("CoolStuff request - Response is not 'double cool'")
        );
    }

    Ok(())
}
