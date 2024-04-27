use Sessionless::sign;
use http::{Request, Response};

mod server_config;

pub fn register(color: Option<String>) -> Response {
    let unwrapped_color = color.unwrap_or("no color".to_string());

    let base_url = server_config::url_for_color(color);
    let placement = server_config::signature_placement_for_color(color);
    
}
