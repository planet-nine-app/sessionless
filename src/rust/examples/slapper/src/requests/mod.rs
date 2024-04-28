use sessionless::{Sessionless};
use reqwest::{Request, Response, Result};

mod register;
mod do_cool_stuff;
mod server_config;

pub fn register(color: Option<String>) -> (Sessionless, Result<T>) {
    let unwrapped_color = color.unwrap_or("no color".to_string());

    let base_url = server_config::url_for_color(unwrapped_color);
    let placement = server_config::signature_placement_for_color(unwrapped_color);

    return register::register(base_url, placement);
}

pub fn do_cool_stuff(color: String, SESSIONLESS: Sessionless, register_response: Result) {
    do_cool_stuff::do_cool_stuff(color, SESSIONLESS, register_response);
}
