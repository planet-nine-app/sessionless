<div align="center">
    <h1> Sessionless : Python</h1>
</div>

## About

[Sessionless](https://sessionless.org/) is an open source authentication protocol that uses the cryptography employed by Bitcoin and Ethereum to authenticate messages sent between a client and a server. Within this protocol, you create and store a private key on the client and then use that key to sign messages; those messages are then verified by the server via the public key associated with the client. When you verify a message you also certify its provenance. Because no other secret need be shared between client and server, sessions are wholly unnecessary.

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
# Users must pass a method to retrieve keys.
def get_key():
    pass
sessionless = SessionlessSecp256k1(get_key)
```

### Generating private and public keys
The generate keys method will generate a private key and a public key. To use the method, users must provide a method to save the keys. 
```python
def save_key():
    pass
private_key, public_key = sessionless.generate_keys(save_key) # This will return the encrypted private key
```

### Signing messages
Users can easily sign messages by providing a message to the sign method. Messages do not need to be encoded before passing them to the method. The method will return an encrypted signature that users can store as needed.
```python
msg = {
"message": "The weather is so nice today!"
}
signature = sessionless.sign(msg)
```

### Verifying messages
Users can verify messages and signatures to ensure data integrity, authenticity, and non-repudiation. Users will pass a signature, message, and an encrypted public key as parameters. If public key is not provided, a public key will be generated from the instance's private key.
```python
res = sessionless.verify_signature(signature, msg, public_key) # Returns True
res2 = sessionless.verify_signature(first_signature, first_msg, second_primary_key) # Returns False
```

### Associating messages
Users can verify that two messages can be associated using the associate method. 
```python
res = sessionless.associate(primary_sig, primary_msg, primary_public_key, secondary_sig, secondary_msg, secondary_public_key) # Returns either True or False

```

### Generating UUIDs [Universally Unique Identifiers]
Users can generate unique identifiers as needed by calling the generate UUID method.
```python
uuid = sessionless.generate_UUID() # Returns UUID

```
