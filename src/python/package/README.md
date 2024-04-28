<div align="center">
    <h1> Sessionless : Python</h1>
</div>

## About

[Sessionless](https://sessionless.org/) is an open-source authentication protocol that uses the cryptography employed by Bitcoin and Ethereum to authenticate messages sent between a client and a server. Within this protocol, you create and store a private key on the client and then use that key to sign messages; the server then verifies those messages via the public key associated with the client. When you verify a message you also certify its provenance. Sessions are wholly unnecessary because no other secret needs to be shared between client and server.

## Getting Started 

To install the package, run the following code within your command line interface. More information regarding release history for this package can be found [here](https://pypi.org/project/sessionless/).
```
pip install sessionless
```

## Development 

### Getting started
To use this package, please call a new instance of the SessionlessSecp256k1 class. Users will need to provide a get key method. This method will be referenced later on to obtain key values.

```python
# This will create an instance of the SessionlessSecp256k1 class 

sessionless = SessionlessSecp256k1()
```

### Generating private and public keys
The generate keys method will generate a unique private key and a public key. To use the method, users must provide a method to save the keys. This function to save the keys is left to the user's implementation.
```python
# The defined function will be called upon to store the generated keys
def saveKey(keyPair):
    db.store(keyPair["privateKey"], keyPair["publicKey"])
private_key, public_key = sessionless.generateKeys(saveKey) 
```

### Signing messages
Users can easily sign messages by providing a message to the sign method and a callable method that will return the private key. Messages do not need to be encoded before passing them to the method. The method will return an encrypted signature that users can store as needed. The method to the get the key is left to the user's implementation.
```python
# The defined method to get the keys will be called to retrieve the private key
msg = {
"message": "The weather is so nice today!"
}
signature = sessionless.sign(msg, getKey(privateKey={}))
```

### Verifying messages
Users can verify messages and signatures to ensure data integrity, authenticity, and non-repudiation. Users will pass a signature, message, and an encrypted public key as parameters. If public key is not provided, a public key will be generated from the instance's private key.
```python
result = sessionless.verifySignature(signature, msg, public_key) # Returns True
result2 = sessionless.verifySignature(first_signature, first_msg, second_primary_key) # Returns False
```

### Associating messages
Users can verify that two messages can be associated using the associate method. 
```python
result = sessionless.associate(primary_sig, primary_msg, primary_public_key, secondary_sig, secondary_msg, secondary_public_key) # Returns either True or False

```

### Generating UUIDs [Universally Unique Identifiers]
Users can generate unique identifiers as needed by calling the generate UUID method.
```python
uuid = sessionless.generateUUID() # Returns UUID

```
