use crate::utils::Placement;

#[derive(Copy, Clone, Debug, PartialEq, EnumString)]
#[strum(serialize_all = "snake_case")]
pub enum Color {
    Blue,
    Green,
    Red,
    Magenta,
}

impl Color {
    pub fn get_url(&self) -> &'static str {
        use Color::*;
        match self {
            Blue => "http://127.0.0.1:3001",
            Green => "http://127.0.0.1:3002",
            Red => "http://127.0.0.1:3000",
            Magenta => "http://127.0.0.1:5139",
        }
    }

    pub fn get_signature_placement(&self) -> Placement {
        use Color::*;
        match self {
            Blue | Green => Placement::Payload,
            Red | Magenta => Placement::Header,
        }
    }
}
