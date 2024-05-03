
pub fn url_for_color(color: &String) -> String {
    match color.as_str() {
        "blue" => "http://127.0.0.1:3001".to_string(),
        "green" => "http://127.0.0.1:3002".to_string(),
        "red" => "http://127.0.0.1:3000".to_string(),
        "magenta" => "http://127.0.0.1:5139".to_string(),
        color => {
            // IDK what to do on an invalid color - FssAy
            panic!("Invalid color [{}]!", color);
        }
    }
}

pub fn signature_placement_for_color(color: &String) -> String {
    match color.as_str() {
        "blue" => "payload".to_string(),
        "green" => "payload".to_string(),
        "red" => "header".to_string(),
        "magenta" => "header".to_string(),
        _ => {
            // IDK what to do on an invalid color - FssAy
            "payload".to_string()
        }
    }
}
