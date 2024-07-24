using Org.BouncyCastle.Crypto.Parameters;
using SessionlessNET.Impl;
using SessionlessNET.Impl.Exceptions;
using SessionlessNET.Util;

namespace SessionlessNET.Models;

/// <summary> A <see cref="Message"/> that has been signed, having the given <see cref="Signature"/> </summary>
public record SignedMessage {
    /// <summary> Original <see cref="string"/> that was signed </summary>
    public string Message { get; }
    /// <summary> <see cref="Message"/> signature </summary>
    public MessageSignatureHex Signature { get; }

    public SignedMessage(string message, MessageSignatureHex signature) {
        Message = message;
        Signature = signature;
    }

    /// <summary> Add the public key (<see cref="string"/>) that was used to sign the message </summary>
    /// <returns> this but as <see cref="SignedMessageWithKey"/> </returns>
    public SignedMessageWithKey WithKey(string publicKey)
        => new(this, publicKey);

    /// <summary> Add the public key (<see cref="ECPublicKeyParameters"/>) that was used to sign the message </summary>
    /// <returns> this but as <see cref="SignedMessageWithECKey"/> </returns>
    public SignedMessageWithECKey WithKey(ECPublicKeyParameters publicKey)
        => new(this, publicKey);
}

/// <summary> <see cref="SignedMessage"/> but with a public key <see cref="string"/> </summary>
public record SignedMessageWithKey : SignedMessage {
    /// <summary> Public key that was used to sign the <see cref="SignedMessage.Message"/> (as a <see cref="string"/> of bytes) </summary>
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

    /// <summary> Convert to <see cref="SignedMessageWithECKey"/> </summary>
    public SignedMessageWithECKey ToEC()
        => new(this, PublicKey.ToEC());
}

/// <summary> <see cref="SignedMessage"/> but with a public key of type <see cref="ECPublicKeyParameters"/> </summary>
public record SignedMessageWithECKey : SignedMessage {
    /// <summary> Public key that was used to sign the <see cref="SignedMessage.Message"/> (as <see cref="ECPublicKeyParameters"/>) </summary>
    public ECPublicKeyParameters PublicKey { get; }

    public SignedMessageWithECKey(string message, MessageSignatureHex signature, ECPublicKeyParameters publicKey)
            : base(message, signature) {
        PublicKey = publicKey;
    }
    public SignedMessageWithECKey(SignedMessage signedMessage, ECPublicKeyParameters publicKey)
            : this(signedMessage.Message, signedMessage.Signature, publicKey) { }
}

