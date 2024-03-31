#[derive(Serialize)]
pub struct Error {
    pub status: u16,
    pub message: String,
}

#[derive(Serialize)]
#[serde(rename_all="camelCase")]
pub struct Register {
    pub uuid: String,
    pub welcome_message: String,
}

#[derive(Serialize)]
#[serde(rename_all="camelCase")]
pub struct CoolStuff {
    pub double_cool: String,
}
