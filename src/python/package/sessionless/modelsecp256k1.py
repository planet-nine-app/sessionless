import secp256k1
import pickle
import uuid
class SessionlessSecp256k1():
    
    def __init__(self, private_key_hex=None):
        if private_key_hex is not None:
            private_key_hex = secp256k1.PrivateKey().serialize()
        private_key = secp256k1.PrivateKey()
        assert private_key.deserialize(private_key_hex) == private_key.private_key
        self.__private_key = private_key
    
    def generateUUID(self):
        return uuid.uuid4().hex
    
    def generate_keys(self, saveKeys, getKeys):
        try: 
            if callable(saveKeys) and callable(getKeys):
                private_key = self.__private_key.serialize()
                public_key = self.__private_key.pubkey.serialize().hex()
                return private_key, public_key
        except Exception as e:
            raise TypeError("No default secure storage in python. Please provide a saveKeys and getKeys function to store private key. Internal error message: " + e)
    
    def sign(self, msg):
        try:
            if not isinstance(msg, bytes):
                msg = pickle.dumps(msg)
            deserializedSig = self.__private_key.ecdsa_sign(msg)
            sig = self.__private_key.ecdsa_serialize_compact(deserializedSig)
            return sig.hex()
        except Exception as e:
            raise ValueError("Value not provided in correct format. Internal error message: " + e)
        
    def verify_signature(self, signature, msg, public_key_hex=None):
        try:
            if not isinstance(msg, bytes):
                msg = pickle.dumps(msg)
            sig = bytes.fromhex(signature)
            if public_key_hex is not None:
                public_key = secp256k1.PublicKey()
                public_key.deserialize(bytes.fromhex(public_key_hex))
            else:
                public_key = self.__private_key.pubkey
            signature = public_key.ecdsa_deserialize_compact(sig)
            return public_key.ecdsa_verify(msg, signature)
        except Exception as e:
            raise ValueError("Error with parameters. Please ensure values are provided in correct format. Internal error message: " + e)
    
    def associate(self, primary_sig, primary_msg, primary_public_key, secondary_sig, secondary_msg, secondary_public_key ):
        try:
            return (self.verifySignature(primary_sig, primary_msg, primary_public_key) and self.verifySignature(secondary_sig, secondary_msg, secondary_public_key))
        except Exception as e:
            raise ValueError("Error with parameters. Please ensure values are provided in correct format. Internal error message: " + e)




