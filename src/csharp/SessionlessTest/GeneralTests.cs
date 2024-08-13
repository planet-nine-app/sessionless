namespace SessionlessTest;

using NUnit.Framework;
using NUnit.Framework.Internal;
using SessionlessNET.Impl;
using SessionlessNET.Models;
using SessionlessTest.Samples;

[TestFixture]
public class GeneralTests {
    private static Sessionless Instance => new(new VaultSample());

    [Test]
    public void GenerateKeysAndRetrieve() {
        var sessionless = Instance;
        var generated = sessionless.GenerateKeys();
        var retrieved = sessionless.GetKeys();

        Assert.Multiple(() => {
            Assert.That(generated.PrivateKey == retrieved?.PrivateKey);
            Assert.That(generated.PublicKey == retrieved?.PublicKey);
        });
    }


    [Test]
    public void VerifySignature() {
        var text = "Hello World";
        var sessionless = Instance;
        var generated = sessionless.GenerateKeys();
        var signature = sessionless.Sign(text);

        // Bad signature
        var signatureBad = new MessageSignatureHex(
            signature.RHex + "11",
            signature.SHex + "11"
        );

        var publicHex = generated.PublicKey;
        var verified = sessionless.VerifySignature(
            new SignedMessageWithKey(text, signature, publicHex)
        );

        // Bad signature should lead to verification failure
        var verifiedBad = sessionless.VerifySignature(
            new SignedMessageWithKey(text, signatureBad, publicHex)
        );

        Assert.Multiple(() => {
            Assert.That(verified);
            Assert.That(!verifiedBad);
        });
    }
}
