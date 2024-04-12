import unittest
from sessionless import SessionlessSecp256k1

PRIVATE_KEY_1 = "8dea0f7f726273b6971c1a4bcc3d60d8006fc087a4b9c4e7ae633918dff54f26"
PRIVATE_KEY_2 = "68eab2d2fae06bbd24d633ecb72d9a8d6c9e2de0aef7499028d471b8ad4cb204"

PUBLIC_KEY_1 = "036c2d503304b96b59bfb5f40e7d8b7f84646c21923a2eb19108b84afbd808f317"
PUBLIC_KEY_2 = "03a68885996743ac828dcb3d7747cb1758bca819409f86dfd017acac1eda98fd69"

MSG = "Test"

SIG_1 = "34cd9384267027bbaedff2727252a52d77aee14f266594584a18d3d69e3f0b8b1a1e597fe428829e4ef850cfd6c032212e6e2eb35827056d8fd403a5a571939a"
SIG_2 = "db1ac1ad92ad24c0cfd89f055250c5e6c187d859c134016fed1d7c631260823146d01c3f5a082fde987d413c58f8c04faa5f6da00b99d003d1d3a691deedba6b"

def getKey():
        pass

def saveKey():
        pass
class SessionlessSecp256k1Test(unittest.TestCase):
   
        def test_generate_keys(self):
                sl = SessionlessSecp256k1(PRIVATE_KEY_1)
                private_key, public_key = sl.generate_keys(saveKey, getKey)
                self.assertIsNotNone(private_key)
                self.assertIsNotNone(public_key)
                
                sl2 = SessionlessSecp256k1(PRIVATE_KEY_2)
                private_key2, public_key2 = sl2.generate_keys(saveKey, getKey)
                self.assertIsNotNone(private_key2)
                self.assertIsNotNone(public_key2)
                
        
        async def test_sign(self):
                sl = SessionlessSecp256k1(getKey)
                result = await sl.sign(MSG)
                self.assertIsNotNone(result)
                
                sl2 = SessionlessSecp256k1(getKey)
                result2 = await sl2.sign(MSG)
                self.assertIsNotNone(result2)
        
        async def test_verify_signature(self):
                sl = SessionlessSecp256k1(getKey)
                sig = await sl.sign(MSG)
                
                sl2 = SessionlessSecp256k1(getKey)
                sig2 = await sl2.sign(MSG)
                
                res1 = sl.verify_signature(sig2, MSG) 
                res2 = sl.verify_signature(sig, MSG)
                res3 = sl2.verify_signature(sig2, MSG) 
                res4 = sl2.verify_signature(sig, MSG)
                
                self.assertEqual(res1, False)
                self.assertEqual(res2, True)
                self.assertEqual(res3, True)
                self.assertEqual(res4, False)
        
        
        def test_associate(self):
                sl = SessionlessSecp256k1(getKey)
       
                res = sl.associate(SIG_1, MSG, PUBLIC_KEY_1, SIG_2, MSG, PUBLIC_KEY_2)

                self.assertEqual(res, True)

        def test_generate_UUID(self):
                sl = SessionlessSecp256k1(getKey)
                uuid = sl.generate_UUID()
                self.assertIsNotNone(uuid)