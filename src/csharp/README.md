# .../Models/**ISessionless**.cs

```mermaid
classDiagram
  class ISessionless {
    +Vault: IVault
    +GenerateUUID(): string
    +GenerateKeys(): KeyPairHex
    +GenerateKeysAsync(): Task<`KeyPairHex>
    +GetKeys(): KeypairHex?
    +Sign(string): MessageSignatureHex
    +Sign(string, string): MessageSignatureHex
    +Sign(string, ECPrivateKeyParameters): MessageSignatureHex
    +VerifySignature(SignedMessage): bool
    +VerifySignature(SignedMessageWithKey): bool
    +VerifySignature(SignedMessageWithECKey): bool
    +Associate(SignedMessages[]): bool
  }
  class Sessionless {
    constructor(IVault)
  }
  ISessionless <-- Sessionless
```

# .../Models/**IVault**.cs

```mermaid
classDiagram
  class IVault {
    +Get(): KeyPairHex?
    +Save(KeyPairHex)
  }
  class Vault {
    constructor(Func<`KeyPairHex?>, Action<`KeyPairHex>)
  }

  IVault <-- Vault
```

# The rest:

### .../Impl/MessageSignature.cs

```mermaid
classDiagram
  class IMessageSignature { }
  class MessageSignatureInt {
    +R: BigInteger
    +S: BigInteger
    +constructor(BigInteger, BigInteger)
    +constructor(BigInteger[])
    +constructor(MessageSignatureHex)
    +toHex(): MessageSignatureHex
    +op_explicit(MessageSignatureInt): MessageSignatureHex
  }
  class MessageSignatureHex {
    +RHex: string
    +SHex: string
    +constructor(string, string)
    +constructor(MessageSignatureInt)
    +toInt(): MessageSignatureInt
    +op_explicit(MessageSignatureHex): MessageSignatureInt
  }

  IMessageSignature <-- MessageSignatureInt
  IMessageSignature <-- MessageSignatureHex
```

### .../Impl/MessageSignature.cs

```mermaid
classDiagram
  class SignedMessage {
    +Message: string
    +Signature: MessageSignatureHex
    +constructor(string, MessageSignatureHex)
    +WithKey(string): SignedMessageWithKey
    +WithKey(ECPublicKeyParameters): SignedMessageWithECKey
  }
  class SignedMessageWithKey {
    +PublicKey: string
    +constructor(string, MessageSignatureHex, string)
    +constructor(SignedMessage, string)
  }
  class SignedMessageWithECKey {
    +PublicKey: ECPublicKeyParameters
    +constructor(string, MessageSignatureHex, ECPublicKeyParameters)
    +constructor(SignedMessage, ECPublicKeyParameters)
  }
  
  SignedMessage <-- SignedMessageWithKey
  SignedMessage <-- SignedMessageWithECKey
```

### .../Impl/...

```mermaid
classDiagram
  class KeyPairHex {
    +PrivateKey: string
    +PublicKey: string
    +constructor(string, string)
  }
```

### .../Impl/Exceptions/...

```mermaid
classDiagram
  class KeyPairNotFoundException {
    constructor()
  }
  class HexFormatRequiredException {
    constructor(string)
  }
  Exception <-- KeyPairNotFoundException
  Exception <.. FormatException
  FormatException <-- HexFormatRequiredException
```
