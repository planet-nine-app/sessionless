from sawtooth_signing.secp256k1 import Secp256k1PublicKey, Secp256k1Context
from eth_hash.auto import keccak

class SessionlessSecp256k1():
    def __init__(self) -> None:
        pass

    def verifySignature(signature, message, publicKey):
        context = Secp256k1Context()
        messageHash = keccak(bytearray(message[:32]))
        hex = signature.r
        hex2 = signature.s
        if len(hex) % 2:
            hex = '0x' + hex
        if len(hex2) % 2:
            hex2 = '0x' + hex2
        hexBn = int(hex, 16)
        hex2Bn = int(hex2, 16)
    
        signatureHexBn = {
        "r": hexBn, 
        "s": hex2Bn
        }
    
        res = context.verify(signatureHexBn, messageHash, publicKey)
    