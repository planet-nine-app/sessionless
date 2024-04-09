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
   
        def test_private_key_hex(self):
                sl = SessionlessSecp256k1(PRIVATE_KEY_1)
                result = sl.get_private_key(saveKey, getKey)
                self.assertEqual(result, PRIVATE_KEY_1)
                
                sl2 = SessionlessSecp256k1(PRIVATE_KEY_2)
                result2 = sl2.get_private_key(saveKey, getKey)
                self.assertEqual(result2, PRIVATE_KEY_2)
        
        def test_public_key_from_hex(self):
                sl = SessionlessSecp256k1(PRIVATE_KEY_1)
                result = sl.get_public_key_from_private_key()
                self.assertEqual(result, PUBLIC_KEY_1)
                
                sl2 = SessionlessSecp256k1(PRIVATE_KEY_2)
                result2 = sl2.get_public_key_from_private_key()
                self.assertEqual(result2, PUBLIC_KEY_2)
                
        
        def test_signing_message(self):
                sl = SessionlessSecp256k1(PRIVATE_KEY_1)
                result = sl.sign_message(MSG)
                self.assertEqual(result, SIG_1)
                
                sl2 = SessionlessSecp256k1(PRIVATE_KEY_2)
                result2 = sl2.sign_message(MSG)
                self.assertEqual(result2, SIG_2)
        
        def test_verifying_signature(self):
                sl = SessionlessSecp256k1(PRIVATE_KEY_1)
                sig = sl.sign_message(MSG)
                
                sl2 = SessionlessSecp256k1(PRIVATE_KEY_2)
                sig2 = sl2.sign_message(MSG)
                
                res1 = sl.verify_signature(sig2, MSG) 
                res2 = sl.verify_signature(sig, MSG)
                res3 = sl2.verify_signature(sig2, MSG) 
                res4 = sl2.verify_signature(sig, MSG)
                
                self.assertEqual(res1, False)
                self.assertEqual(res2, True)
                self.assertEqual(res3, True)
                self.assertEqual(res4, False)
        
        
        def test_associating_verification(self):
                pass

