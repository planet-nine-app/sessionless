import secp256k1

class SessionlessSecp256k1():
    def __init__(self) -> None:
        pass
    
    def generateRandomPrivateKeyHex(self, saveKeys, getKeys):
        if callable(saveKeys) and callable(getKeys):
            return secp256k1.PrivateKey().serialize().hex()
        return 'Not compatiable with...'
    
    def generateRandomPrivateKeySerialized(self, saveKeys, getKeys):
        if callable(saveKeys) and callable(getKeys):
            return secp256k1.PrivateKey().serialize()
        return 'Not compatiable with...'
    
    def generatePublicKeyFromPrivateKeyHex(self, privateKey):
        if isinstance(privateKey, str) and all(c in '0123456789abcdefABCDEF' for c in privateKey):
            deserializedPrivateKey = bytes.fromhex(privateKey)
            newPrivateKey = secp256k1.PrivateKey()
            newPrivateKey.deserialize(deserializedPrivateKey)
            return newPrivateKey.pubkey.serialize().hex()
        raise ValueError("Value not provided in correct format. Private key expected to be in hex format.")
    
    def generatePublicKeyFromPrivateKeySerialized(self, privateKey):
        newPrivateKey = secp256k1.PrivateKey()
        newPrivateKey.deserialize(privateKey)
        return newPrivateKey.pubkey.serialize().hex()
    
    def verifySignature(self, signature, message, publicKey):
        pass
      
print(secp256k1.PrivateKey().pubkey.serialize().hex())

    