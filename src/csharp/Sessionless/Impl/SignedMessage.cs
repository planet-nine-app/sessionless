using Org.BouncyCastle.Crypto.Parameters;
using SessionlessNET.Impl;
using SessionlessNET.Impl.Exceptions;
using SessionlessNET.Util;

namespace SessionlessNET.Models;

public record SignedMessage {
    public string Message { get; }
    public MessageSignatureHex Signature { get; }

    public SignedMessage(string message, MessageSignatureHex signature) {
        Message = message;
        Signature = signature;
    }
    public SignedMessageWithKey WithKey(string publicKey)
        => new(this, publicKey);

    public SignedMessageWithECKey WithKey(ECPublicKeyParameters publicKey)
        => new(this, publicKey);
}

/// <summary> <see cref="SignedMessage"/> but with a public key <see cref="string"/> </summary>
public record SignedMessageWithKey : SignedMessage {
    public string PublicKey { get; }

    public SignedMessageWithKey(string message, MessageSignatureHex signature, string publicKey)
            : base(message, signature) {
        if (!publicKey.IsBytes()) {
            throw new HexFormatRequiredException(nameof(publicKey));
        }
        PublicKey = publicKey;
    }
    public SignedMessageWithKey(SignedMessage signedMessage, string publicKey)
            : this(signedMessage.Message, signedMessage.Signature, publicKey) { }
}

/// <summary> <see cref="SignedMessage"/> but with a public key of type <see cref="ECPublicKeyParameters"/> </summary>
public record SignedMessageWithECKey : SignedMessage {
    public ECPublicKeyParameters PublicKey { get; }

    public SignedMessageWithECKey(string message, MessageSignatureHex signature, ECPublicKeyParameters publicKey)
            : base(message, signature) {
        PublicKey = publicKey;
    }
    public SignedMessageWithECKey(SignedMessage signedMessage, ECPublicKeyParameters publicKey)
            : this(signedMessage.Message, signedMessage.Signature, publicKey) { }
}

