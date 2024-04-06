using SessionlessNET.Models;

namespace SessionlessNET.Impl;

public record KeyPair : IKeyPair {
    public string PrivateKey { get; }
    public string PublicKey { get; }
    public KeyPair(string privateKey, string publicKey) {
        PrivateKey = privateKey;
        PublicKey = publicKey;
    }
}
