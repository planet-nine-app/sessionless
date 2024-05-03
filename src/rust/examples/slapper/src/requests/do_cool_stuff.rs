use sessionless::hex::IntoHex;
use sessionless::{Sessionless, Signature};
use reqwest;
use colored::Colorize;
use serde::Serialize;
use crate::requests::{WelcomeResponse, CoolnessResponse};
use crate::utils::Color;

#[derive(Debug, Serialize)]
struct Message<'a> {
    uuid: String,
    coolness: &'a str,
    timestamp: &'a str,
    #[serde(skip_serializing_if = "Option::is_none")]
    signature: Option<String>,
}

impl<'a> Message<'a> {
    fn as_json(&self) -> String {
        serde_json::to_string(&self).unwrap()
    }

    fn sign(&self, sessionless: &Sessionless) -> Signature {
        let message_json = self.as_json();
        println!("Signing {}", message_json);
        sessionless.sign(message_json.as_bytes())
    }
}

pub fn do_cool_stuff(color: Color, sessionless: Sessionless, welcome_response: WelcomeResponse) {
    let client = reqwest::blocking::Client::new();
    let base_url = color.get_url();
    let placement = color.get_signature_placement();

    let mut message = Message {
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
        .json::<CoolnessResponse>()
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
