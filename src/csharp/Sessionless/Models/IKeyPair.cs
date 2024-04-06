namespace SessionlessNET.Models;

public interface IKeyPair {
    public string PrivateKey { get; }
    public string PublicKey { get; }
}
