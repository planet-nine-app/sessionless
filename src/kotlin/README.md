# [Sessionless](./src/main/kotlin/com/planetnine/sessionless/models/ISessionless.kt) interface:

```mermaid
classDiagram
  class ISessionless {
    +vault: IVault

    +generateUUID(): String
    +sign(message: String, key: PrivateKey): IMessageSignatureHex
    +verify(signedMessage: SignedMessage): Boolean
    +associate(...signedMessages: SignedMessage): Boolean
  }

  class IWithKeyStore {
    @Override
    ^vault: IKeyStoreVault

    +generateKeys(access: KeyAccessInfo): KeyPair
    +generateKeysAsync(access: KeyAccessInfo): KeyPair
    +getKeys(): KeyPair
    +sign(message: String, access: KeyAccess): IMessageSignatureHex
  }

  class IWithCustomVault {
    @Override
    ^vault: ICustomVault

    +generateKeys(): SimpleKeyPair
    +generateKeysAsync(): SimpleKeyPair
    +getKeys(): SimpleKeyPair
    +sign(message: String): SignedMessage
  }

  ISessionless <-- IWithKeyStore
  ISessionless <-- IWithCustomVault
```

# [MessageSignature](./src/main/kotlin/com/planetnine/sessionless/models/IMessageSignature.kt) interface:

```mermaid
classDiagram
  class IMessageSignature {
    +r: BigInteger
    +s: BigInteger
  }
  class IMessageSignatureHex {
    +rHex: String
    +sHex: String
  }
  class MessageSignature {
    +constructor(r: BigInteger, s: BigInteger)
    +constructor(rHex: String, sHex: String)
  }

  IMessageSignature <-- MessageSignature
  IMessageSignatureHex <-- MessageSignature
```

# [Vault](./src/main/kotlin/com/planetnine/sessionless/models/IVault.kt) interface:

```mermaid
classDiagram
  class IVault { }
  class IKeyStoreVault {
    +keyStore: KeyStore

    +save(pair: KeyPair, access: KeyAccessInfo,certificate: Certificate)
    +get(access: KeyAccessInfo): KeyPair
  }
  class ICustomVault {
    +save(pair: SimpleKeyPair)
    +get(): SimpleKeyPair?
  }

  class KeyStoreVault {
    +save(pair: KeyPair, access: KeyAccessInfo)
    +constructor(keyStore: KeyStore)
  }

  IVault <-- IKeyStoreVault
  IVault <-- ICustomVault
  IKeyStoreVault <-- KeyStoreVault
```

# The rest:

```mermaid
classDiagram
  class SignedMessage {
    +message: String
    +signature: IMessageSignatureHex
    +publicKey: String
    +constructor(..all..)
  }

  class SimpleKeyPair {
    +publicKey: String
    +privateKey: String
    +constructor(..all..)
  }

  class KeyAccessInfo {
    +alias: String
    +password: CharArray?
    +constructor(..all..)
  }
```
