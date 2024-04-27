import unittest
from sessionless import SessionlessSecp256k1

PRIVATE_KEY_1 = "8dea0f7f726273b6971c1a4bcc3d60d8006fc087a4b9c4e7ae633918dff54f26"
PRIVATE_KEY_2 = "68eab2d2fae06bbd24d633ecb72d9a8d6c9e2de0aef7499028d471b8ad4cb204"

PUBLIC_KEY_1 = "036c2d503304b96b59bfb5f40e7d8b7f84646c21923a2eb19108b84afbd808f317"
PUBLIC_KEY_2 = "03a68885996743ac828dcb3d7747cb1758bca819409f86dfd017acac1eda98fd69"


MSG = "Test"

SIG_1 = "34cd9384267027bbaedff2727252a52d77aee14f266594584a18d3d69e3f0b8b1a1e597fe428829e4ef850cfd6c032212e6e2eb35827056d8fd403a5a571939a"
SIG_2 = "db1ac1ad92ad24c0cfd89f055250c5e6c187d859c134016fed1d7c631260823146d01c3f5a082fde987d413c58f8c04faa5f6da00b99d003d1d3a691deedba6b"

def getKey(privateKey):
        return privateKey

def saveKey(object):
        pass

class SessionlessSecp256k1Test(unittest.TestCase):
   
        def test_generate_keys(self):
                slap = SessionlessSecp256k1()
                private_key, public_key = slap.generateKeys(saveKey)
                self.assertIsNotNone(private_key)
                self.assertIsNotNone(public_key)
                
                slap2 = SessionlessSecp256k1()
                private_key2, public_key2 = slap2.generateKeys(saveKey)
                self.assertIsNotNone(private_key2)
                self.assertIsNotNone(public_key2)
                
        
        async def test_sign(self):
                slap = SessionlessSecp256k1()
                result = await slap.sign(MSG, getKey(PRIVATE_KEY_1))
                self.assertEqual(result, SIG_1)
                
                slap2 = SessionlessSecp256k1()
                result2 = await slap2.sign(MSG, getKey(PRIVATE_KEY_2))
                self.assertEqual(result2, SIG_2)
        
        async def test_verify_signature(self):
                slap = SessionlessSecp256k1()
                
                result1 = slap.verifySignature(SIG_1, MSG, PUBLIC_KEY_1) 
                result2 = slap.verifySignature(SIG_2, MSG, PUBLIC_KEY_2)
                result3 = slap.verifySignature(SIG_1, MSG, PUBLIC_KEY_2) 
                result4 = slap.verifySignature(SIG_2, MSG, PUBLIC_KEY_1)
                
                self.assertEqual(result1, True)
                self.assertEqual(result2, True)
                self.assertEqual(result3, False)
                self.assertEqual(result4, False)
        
        
        def test_associate(self):
                slap = SessionlessSecp256k1()
                result = slap.associate(SIG_1, MSG, PUBLIC_KEY_1, SIG_2, MSG, PUBLIC_KEY_2)
                self.assertEqual(result, True)

        def test_generate_UUID(self):
                slap = SessionlessSecp256k1()
                uuid = slap.generateUUID()
                self.assertIsNotNone(uuid)