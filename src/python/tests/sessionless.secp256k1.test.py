import unittest
from unittest.mock import Mock

class SessionlessSecp256k1Test(unittest.TestCase):
        def setUp(self):
         self.context_id = "test"
         self.mock_stream = Mock()
         self.addresses = ["a", "b", "c"]
         self.data = [addr.encode() for addr in self.addresses]