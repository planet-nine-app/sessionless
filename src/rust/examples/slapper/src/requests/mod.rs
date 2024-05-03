use sessionless::{Sessionless};
use serde::{Deserialize};
use crate::utils::Color;

mod register;
mod do_cool_stuff;

#[derive(Deserialize)]
#[serde(rename_all="camelCase")]
pub struct WelcomeResponse {
    uuid: String,
    #[allow(dead_code)]  // todo: Find out why this field is never used.
    welcome_message: String
}

#[derive(Deserialize)]
#[serde(rename_all="camelCase")]
pub struct CoolnessResponse {
    double_cool: String
}

pub fn register(color: Color) -> (Sessionless, WelcomeResponse) {
    return register::register(
        color.get_url().to_string(),
        color.get_signature_placement().to_string()
    );
}

pub use do_cool_stuff::do_cool_stuff;
