
pub fn url_for_color(color: &String) -> String {

    let blue = "blue".to_string();
    let green = "green".to_string();
    let red = "red".to_string();
    let magenta = "magenta".to_string();

    return match color {
        blue => "http://127.0.0.1:3001".to_string(),
        green => "http://127.0.0.1:3002".to_string(),
        red => "http://127.0.0.1:3000".to_string(),
        magenta => "http://127.0.0.1:5139".to_string(),
    }
}

pub fn signature_placement_for_color(color: &String) -> String {

    let blue = "blue".to_string();
    let green = "green".to_string();
    let red = "red".to_string();
    let magenta = "magenta".to_string();

    return match color {
        blue => "payload".to_string(),
        green => "payload".to_string(),
        red => "header".to_string(),
        magenta => "header".to_string(),
    }
}
