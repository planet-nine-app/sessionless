use sessionless::hex::IntoHex;
use sessionless::{PublicKey, Sessionless, Signature};
use reqwest;
use reqwest::blocking::Response;
use colored::Colorize;
use serde::{Deserialize};
use crate::requests::server_config;

#[derive(Deserialize)]
struct WelcomeResponse {
    uuid: String,
    welcomeMessage: String
}

#[derive(Deserialize)]
struct CoolnessResponse {
    doubleCool: String
}

pub fn do_cool_stuff(color: String, SESSIONLESS: Sessionless, result: Result<T, E>) {
    let welcome: WelcomeResponse = result.json::<WelcomeResponse>();
    
    let base_url = server_config::url_for_color(color);
    let placement = server_config::signature_placement_for_color(color);

    let message = format!("{{\"uuid\":\"{0}\",\"coolness\":\"max\",\"timestamp\":\"right now\"}}", welcome.uuid);

    let signature = SESSIONLESS.sign(message).into_hex();

    let client = reqwest::blocking::Client::new();
    let post = client.post("{base_url.to_string()}/cool-stuff")
        .header("Content-Type", "application/json")
        .header("Accept", "application/json");

    if placement == "payload" {
        let payload = format!("{{\"uuid\":\"{0}\",\"coolness\":\"max\",\"timestamp\":\"right now\",\"signature\":\"{signature}\"}}", welcome.uuid);
        post = post.body(payload);
    } else {
        post = post.header("signature", signature)
        .body(message);
    }

    let res = post.send();
    let coolness_response: CoolnessResponse = res.json::<CoolnessResponse>();
       
    if coolness_response.doubleCool == "double cool".to_string() {
        let success = format!("Aww yeah! The {color} server thinks you're {1}!", coolness_response.doubleCool);
        println!("{}", success.to_string().green());
    } else {
        let fail = "Oh no, something went wrong.".red();
        println!("{}", fail.to_string());
    }
}
