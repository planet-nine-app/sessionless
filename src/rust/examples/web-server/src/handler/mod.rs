mod api;
mod resources;

use api::*;

use std::convert::Infallible;
use anyhow::anyhow;
use http_body_util::Full;
use hyper::body::{Bytes, Incoming};
use hyper::Response;
use hyper::http::request::Parts;
use crate::response;

pub async fn service(request: hyper::Request<Incoming>) -> Result<Response<Full<Bytes>>, Infallible> {
    let mut builder = response::Builder::new();

    let (head, body) = request.into_parts();

    let path = head.uri.path();
    let api_result = match path {
        "/register" => Register::handle(&head, body, &mut builder).await,
        "/cool-stuff" => CoolStuff::handle(&head, body, &mut builder).await,
        _ => {
            if resources::load(&head, body, &mut builder).await.is_ok() {
                return Ok(builder.build());
            }

            builder.status = 404;
            Err(anyhow!("Endpoint '{}' not found!", path))
        }
    };

    match api_result {
        Ok(_) => {}
        Err(err) => {
            if builder.status < 400 || builder.status > 599 {
                builder.status = 500;
            }

            builder.set_body(response::Error {
                status: builder.status,
                message: format!("{}", err),
            });
        }
    }

    Ok(builder.build())
}
