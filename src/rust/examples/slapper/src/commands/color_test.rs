use colored::Colorize;

mod requests;

/*async*/ pub fn color_test(color: Option<String>, language: Option<String>, iterations: Option<i32>) {
    let col = color.unwrap_or("foo".to_string());
    let lang = language.unwrap_or("foo".to_string());
    let iter = iterations.unwrap_or(42);
    println!("color: {col}".green());
    println!("language: {lang}");
    println!("iterations: {iter}");

    let register_response = requests::register(color);
    let cool_stuff_response = requests::do_cool_stuff(registerResponse);
    if(cool_stuff_response.doubleCool) {
        println!("Aww yeah! The {language} server thinks you're {coolStuffResponse.doubleCool}".green());
    } else {
        println!("OH NO, THIS DIDN'T WORK");
    }
}
