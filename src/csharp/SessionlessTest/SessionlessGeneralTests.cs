

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
    public void GenerateRetrieveKeys() {
        var generated = S.GenerateKeys();
        var retrieved = S.GetKeys();
        Assert.Multiple(() => {
            Assert.That(
                generated.PrivateKey, Is.EqualTo(retrieved?.PrivateKey),
                "Generated private key doesn't match the retrieved one"
            );
            Assert.That(
                generated.PublicKey, Is.EqualTo(retrieved?.PublicKey),
                "Generated public key doesn't match the retrieved one"
            );
        });
    }
}
