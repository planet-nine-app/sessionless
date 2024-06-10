/// @description build new Sessionless context

var new_private_key = get_string("Insert new private key:", private_key);

var new_ctx = new Sessionless(new_private_key);

if new_ctx.id == 0 {
	show_message("Invalid private key!");
} else {
	ctx.Clean();
	
	ctx = new_ctx;
	
	private_key = ctx.GetPrivateKey();
	public_key = ctx.GetPublicKey();
	
	signature = ctx.Sign(message);
	is_verified = ctx.Verify(message, public_key, signature);
}
