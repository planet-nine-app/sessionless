using SessionlessNET.Impl;
using SessionlessNET.Models;

namespace SessionlessTest.Samples;

internal class VaultSample : IVault {
    private const string PrivateKeyPath = "./private.key";
    private const string PublicKeyPath = "./public.key";

    public void Save(KeyPairHex pair) {
        File.WriteAllText(PrivateKeyPath, pair.PrivateKey);
        File.WriteAllText(PublicKeyPath, pair.PublicKey);
    }

    public KeyPairHex? Get() {
        if (!File.Exists(PrivateKeyPath) || !File.Exists(PublicKeyPath)) {
            Clear();
            return null;
        }

        var privateKey = File.ReadAllText(PrivateKeyPath);
        var publicKey = File.ReadAllText(PublicKeyPath);
        return new KeyPairHex(privateKey, publicKey);
    }

    public static void Clear() {
        if (File.Exists(PrivateKeyPath)) File.Delete(PrivateKeyPath);
        if (File.Exists(PublicKeyPath)) File.Delete(PublicKeyPath);
    }
}
