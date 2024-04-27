import secp256k1
import pickle
import uuid

class SessionlessSecp256k1():       
    def generateUUID(self):
        return uuid.uuid4().hex
    
    def generateKeys(self, saveKeys):
        try: 
            if not callable(saveKeys):
                raise Exception
            privateKeyObj = secp256k1.PrivateKey()
            privateKey = privateKeyObj.serialize()
            publicKey = privateKeyObj.pubkey.serialize().hex()
            saveKeys({"privateKey": privateKey, "publicKey": publicKey})
            return privateKey, publicKey
        except Exception:
            raise TypeError("No default secure storage in python. Please provide a callable method to store private key.")
    
    async def sign(self, msg, getKey):
        try:
            privateKey = getKey()
            if not isinstance(msg, bytes):
                msg = pickle.dumps(msg)
            privateKeyObj = secp256k1.PrivateKey()
            privateKeyObj.deserialize(privateKey)
            deserializedSig = privateKeyObj.ecdsa_sign(msg)
            sig = privateKeyObj.ecdsa_serialize_compact(deserializedSig)
            return sig.hex()
        except Exception:
            raise ValueError("Value not provided in correct format.")
        
    def verifySignature(self, signature, msg, publicKey):
        try:
            if not isinstance(msg, bytes):
                msg = pickle.dumps(msg)
            sig = bytes.fromhex(signature)
            publicKeyObj = secp256k1.PublicKey()
            publicKeyObj.deserialize(bytes.fromhex(publicKey))
            
            signature = publicKeyObj.ecdsa_deserialize_compact(sig)
            return publicKeyObj.ecdsa_verify(msg, signature)
        except Exception:
            raise ValueError("Error with parameters. Please ensure values are provided in correct format.")
    
    def associate(self, primarySignature, primaryMsg, primaryPublicKey, secondarySignature, secondaryMsg, secondaryPublicKey ):
        try:
            return (self.verifySignature(primarySignature, primaryMsg, primaryPublicKey) and self.verifySignature(secondarySignature, secondaryMsg, secondaryPublicKey))
        except Exception:
            raise ValueError("Error with parameters. Please ensure values are provided in correct format.")
