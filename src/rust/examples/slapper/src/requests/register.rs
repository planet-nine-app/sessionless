use sessionless::hex::IntoHex;
use sessionless::{Sessionless, Signature};
use reqwest;
use serde::Serialize;
use crate::requests::WelcomeResponse;
use crate::utils::Color;

#[derive(Debug, Serialize)]
struct Message<'a> {
    pub_key: String,
    entered_text: &'a str,
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

pub fn register(color: Color) -> (Sessionless, WelcomeResponse) {
    let sessionless = Sessionless::new();
    let client = reqwest::blocking::Client::new();

    let placement = color.get_signature_placement();

    let mut message = Message {
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
        .json::<WelcomeResponse>()
    {
        Ok(wr) => wr,
        Err(_) => panic!("Error serializing JSON")
    };

    return (sessionless, welcome_response);
}
