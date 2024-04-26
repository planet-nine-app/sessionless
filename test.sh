#!/bin/bash
cd src/rust/crate
cargo test
cd ../../..
cd src/javascript/node
npm i && npx mocha
cd ../../..

