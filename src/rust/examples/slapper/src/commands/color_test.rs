use anyhow::anyhow;
use crate::requests;
use crate::utils::Color;

pub fn color_test(color: Option<Color>, _language: Option<String>, _iterations: Option<u32>) -> anyhow::Result<()> {
    let color = color.ok_or_else(|| anyhow!("Need a color for a color test!"))?;

    let register_tuple = requests::register(color)?;
    requests::do_cool_stuff(color, register_tuple.0, register_tuple.1)?;

    Ok(())
    /*    if cool_stuff_response.doubleCool != null {
            let success = "Aww yeah! The {language} server thinks you're {coolStuffResponse.doubleCool}".green();
            println!("{}", success.to_string());
        } else {
            let fail = "OH NO, THIS DIDN'T WORK".red();
            println!("{}", fail.to_string());
        }*/
}
