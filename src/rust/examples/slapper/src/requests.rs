use std::fmt::Debug;
use anyhow::anyhow;
use reqwest::header::{ACCEPT, CONTENT_TYPE};
use reqwest::{blocking::RequestBuilder};
use serde::{Deserialize, Serialize};
use sessionless::{Sessionless, Signature};
use sessionless::hex::IntoHex;

pub mod register;
pub mod do_cool_stuff;

use crate::utils::{Color, Placement};

pub trait Request {
    type Input;
    type Output;

    fn endpoint() -> &'static str;

    fn execute(color: Color, sessionless: &Sessionless, input: Self::Input) -> anyhow::Result<Self::Output>;

    fn prepare<P: Payload>(mut payload: P, color: Color, sessionless: &Sessionless) -> anyhow::Result<RequestBuilder> {
        let signature = payload.sign(&sessionless);

        let mut request_builder = reqwest::blocking::Client::new()
            .post({
                let url = format!("{}/{}", color.get_url(), Self::endpoint());
                debug!("{}", url);
                url
            })
            .header(CONTENT_TYPE, "application/json")
            .header(ACCEPT, "application/json");

        request_builder = match color.get_signature_placement() {
            Placement::Payload => {
                payload.update_signature(Some(signature));
                request_builder
                    .body(payload.as_json())
            }
            Placement::Header => {
                request_builder
                    .header("signature", signature.to_hex())
                    .body(payload.as_json())
            }
        };

        Ok(request_builder)
    }

    fn send<R: Response>(request_builder: RequestBuilder) -> anyhow::Result<R> {
        request_builder
            .send()
            .map_err(|err| anyhow!("{} request - Failure: {}", Self::endpoint(), err))?
            .json::<R>()
            .map_err(|err| anyhow!("{} request - Invalid JSON response: {}", Self::endpoint(), err))
    }
}

pub trait Payload: Debug + Serialize {
    fn update_signature(&mut self, signature: Option<Signature>);

    fn as_json(&self) -> String {
        serde_json::to_string(&self).unwrap()
    }

    fn sign(&self, sessionless: &Sessionless) -> Signature {
        let message_json = self.as_json();
        debug!("Signing {}", message_json);
        sessionless.sign(message_json.as_bytes())
    }
}

pub trait Response: for<'a> Deserialize<'a> {}
