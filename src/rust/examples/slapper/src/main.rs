#[macro_use]
extern crate strum_macros;

#[macro_use]
extern crate log;

use std::str::FromStr;
use anyhow::anyhow;
use clap::{Parser, Subcommand};
use simple_logger::SimpleLogger;
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

fn main() {
    SimpleLogger::new().init().expect("Failed to initialize the logger!");
    if let Err(err) = core() {
        error!("{err}");
    }
}

fn core() -> anyhow::Result<()> {
    let cli = Cli::parse();

    // IDK how `clap` works, so I'll parse all the values here
    // instead of implementing `Parse` trait. - FssAy

    let color = if let Some(color) = cli.color {
        Some(Color::from_str(&*color)?)
    } else {
        None
    };

    match &cli.command {
        Some(Commands::Test) => {
            commands::color_test(color, cli.language, cli.iterations)
        },
        Some(Commands::Lots) => {
            commands::lots(color, cli.language, cli.iterations)
        },
        None => Err(
            anyhow!("No command provided!")
        ),
    }
    // Continued program logic goes here...
}
