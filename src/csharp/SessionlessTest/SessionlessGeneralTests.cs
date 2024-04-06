

using SessionlessNET.Impl;

namespace SessionlessTest;

public class SessionlessGeneralTests {
    Vault V;
    Sessionless S;

    [SetUp]
    public void Setup() {
        string privPath = "./priv.key";
        string pubPath = "./pub.key";
        V = new Vault(
            getter: () => {
                if (!File.Exists(privPath)) return null;
                if (!File.Exists(pubPath)) return null;
                var privateHex = File.ReadAllText(privPath);
                var publicHex = File.ReadAllText(pubPath);
                return new(privateHex, publicHex);
            },
            saver: (KeyPair pair) => {
                File.WriteAllText(privPath, pair.PrivateKey);
                File.WriteAllText(pubPath, pair.PublicKey);
            }
        );
        S = new(V);
    }

    [Test]
    public void GenerateKeys() {
        var generated = (KeyPair)S.GenerateKeys();
        var retrieved = (KeyPair)S.GetKeys();
        Assert.That(generated == retrieved, "Generated keys match retrieved keys");
    }
}

internal record NewRecord();
