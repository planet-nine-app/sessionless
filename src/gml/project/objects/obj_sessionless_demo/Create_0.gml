// Create new context object
ctx = new Sessionless();

// It's possible to see if the context was created successfully by checking
// if the `id` is `0`.
// This should never happen on new random context, but can occur when invalid
// private key was passed to the constructor.
if ctx.id == 0 {
	throw ("Failed to create Sessionless context");	
}

private_key = ctx.GetPrivateKey();
public_key = ctx.GetPublicKey();

message = "Hello Sessionless!";
signature = ctx.Sign(message);
is_verified = ctx.Verify(message, public_key, signature);

// Draw event
y_new_line = 18;
y_new_section = 30;
