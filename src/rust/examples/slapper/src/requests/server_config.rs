
pub fn url_for_color(color: String) -> String {
    return match color {
        "blue" => "http://127.0.0.1:3001",
        "green" => "http://127.0.0.1:3002",
        "red" => "http://127.0.0.1:3000",
        "magenta" => "http://127.0.0.1:5139",
    }
}

pub fn signature_placement_for_color(color: String) -> String {
    return match color {
        "blue" => "payload",
        "green" => "payload",
        "red" => "header",
        "magenta" => "header",
    }
}
