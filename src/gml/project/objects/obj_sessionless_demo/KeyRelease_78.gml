/// @description create new random Sessionless context

ctx.Clean();

ctx = new Sessionless();

private_key = ctx.GetPrivateKey();
public_key = ctx.GetPublicKey();

signature = ctx.Sign(message);
is_verified = ctx.Verify(message, public_key, signature);
