use anyhow::anyhow;
use sessionless::Sessionless;
use crate::requests::register::RegisterRequest;
use crate::requests::*;
use crate::requests::do_cool_stuff::CoolRequest;
use crate::utils::Color;

pub fn color_test(color: Option<Color>, _language: Option<String>, _iterations: Option<u32>) -> anyhow::Result<()> {
    let color = color.ok_or_else(|| anyhow!("Need a color for a color test!"))?;
    let sessionless = Sessionless::new();

    let register_response = RegisterRequest::execute(color, &sessionless, ())?;
    CoolRequest::execute(color, &sessionless, register_response)?;

    Ok(())
}
