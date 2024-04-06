# [ISessionless](./src/main/kotlin/com/planetnine/sessionless/models/ISessionless.kt):

```mermaid
classDiagram
  class ISessionless {
    +vault: IVault

    +generateUUID(): String
    +sign(message: String, key: PrivateKey): IMessageSignature
    +verify(signedMessage: SignedMessage): Boolean
    +associate(...signedMessages: SignedMessage): Boolean
  }

  class IWithKeyStore {
    @Override
    ^vault: IKeyStoreVault

    +generateKeys(access: KeyAccessInfo): KeyPair
    +generateKeysAsync(access: KeyAccessInfo): KeyPair
    +getKeys(): KeyPair
    +sign(message: String, access: KeyAccess): IMessageSignature
  }

  class IWithCustomVault {
    @Override
    ^vault: ICustomVault

    +generateKeys(): KeyPairHex
    +generateKeysAsync(): KeyPairHex
    +getKeys(): KeyPairHex
    +sign(message: String): IMessageSignature
  }

  ISessionless <-- IWithKeyStore
  ISessionless <-- IWithCustomVault
```

# [MessageSignature](./src/main/kotlin/com/planetnine/sessionless/impl/MessageSignature.kt):

```mermaid
classDiagram
  class IMessageSignature { }
  class MessageSignatureInt {
    +r: BigInteger
    +s: BigInteger
    +constructor(r: BigInteger, s: BigInteger)
    +toHex(): MessageSignatureHex
  }
  class MessageSignatureHex {
    +rHex: String
    +sHex: String
    +constructor(rHex: String, sHex: String)
    +toInt(): MessageSignatureInt
  }

  IMessageSignature <-- MessageSignatureInt
  IMessageSignature <-- MessageSignatureHex
```

# [IVault](./src/main/kotlin/com/planetnine/sessionless/models/IVault.kt):

```mermaid
classDiagram
  class IVault {
    +getDefault(keyStore: KeyStore): KeyStoreVault
  }
  class IKeyStoreVault {
    +keyStore: KeyStore

    +save(pair: KeyPair, access: KeyAccessInfo, certificate: Certificate)
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
    +signature: MessageSignatureHex
    +publicKey: String
    +constructor(..all..)
  }

  class KeyPairHex {
    +privateKey: String
    +publicKey: String
    +constructor(..all..)
  }

  class KeyAccessInfo {
    +alias: String
    +password: CharArray?
    +constructor(..all..)
    +constructor(alias: String, password: String)
  }
```
