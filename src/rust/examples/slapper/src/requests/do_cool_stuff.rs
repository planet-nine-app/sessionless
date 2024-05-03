use sessionless::hex::IntoHex;
use sessionless::Sessionless;
use reqwest;
use colored::Colorize;
use crate::requests::server_config;
use crate::requests::{WelcomeResponse, CoolnessResponse};

pub fn do_cool_stuff(color: &String, sessionless: Sessionless, welcome_response: WelcomeResponse) {
    
    let base_url = server_config::url_for_color(color);
    let placement = server_config::signature_placement_for_color(color);

    let message = format!(r#"{{"uuid":"{0}","coolness":"max","timestamp":"right now"}}"#, welcome_response.uuid);

    let signature = sessionless.sign(message.clone()).into_hex();

    let client = reqwest::blocking::Client::new();
    let url = format!("{0}/cool-stuff", {base_url.to_string()});
    println!("{}", url);
    let mut post = client.post(url)
        .header("Content-Type", "application/json")
        .header("Accept", "application/json");

    if placement == "payload".to_string() {
        let payload = format!(r#"{{"uuid":"{0}","coolness":"max","timestamp":"right now","signature":"{signature}"}}"#, welcome_response.uuid);
        post = post.body(payload);
    } else {
        post = post.header("signature", signature)
        .body(message);
    }

    let result = post.send();
    let response = match result {
        Ok(resp) => resp,
        Err(_) => panic!("Something went awry!")
    };
    let json_result = response.json::<CoolnessResponse>();
    let coolness_response: CoolnessResponse = match json_result {
        Ok(cr) => cr,
        Err(_) => panic!("Error serializing JSON")
    };

    if coolness_response.double_cool == "double cool".to_string() {
        let success = format!("Aww yeah! The {color} server thinks you're {0}!", coolness_response.double_cool);
        println!("{}", success.to_string().green());
    } else {
        let fail = "Oh no, something went wrong.".red();
        println!("{}", fail.to_string());
    }
}
