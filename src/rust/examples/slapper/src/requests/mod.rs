use http::{Request, Response};

mod register;
mod do_cool_stuff;

pub fn register(color: Option<String>) {
    return register::register(color);
}

pub fn do_cool_stuff(register_response: Response) {
    return do_cool_stuff::do_cool_stuff(register_response);
}
