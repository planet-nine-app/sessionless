/// @description insert new message

message = get_string("Insert new message:", "");
signature = ctx.Sign(message);
is_verified = ctx.Verify(message, public_key, signature);
