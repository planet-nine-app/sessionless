#[derive(Deserialize, Serialize)]
#[serde(rename_all="camelCase")]
pub struct PayloadRegister {
    pub public_key: String,
    pub timestamp: String,
    pub entered_text: String,
    #[serde(skip_serializing)]
    pub signature: String,
}

#[derive(Deserialize, Serialize)]
#[serde(rename_all="camelCase")]
pub struct PayloadCoolStuff {
    pub timestamp: String,
    pub coolness: String,
    #[serde(skip_serializing)]
    pub uuid: String,
    #[serde(skip_serializing)]
    pub signature: String,
}
