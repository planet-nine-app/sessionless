# Sessionless

[*Sessionless*](https://www.github.com/planet-nine-app/sessionless) is a multi-language implementation of public-key cryptography.
It's trying to be like the cordless drill or sewing machine of authentication--a tool everyone can have in their house t
o help them make things.

Rust implementation of the [Sessionless](https://sessionless.org) protocol.

> Sessionless is an authentication protocol that uses the cryptography employed by Bitcoin and Ethereum to authenticate messages sent between a client and a server.

This crate provides a context to simplify the cryptography operations behind the protocol for communication with other Sessionless instances.

### Features
- `uuid` - Enables random UUID generation with `Sessionless::generate_uuid`.

### Examples
All usage examples can be found [here](https://github.com/planet-nine-app/sessionless/tree/main/src/rust/examples).
