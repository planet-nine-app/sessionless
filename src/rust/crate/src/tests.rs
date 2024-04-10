use crate::hex::IntoHex;
use crate::Sessionless;

#[test]
fn self_check() {
    let message = "test";
    let sl = Sessionless::new();
    let sig = sl.sign(message);

    sl.verify(message, sl.public_key(), &sig)
        .expect("Couldn't verify the self signed message!");

    let message_tampered = "tset";
    sl.verify(message_tampered, sl.public_key(), &sig)
        .expect_err("Verified tampered self signed message!");

    let sl2 = Sessionless::from_private_key(*sl.private_key());
    sl2.verify(
        message,
        sl2.public_key(),
        &sig
    ).expect("Couldn't verify the self signed message after creating another context from the same private key!");
}

#[test]
fn peer_check() {
    let sender = Sessionless::new();
    let receiver = Sessionless::new();

    // whole package that is sent to the receiver
    let message = "<SOME PAYLOAD FROM SENDER>";
    let sig = sender.sign(message);
    let pub_key = sender.public_key();

    receiver
        .verify(message, pub_key, &sig)
        .expect("Receiver couldn't verify message signed by the Sender!");

    let message_tampered = "<SOME TAMPERED PAYLOAD>";
    receiver
        .verify(message_tampered, pub_key, &sig)
        .expect_err("Receiver verify tampered message signed by the Sender!");
}

#[test]
fn hex_encoding() {
    let sl = Sessionless::new_predefined();

    assert_eq!(
        sl.private_key.to_hex(),
        "0000000000000000010000000000000002000000000000000300000000000000",
    );

    assert_eq!(
        sl.public_key.to_hex(),
        "03250075d2c40b5e242a2f738802ade3bc32bf35ff8ef26a15b6c3f879312906a4",
    );

    assert_eq!(
        sl.sign("123").into_hex(),
        "30440220753bfda8fdfbc134cdd76cb52041488f13942f3aca558aa460c555b0a6561c99022014117bce0a29d70d6c1ac63c42421143568aa43df97fa4915f0ea7a0e6580037",
    );
}
