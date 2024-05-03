#[macro_use]
extern crate strum_macros;

use std::str::FromStr;
use clap::{Parser, Subcommand};
use crate::utils::Color;

mod commands;
mod utils;
mod requests;

#[derive(Parser)]
#[command(version, about, long_about = None)]
struct Cli {
    /// Optional color to test
    #[arg(short, long)]
    color: Option<String>,

    /// Optional language to test
    #[arg(short, long)]
    language: Option<String>,

    /// Iterations to test
    #[arg(short, long)]
    iterations: Option<u32>,

    #[command(subcommand)]
    command: Option<Commands>,
}

#[derive(Subcommand)]
enum Commands {
    /// does the color test
    Test,
    Lots 
}

// Never used function, I am not sure of its purpose - FssAy
#[allow(dead_code)]
fn test(color: Option<String>, language: Option<String>, iterations: Option<i32>) {
    let col = color.unwrap_or("foo".to_string());
    let lang = language.unwrap_or("foo".to_string());
    let iter = iterations.unwrap_or(42);
    println!("color: {col}");
    println!("language: {lang}");
    println!("iterations: {iter}");
}

fn main() {
    let cli = Cli::parse();

    // IDK how `clap` works, so I'll parse all the values here
    // instead of implementing `Parse` trait. - FssAy

    let color = cli
        .color
        .map(|value| Color::from_str(&*value)
            .expect("Provided value is not an accepted Color!")
        );

    match &cli.command {
        Some(Commands::Test) => {
            commands::color_test(color, cli.language, cli.iterations);
        },
        Some(Commands::Lots) => {
            commands::lots(color, cli.language, cli.iterations);
        },
        None => {}
    }

    // Continued program logic goes here...
}
