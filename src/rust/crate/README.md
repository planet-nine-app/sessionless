# Sessionless

Rust implementation of the [Sessionless](https://github.com/planet-nine-app/sessionless) protocol.

> Sessionless is an authentication protocol that uses the cryptography employed by Bitcoin and Ethereum to authenticate messages sent between a client and a server.

This crate provides a context to simplify the cryptography operations behind the protocol for communication with other Sessionless instances.

### Features
- `uuid` - Enables random UUID generation with `Sessionless::generate_uuid`.
