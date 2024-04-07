import secp256k1
import pickle
class SessionlessSecp256k1():
    def __init__(self, private_key_hex=secp256k1.PrivateKey().serialize()):
        private_key = secp256k1.PrivateKey()
        assert private_key.deserialize(private_key_hex) == private_key.private_key
        self.private_key = private_key
    
    def get_algorithm_name(self):
        return "secp256k1"
    
    def get_private_key(self, saveKeys, getKeys):
        try: 
            if callable(saveKeys) and callable(getKeys):
                return self.private_key.serialize()
        except Exception as e:
            raise TypeError("No default secure storage in python. Please provide a saveKeys and getKeys function to store private key. Internal error message: " + e)
        
    def update_private_key(self, new_private_key_hex):
        try:
            private_key = secp256k1.PrivateKey()
            assert private_key.deserialize(new_private_key_hex) == private_key.private_key
            self.private_key = private_key
            return "Updated private key value."
        except Exception as e:
            raise AttributeError("Private key unable to be assigned. Please provide a valid key in hex format and try again. Internal error message: " + e)
    
    def get_public_key_from_private_key(self):
        try:
            return self.private_key.pubkey.serialize().hex()
        except Exception as e:
            raise ValueError("Value not provided in correct format. Private key expected to be in hex format. Internal error message: " + e)
    
    def sign_message(self, msg):
        try:
            if not isinstance(msg, bytes):
                msg = pickle.dumps(msg)
            deserializedSig = self.private_key.ecdsa_sign(msg)
            sig = self.private_key.ecdsa_serialize_compact(deserializedSig)
            return sig.hex()
        except Exception as e:
            raise ValueError("Value not provided in correct format. Internal error message: " + e)
        
    def verify_signature(self, signature, msg, public_key_hex):
        try:
            if not isinstance(msg, bytes):
                msg = pickle.dumps(msg)
            sig = bytes.fromhex(signature)
            deserializedPublicKey = bytes.fromhex(public_key_hex)
            publicKey = secp256k1.PublicKey()
            publicKey.deserialize(deserializedPublicKey)
            signature = publicKey.ecdsa_deserialize_compact(sig)
            return publicKey.ecdsa_verify(msg, signature)
        except Exception as e:
            return ValueError("Error with parameters. Please ensure values are provided in correct format. Internal error message: " + e)
    
    def associate_message(self, primarySignature, primaryMessage, primaryPublicKey, secondarySignature, secondaryMessage, secondaryPublicKey ):
        try:
            return (self.verifySignature(primarySignature, primaryMessage, primaryPublicKey) and self.verifySignature(secondarySignature, secondaryMessage, secondaryPublicKey))
        except Exception as e:
            pass
    
