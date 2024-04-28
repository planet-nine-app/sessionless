use sessionless::{Sessionless};
use reqwest::{Request, Response, Result};
use serde::{Deserialize};

mod register;
mod do_cool_stuff;
mod server_config;

#[derive(Deserialize)]
pub struct WelcomeResponse {
    uuid: String,
    welcomeMessage: String
}

#[derive(Deserialize)]
pub struct CoolnessResponse {
    doubleCool: String
}

pub fn register(color: &String) -> (Sessionless, WelcomeResponse) {
    let base_url = server_config::url_for_color(color);
    let placement = server_config::signature_placement_for_color(color);

    return register::register(base_url, placement);
}

pub fn do_cool_stuff(color: &String, SESSIONLESS: Sessionless, register_response: WelcomeResponse) {
    do_cool_stuff::do_cool_stuff(color, SESSIONLESS, register_response);
}
