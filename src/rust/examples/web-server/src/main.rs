#[macro_use]
extern crate serde;

#[macro_use]
extern crate lazy_static;

mod handler;
mod response;

use hyper::server::conn::http1;
use hyper::service::service_fn;
use hyper_util::rt::TokioIo;
use sessionless::Sessionless;
use std::net::SocketAddr;
use tokio::net::TcpListener;
use tokio::sync::OnceCell;

static SESSIONLESS: OnceCell<Sessionless> = OnceCell::const_new();

#[tokio::main]
async fn main() -> anyhow::Result<()> {
    let addr = SocketAddr::from(([127, 0, 0, 1], 3000));
    let listener = TcpListener::bind(addr).await?;

    let _ = SESSIONLESS.set(Sessionless::new());

    loop {
        let (stream, _) = listener.accept().await?;
        let io = TokioIo::new(stream);

        tokio::task::spawn(async move {
            if let Err(err) = http1::Builder::new()
                .serve_connection(io, service_fn(handler::service))
                .await
            {
                eprintln!("Error serving connection: {:?}", err);
            }
        });
    }
}
