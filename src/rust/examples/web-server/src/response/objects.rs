#[derive(Serialize)]
pub struct Error {
    pub status: u16,
    pub message: String,
}

#[derive(Deserialize, Serialize)]
pub struct ResponseSuccess {
    pub success: bool,
}
