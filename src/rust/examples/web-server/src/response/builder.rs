use std::borrow::Cow;
use http_body_util::Full;
use hyper::body::Bytes;
use hyper::header::CONTENT_TYPE;
use erased_serde::Serialize;
use hyper::Response;

static JSON_EMPTY: &str = "{}";

pub struct Builder {
    builder: hyper::http::response::Builder,
    body: Option<Box<dyn Serialize + 'static + Send + Sync>>,
    pub status: u16,
}

impl Builder {
    pub fn new() -> Self {
        let builder = hyper::Response::builder()
            .header(CONTENT_TYPE, "application/json");

        Self {
            builder,
            body: None,
            status: 200,
        }
    }

    pub fn set_body<T: Serialize + 'static + Send + Sync>(&mut self, body: T) -> &mut Self {
        self.body = Some(Box::new(body));
        self
    }

    pub fn build(self) -> Response<Full<Bytes>> {
        let body_json = self.body.map(|obj| {
            serde_json::to_string(obj.as_ref())
                .map(|json| Cow::Owned(json))
                .unwrap_or_else(|_| JSON_EMPTY.into())
        }).unwrap_or_else(|| JSON_EMPTY.into());

        let body = Full::new(match body_json {
            Cow::Borrowed(data) => Bytes::from_static(data.as_bytes()),
            Cow::Owned(data) => Bytes::from(data.into_bytes()),
        });

        self.builder
            .status(self.status)
            .body(body)
            .unwrap()
    }
}
