

using SessionlessNET.Impl;

namespace SessionlessTest;

public class SessionlessGeneralTests {
    Vault V;
    Sessionless S;

    const string privPath = "./priv.key";
    const string pubPath = "./pub.key";
    static void VaultSaver(KeyPairHex pair) {
        File.WriteAllText(privPath, pair.PrivateKey);
        File.WriteAllText(pubPath, pair.PublicKey);
    }
    static KeyPairHex? VaultGetter() {
                if (!File.Exists(privPath)) return null;
                if (!File.Exists(pubPath)) return null;
                var privateHex = File.ReadAllText(privPath);
                var publicHex = File.ReadAllText(pubPath);
                return new(privateHex, publicHex);
            }


    [SetUp]
    public void Setup() {
        V = new Vault(VaultGetter, VaultSaver);
        S = new(V);
    }

    [Test]
    public void GenerateKeys() {
        var generated = (KeyPairHex)S.GenerateKeys();
        var retrieved = (KeyPairHex)S.GetKeys();
        Assert.That(generated == retrieved, "Generated keys match retrieved keys");
    }
}

internal record NewRecord();
