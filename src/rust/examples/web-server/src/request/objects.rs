#[derive(Deserialize, Serialize)]
#[serde(rename_all="camelCase")]
pub struct PayloadRegister {
    pub public_key: String,
    pub timestamp: String,
    pub entered_text: String,
}

#[derive(Deserialize, Serialize)]
#[serde(rename_all="camelCase")]
pub struct PayloadCoolStuff {
    pub timestamp: String,
    pub coolness: String,
    pub uuid: String,
}
