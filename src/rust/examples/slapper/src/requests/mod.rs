use serde::{Deserialize};

mod register;
mod do_cool_stuff;

pub use register::*;
pub use do_cool_stuff::*;

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
