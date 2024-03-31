use std::borrow::Cow;
use http_body_util::Full;
use hyper::body::Bytes;
use hyper::header::CONTENT_TYPE;
use erased_serde::Serialize;
use hyper::http::{HeaderName, HeaderValue};
use hyper::Response;

static JSON_EMPTY: &str = "{}";

pub struct Builder {
    builder: hyper::http::response::Builder,
    body: Option<Box<dyn Serialize + 'static + Send + Sync>>,
    body_raw: Option<Cow<'static, [u8]>>,
    pub status: u16,
}

impl Builder {
    pub fn new() -> Self {
        let builder = hyper::Response::builder()
            .header(CONTENT_TYPE, "application/json");

        Self {
            builder,
            body: None,
            body_raw: None,
            status: 200,
        }
    }

    pub fn set_body<T: Serialize + 'static + Send + Sync>(&mut self, body: T) -> &mut Self {
        self.body = Some(Box::new(body));
        self
    }

    pub fn set_body_raw_static(&mut self, body: &'static [u8]) -> &mut Self {
        self.body_raw = Some(Cow::Borrowed(body));
        self
    }

    #[allow(dead_code)]
    pub fn set_body_raw(&mut self, body: Vec<u8>) -> &mut Self {
        self.body_raw = Some(Cow::Owned(body));
        self
    }

    pub fn set_header(&mut self, header: HeaderName, value: HeaderValue) -> &mut Self {
        if let Some(map) = self.builder.headers_mut() {
            map.insert(header, value);
        }

        self
    }

    pub fn build(mut self) -> Response<Full<Bytes>> {
        self.builder = self.builder
            .status(self.status);

        if let Some(body_raw) = self.body_raw {
            return self.builder.body(Full::new(match body_raw {
                Cow::Borrowed(data) => Bytes::from_static(data),
                Cow::Owned(data) => Bytes::from(data),
            })).unwrap();
        }

        let body_json = self.body.map(|obj| {
            serde_json::to_string(obj.as_ref())
                .map(|json| Cow::Owned(json))
                .unwrap_or_else(|_| JSON_EMPTY.into())
        }).unwrap_or_else(|| JSON_EMPTY.into());

        let body = Full::new(match body_json {
            Cow::Borrowed(data) => Bytes::from_static(data.as_bytes()),
            Cow::Owned(data) => Bytes::from(data.into_bytes()),
        });

        self.builder.body(body).unwrap()
    }
}
