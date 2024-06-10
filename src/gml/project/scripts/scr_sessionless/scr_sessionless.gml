function Sessionless(private_key="") constructor {
	if private_key == "" or typeof(private_key) != "string" {
		id = slap_ctx_new();
	} else {
		id = slap_ctx_new_from_key(private_key);
	}
	
	static Sign = function(message) {
		return slap_ctx_sign(id, message);
	}
	
	static Verify = function(message, public_key, signature) {
		var result = slap_ctx_verify(id, message, public_key, signature);
		if result >= 1 {
			return true;	
		} else {
			return false;	
		}
	}
	
	static GetPublicKey = function() {
		return slap_ctx_get_key_public(id);	
	}
	
	static GetPrivateKey = function() {
		return slap_ctx_get_key_private(id);	
	}
	
	static Clean = function() {
        return slap_ctx_remove(id);
    }
}
