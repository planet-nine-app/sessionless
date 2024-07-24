# Docs

## How to use
1. Import the package (`Demo` folder is optional).
2. Create `Sessionless` context object.
3. Remember to call `Clean()` method whenever the context object is destroyed.

## `Sessionless`
Sessionless context object is an object that lets you communicate with the underlying engine.
In most cases you don't have to create it more than once in the entire lifetime of the process.
It's allocated globally on the heap, and it is your responsibility to deallocate it whenever it's no longer used.

The context object can be created as follows:
```js
// New random context object.
ctx = new Sessionless();

// New predefined context object.
var private_key = "084d87333fe840901503a4e302d3cae42f7b6d2afac245d28aba16ce7a3c6978";
ctx = new Sessionless(private_key);
```

### `Sign(message)`
Used to sign the message. Will return signature as a hex string, or empty string if something went wrong.

### `Verify(message, public_key, signature)`
Used to verify the message. Will return boolean depending on if the verification completed successfully or not. <br>
Requires public key of the context that signed the message and the created signature.

### `GetPublicKey()`
Returns the context's public key as a hex string.

### `GetPrivateKey()`
Returns the context's private key as a hex string.

### `Clean()`
Deallocates the context object.
