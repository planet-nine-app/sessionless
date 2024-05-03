use sessionless::hex::IntoHex;
use sessionless::Sessionless;
use reqwest;
use serde::{Deserialize, Serialize};
use crate::requests::{Payload, Response};
use crate::utils::Color;

#[derive(Debug, Serialize)]
struct RegisterPayload<'a> {
    pub_key: String,
    entered_text: &'a str,
    timestamp: &'a str,
    #[serde(skip_serializing_if = "Option::is_none")]
    signature: Option<String>,
}

#[derive(Deserialize)]
#[serde(rename_all="camelCase")]
pub struct RegisterResponse {
    pub uuid: String,
    #[allow(dead_code)]  // todo: Find out why this field is never used.
    welcome_message: String
}

impl Payload for RegisterPayload<'_> {}
impl Response for RegisterResponse {}

pub fn register(color: Color) -> (Sessionless, RegisterResponse) {
    let sessionless = Sessionless::new();
    let client = reqwest::blocking::Client::new();

    let placement = color.get_signature_placement();

    let mut message = RegisterPayload {
        pub_key: sessionless.public_key().to_hex(),
        entered_text: "Foo",
        timestamp: "right now",
        signature: None,
    };

    let signature = message.sign(&sessionless).to_hex();

    let mut request_builder = client
        .post({
            let url = format!("{}/register", color.get_url());
            println!("{}", url);
            url
        })
        .header("Content-Type", "application/json")
        .header("Accept", "application/json");

    if placement == "payload" {
        message.signature = Some(signature);
        request_builder = request_builder
            .body(message.as_json());
    } else {
        request_builder = request_builder
            .header("signature", signature)
            .body(message.as_json());
    }

    let welcome_response = match request_builder
        .send()
        .expect("Something went awry!")
        .json::<RegisterResponse>()
    {
        Ok(wr) => wr,
        Err(_) => panic!("Error serializing JSON")
    };

    return (sessionless, welcome_response);
}
