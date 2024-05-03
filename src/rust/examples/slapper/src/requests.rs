use std::fmt::Debug;
use serde::{Deserialize, Serialize};
use sessionless::{Sessionless, Signature};

mod register;
mod do_cool_stuff;

pub use register::*;
pub use do_cool_stuff::*;

trait Payload: Debug + Serialize {
    fn as_json(&self) -> String {
        serde_json::to_string(&self).unwrap()
    }

    fn sign(&self, sessionless: &Sessionless) -> Signature {
        let message_json = self.as_json();
        println!("Signing {}", message_json);
        sessionless.sign(message_json.as_bytes())
    }
}

trait Response: for<'a> Deserialize<'a> {}
