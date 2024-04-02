use hyper::header::CONTENT_TYPE;
use hyper::http::HeaderValue;
use super::*;

macro_rules! include_resource {
    ($builder:ident, $path:literal, $mime:literal) => {
        {
            $builder.set_header(CONTENT_TYPE, HeaderValue::from_static($mime));
            include_bytes!($path).as_slice()
        }
    };
}

pub async fn load(head: &Parts, _body: Incoming, builder: &mut response::Builder) -> anyhow::Result<()> {
    let path = head.uri.path();

    let data = match path {
        "/client" => include_resource!(builder, "../../frontend/pages/client.html", "text/html"),
        "/web_client.js" => include_resource!(builder, "../../frontend/libs/web_client.js", "text/javascript"),
        "/web_client_bg.wasm" => include_resource!(builder, "../../frontend/libs/web_client_bg.wasm", "application/wasm"),
        _ => return Err(anyhow!("Resource not found!")),
    };

    builder.status = 200;
    builder.set_body_raw_static(data);
    Ok(())
}
