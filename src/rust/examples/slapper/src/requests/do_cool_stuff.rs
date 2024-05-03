use sessionless::hex::IntoHex;
use sessionless::Sessionless;
use reqwest;
use colored::Colorize;
use serde::{Deserialize, Serialize};
use crate::requests::{RegisterResponse, Payload, Response};
use crate::utils::Color;

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

pub fn do_cool_stuff(color: Color, sessionless: Sessionless, welcome_response: RegisterResponse) {
    let client = reqwest::blocking::Client::new();
    let base_url = color.get_url();
    let placement = color.get_signature_placement();

    let mut message = CoolPayload {
        uuid: welcome_response.uuid,
        coolness: "max",
        timestamp: "right now",
        signature: None,
    };

    let signature = message.sign(&sessionless).to_hex();

    let mut request_builder = client
        .post({
            let url = format!("{}/cool-stuff", base_url);
            println!("{}", url);
            url
        })
        .header("Content-Type", "application/json")
        .header("Accept", "application/json");

    if placement == "payload".to_string() {
        message.signature = Some(signature);
        request_builder = request_builder
            .body(message.as_json());
    } else {
        request_builder = request_builder
            .header("signature", signature)
            .body(message.as_json());
    }

    let coolness_response = match request_builder
        .send()
        .expect("Something went awry!")
        .json::<CoolResponse>()
    {
        Ok(cr) => cr,
        Err(_) => panic!("Error serializing JSON")
    };

    if coolness_response.double_cool == "double cool" {
        let success = format!("Aww yeah! The {:?} server thinks you're {}!", color, coolness_response.double_cool);
        println!("{}", success.to_string().green());
    } else {
        let fail = "Oh no, something went wrong.".red();
        println!("{}", fail.to_string());
    }
}
