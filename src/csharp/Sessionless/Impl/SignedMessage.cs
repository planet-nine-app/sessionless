using SessionlessNET.Impl;

namespace SessionlessNET.Models;

public record SignedMessage {
    public string Message { get; }
    public MessageSignatureHex Signature { get; }
    public string PublicKey { get; }

    public SignedMessage(string message, MessageSignatureHex signature, string publicKey) {
        Message = message;
        Signature = signature;
        PublicKey = publicKey;
    }
}
