import Text.Printf (printf)

data KeyTuple = KeyTuple {privateKey :: String, publicKey :: String}

generateKeys :: IO (KeyTuple -> ())  -> IO (() -> ()) -> KeyTuple 
generateKeys f g = do
  putStrLn "generateKeys" 
  let kt = KeyTuple {privateKey = "foo", publicKey = "bar"}
  return kt


sign :: String -> IO String
sign message = do 
  putStrLn "message"
  return message

verifySignature :: String -> String -> String -> IO Bool
verifySignature message signature pubKey = do 
  printf "verify signature of %s %s %s\n" message signature pubKey
  return True

generateUUID :: () -> IO String
generateUUID _ = do
  putStrLn "generate uuid here"
  return "uuid"

associateKeys :: String -> String -> String -> String -> String -> String -> IO Bool
associateKeys message signature pubKey message2 signature2 pubKey2 = do 
  printf "verify signature one %s %s %s and two %s %s %s\n" message signature pubKey message2 signature2 pubKey2
  return True


main :: IO ()
main = do
  -- so what should happen here for actually calling these functions?

  generateKeys (\keyTuple -> putStrLn "save keys") (return ())
--  generateKeys (kt -> ())  ()

  sign "foo"

  (verifySignature "foo" "sig" "pubKey")

  generateUUID ()

--  putStrLn associateKeys "foo" "sig" "pubKey" "foo" "sig2" "pubKey2"


