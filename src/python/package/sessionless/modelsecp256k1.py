import secp256k1
import pickle
import uuid
from Crypto.Hash import keccak

class DigestableKeccak(): # I don't know if a class is the right way to do this
    def digest(self, msg):
        k = keccak.new(digest_bits=256)
        k.update(msg)
        return k

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
                msg = msg.encode('ascii')
            privateKeyObj = secp256k1.PrivateKey()
            privateKeyObj.deserialize(privateKey)
   
            digestable_keccak = DigestableKeccak()
            print(msg[0])
            print(msg[1])
            raw_msg = digestable_keccak.digest(msg)
            deserializedSig = privateKeyObj.ecdsa_sign(raw_msg.digest(), raw=True)
#            deserializedSig = privateKeyObj.ecdsa_sign(msg, digest=digestable_keccak.digest)
            sig = privateKeyObj.ecdsa_serialize_compact(deserializedSig)
            return sig.hex()
        except Exception:
            raise ValueError("Value not provided in correct format.")
        
    def verifySignature(self, signature, msg, publicKey):
        try:
            if not isinstance(msg, bytes):
                msg = msg.encode('ascii')
            sig = bytes.fromhex(signature)
            publicKeyObj = secp256k1.PublicKey()
            publicKeyObj.deserialize(bytes.fromhex(publicKey))

            digestable_keccak = DigestableKeccak()
            raw_msg = digestable_keccak.digest(msg)
            signature = publicKeyObj.ecdsa_deserialize_compact(sig)

#            return publicKeyObj.ecdsa_verify(msg, signature)
            return publicKeyObj.ecdsa_verify(raw_msg.digest(), signature, raw=True)
        except Exception:
            raise ValueError("Error with parameters. Please ensure values are provided in correct format.")
    
    def associate(self, primarySignature, primaryMsg, primaryPublicKey, secondarySignature, secondaryMsg, secondaryPublicKey ):
        try:
            return (self.verifySignature(primarySignature, primaryMsg, primaryPublicKey) and self.verifySignature(secondarySignature, secondaryMsg, secondaryPublicKey))
        except Exception:
            raise ValueError("Error with parameters. Please ensure values are provided in correct format.")
