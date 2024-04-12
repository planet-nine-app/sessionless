import secp256k1
import pickle
import uuid
import inspect


class SessionlessSecp256k1():
    
    def __init__(self, get_keys):
        self.get_keys = get_keys
        
    def generate_UUID(self):
        return uuid.uuid4().hex
    
    def generate_keys(self, saveKeys):
        try: 
            if callable(saveKeys):
                private_key_obj = secp256k1.PrivateKey()
                private_key = private_key_obj.serialize()
                public_key = private_key_obj.pubkey.serialize().hex()
                return private_key, public_key
        except Exception:
            raise TypeError("No default secure storage in python. Please provide a saveKeys and getKeys function to store private key. Internal error message: ")
    
    async def sign(self, msg):
        try:
            if not isinstance(msg, bytes):
                msg = pickle.dumps(msg)
            if inspect.iscoroutinefunction(self.get_keys):
                private_key_hex = await self.get_keys()
            else: 
                private_key_hex = await self.get_keys()
            private_key = secp256k1.PrivateKey()
            assert private_key.deserialize(private_key_hex) == private_key.private_key
            deserializedSig = private_key.ecdsa_sign(msg)
            sig = private_key.ecdsa_serialize_compact(deserializedSig)
            return sig.hex()
        except Exception:
            raise ValueError("Value not provided in correct format. Internal error message: ")
        
    def verify_signature(self, signature, msg, public_key_hex):
        try:
            if not isinstance(msg, bytes):
                msg = pickle.dumps(msg)
            sig = bytes.fromhex(signature)
            public_key = secp256k1.PublicKey()
            assert public_key.deserialize(bytes.fromhex(public_key_hex)) == public_key.public_key
            
            signature = public_key.ecdsa_deserialize_compact(sig)
            return public_key.ecdsa_verify(msg, signature)
        except Exception:
            raise ValueError("Error with parameters. Please ensure values are provided in correct format. Internal error message: ")
    
    def associate(self, primary_sig, primary_msg, primary_public_key, secondary_sig, secondary_msg, secondary_public_key ):
        try:
            return (self.verify_signature(primary_sig, primary_msg, primary_public_key) and self.verify_signature(secondary_sig, secondary_msg, secondary_public_key))
        except Exception:
            raise ValueError("Error with parameters. Please ensure values are provided in correct format. Internal error message: ")
