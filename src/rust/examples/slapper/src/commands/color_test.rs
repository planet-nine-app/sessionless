use colored::Colorize;

use crate::requests;

/*async*/ pub fn color_test(color: Option<String>, language: Option<String>, iterations: Option<u32>) {
    let unwrapped_color = color.unwrap_or("no color".to_string());
    
    if unwrapped_color == "no color" {
        panic!("Need a color for a color test");
    }

    let register_tuple = requests::register(&unwrapped_color);
    requests::do_cool_stuff(&unwrapped_color, register_tuple.0, register_tuple.1);
/*    if cool_stuff_response.doubleCool != null {
        let success = "Aww yeah! The {language} server thinks you're {coolStuffResponse.doubleCool}".green();
        println!("{}", success.to_string());
    } else {
        let fail = "OH NO, THIS DIDN'T WORK".red();
        println!("{}", fail.to_string());
    }*/
}
