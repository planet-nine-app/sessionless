using SessionlessNET.Impl;

namespace SessionlessNET.Models;

public record SignedMessage {
    public string Message { get; }
    public MessageSignatureHex Signature { get; }

    public SignedMessage(string message, MessageSignatureHex signature) {
        Message = message;
        Signature = signature;
    }
}
