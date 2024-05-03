#[derive(Copy, Clone, Debug, PartialEq, EnumString)]
#[strum(serialize_all = "snake_case")]
pub enum Color {
    Blue,
    Green,
    Red,
    Magenta,
}

impl Color {
    const SIG_PLACEMENT_PAYLOAD: &'static str = "payload";
    const SIG_PLACEMENT_HEADER: &'static str = "header";

    pub fn get_url(&self) -> &'static str {
        use Color::*;
        match self {
            Blue => "http://127.0.0.1:3001",
            Green => "http://127.0.0.1:3002",
            Red => "http://127.0.0.1:3000",
            Magenta => "http://127.0.0.1:5139",
        }
    }

    pub fn get_signature_placement(&self) -> &'static str {
        use Color::*;
        match self {
            Blue | Green => Self::SIG_PLACEMENT_PAYLOAD,
            Red | Magenta => Self::SIG_PLACEMENT_HEADER,
        }
    }
}
