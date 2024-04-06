import secp256k1

class SessionlessSecp256k1():
    def __init__(self):
        pass
    
    def generateRandomPrivateKey(self, saveKeys, getKeys):
        try: 
            if callable(saveKeys) and callable(getKeys):
                return secp256k1.PrivateKey().serialize().hex()
        except Exception as e:
            return ""
    
    def generatePublicKeyFromPrivateKey(self, privateKeyHex):
        try:
            if isinstance(privateKeyHex, str) and all(c in '0123456789abcdefABCDEF' for c in privateKey):
                deserializedPrivateKey = bytes.fromhex(privateKeyHex)
                privateKey = secp256k1.PrivateKey()
                privateKey.deserialize(deserializedPrivateKey)
                return privateKey.pubkey.serialize().hex()
        except Exception as e:
            raise ValueError("Value not provided in correct format. Private key expected to be in hex format.")
    
    def sign(self, message, privateKeyHex):
        try:
            deserializedPrivateKey = bytes.fromhex(privateKeyHex)
            privateKey = secp256k1.PrivateKey()
            privateKey.deserialize(deserializedPrivateKey)
            deserializedSignature = privateKey.ecdsa_sign(message)
            signature = privateKey.ecdsa_serialize_compact(deserializedSignature)
            return signature.hex()
        except Exception as e:
            pass
    
    def verifySignature(self, signature, message, publicKeyHex):
        try:
            sig = bytes.fromhex(signature)
            deserializedPublicKey = bytes.fromhex(publicKeyHex)
            publicKey = secp256k1.PublicKey()
            publicKey.deserialize(deserializedPublicKey)
            signature = publicKey.ecdsa_deserialize_compact(sig)
            return publicKey.ecdsa_verify(message, signature)
        except Exception as e:
            return e
    
    def associate(self ):
        try:
            pass
        except Exception as e:
            pass
    
    