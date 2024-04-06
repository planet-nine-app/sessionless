namespace SessionlessNET.Impl;

public record KeyPairHex {
    public string PrivateKey { get; }
    public string PublicKey { get; }
    public KeyPairHex(string privateKey, string publicKey) {
        PrivateKey = privateKey;
        PublicKey = publicKey;
    }
}
