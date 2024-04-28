use sessionless::hex::IntoHex;
use sessionless::{PublicKey, Sessionless, Signature};
use reqwest;
use reqwest::blocking::Response;

pub fn register<T, E>(base_url: String, placement: String) -> (Sessionless, Result<T, E>) {
    let SESSIONLESS = Sessionless::new();

    let public_key = SESSIONLESS.public_key().to_hex();
    let entered_text = "Foo";
    let timestamp = "right now";

    let message = "{{\"pubKey\":\"{public_key}\",\"enteredText\":\"{entered_text}\",\"timestamp\":\"{timestamp}\"}}";    
    let signature = SESSIONLESS.sign(message).into_hex();

    let client = reqwest::blocking::Client::new();
    let post = client.post("{base_url.to_string()}/register")
        .header("Content-Type", "application/json")
        .header("Accept", "application/json");

    if placement == "payload" {
        let payload = "{{\"pubKey\":\"{public_key}\",\"enteredText\":\"{entered_text}\",\"timestamp\":\"{timestamp}\"}}";
        post = post.body(payload)
    } else {
        post = post.header("signature", signature)
        .body(message);
    }
    let res = post.send();
    return (SESSIONLESS, res);
}
