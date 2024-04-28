use colored::Colorize;

use crate::requests;

/*async*/ pub fn color_test(color: Option<String>, language: Option<String>, iterations: Option<i32>) {
    let register_tuple = requests::register(color);
    requests::do_cool_stuff(color.unwrap_or("".to_string()), register_tuple.0, register_tuple.1);
/*    if cool_stuff_response.doubleCool != null {
        let success = "Aww yeah! The {language} server thinks you're {coolStuffResponse.doubleCool}".green();
        println!("{}", success.to_string());
    } else {
        let fail = "OH NO, THIS DIDN'T WORK".red();
        println!("{}", fail.to_string());
    }*/
}
