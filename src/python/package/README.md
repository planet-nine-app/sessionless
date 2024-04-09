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

### Generating a private key
To use this package, please call a new instance of the SessionlessSecp256k1 class. Users can default to providing a private key within the class constructor. If no key is provided, a key will be randomly generated for the user.

```python
#This will generate a random private key
#Passing a private key in hex format within the constructor will assign the value as an instance private key
sessionless = SessionlessSecp256k1()
```

### Retrieving the private key
Users will need to supply methods to retrieve the key. If methods are not supplied, the private key will not be returned. This is to uphold security practices.
```python
sessionless.get_private_key(saveKeys(), getKeys()) #This will return the encrypted private key
```
Accessing the private key by calling the parameter will throw an attribute error. 

```python
sessionless.__private_key #AttributeError: 'SessionlessSecp256k1' object has no attribute '__private_key'. Did you mean: 'get_private_key'?
```
### Generating a public key
Users can easily generate public keys from the private key.
```python
public_key = sessionless.get_public_key_from_private_key()
```

### Signing messages
Users can easily sign messages by providing a message to the sign_message() method. Messages do not need to be encoded before passing them to the method. The method will return an encrypted signature that users can store as needed.
```python
msg = {
"message": "The weather is so nice today!"
}
signature = sessionless.sign_message(msg)
```

### Verifying messages
Users can verify messages and signatures to ensure data integrity, authenticity, and non-repudiation. Users will pass a signature, message, and an encrypted public key as parameters. If public key is not provided, a public key will be generated from the instance's private key.
```python
res = sessionless.verify_signature(signature, msg) # Returns True
res2 = sessionless.verify_signature(signature, msg, second_primary_key) #Returns False
```

