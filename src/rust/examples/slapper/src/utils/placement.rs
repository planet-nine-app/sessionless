#[derive(Copy, Clone, Debug, PartialEq, EnumString)]
#[strum(serialize_all = "snake_case")]
pub enum Placement {
    Payload,
    Header,
}
