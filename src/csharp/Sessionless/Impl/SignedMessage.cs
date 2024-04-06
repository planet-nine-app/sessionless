namespace SessionlessNET.Models;

public record SignedMessage : ISignedMessage {
    public string Message { get; }
    public IMessageSignatureHex Signature { get; }
    public string PublicKey { get; }

    public SignedMessage(string message, IMessageSignatureHex signature, string publicKey) {
        Message = message;
        Signature = signature;
        PublicKey = publicKey;
    }
}
