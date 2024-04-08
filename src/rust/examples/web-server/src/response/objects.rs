#[derive(Serialize)]
pub struct Error {
    pub status: u16,
    pub message: String,
}
