namespace SessionlessNET.Models;

public interface ISignedMessage {
    public string Message { get; }
    public IMessageSignatureHex Signature { get; }
    public string PublicKey { get; }
}
