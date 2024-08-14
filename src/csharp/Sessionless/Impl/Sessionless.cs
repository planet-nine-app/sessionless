using Org.BouncyCastle.Crypto.Digests;
using Org.BouncyCastle.Crypto.Parameters;
using Org.BouncyCastle.Crypto.Signers;
using Org.BouncyCastle.Crypto;
using Org.BouncyCastle.Math;
using Org.BouncyCastle.Security;
using SessionlessNET.Impl.Exceptions;
using SessionlessNET.Models;
using SessionlessNET.Util;

namespace SessionlessNET.Impl;

/// <summary> <see cref="ISessionless"/> implementation </summary>
/// <param name="vault"> The way to store and retrieve <see cref="KeyPairHex"/>s </param>
public class Sessionless(IVault vault) : ISessionless {
    public IVault Vault { get; } = vault;

    public string GenerateUUID() => Guid.NewGuid().ToString();
    public KeyPairHex GenerateKeys() {
        var pair = KeyUtils.GenerateKeyPair();
        return StoreHexPair(pair);
    }

    public async Task<KeyPairHex> GenerateKeysAsync() {
        var pair = await KeyUtils.GenerateKeyPairAsync();
        return StoreHexPair(pair);
    }

    private KeyPairHex StoreHexPair(AsymmetricCipherKeyPair pair) {
        var simple = pair.ToHex();
        Vault.Save(simple);
        return simple;
    }


    public KeyPairHex? GetKeys() => Vault.Get();


    public MessageSignatureHex Sign(string message) {
        var privateHex = GetKeys()?.PrivateKey
            ?? throw new KeyPairNotFoundException();
        return Sign(message, privateHex);
    }

    public MessageSignatureHex Sign(string message, string privateKeyHex) {
        if (!privateKeyHex.IsBytes()) {
            throw new HexFormatRequiredException(nameof(privateKeyHex));
        }
        // private hex to bigint
        var privateInt = new BigInteger(privateKeyHex, 16);
        // secp256k1 spec curve
        ECDomainParameters curve = KeyUtils.Defaults.DomainParameters;
        var privateKey = new ECPrivateKeyParameters(privateInt, curve);
        return Sign(message, privateKey);
    }

    public MessageSignatureHex Sign(string message, ECPrivateKeyParameters privateKey) {
        // init signer
        var signer = new ECDsaSigner(new HMacDsaKCalculator(new Sha256Digest()));
        signer.Init(true, privateKey);
        // message string to keccak256 hash
        byte[] messageHash = message.HashKeccak256();
        // sign

        BigInteger curveOrder = privateKey.Parameters.N;
        BigInteger halfCurveOrder = curveOrder.ShiftRight(1);

        while (true)
        {
            BigInteger[] signature = signer.GenerateSignature(messageHash);
            BigInteger r = signature[0];
            BigInteger s = signature[1];

            if (r.CompareTo(halfCurveOrder) <= 0)
            {
                // Ensure S is the lower of S and -S
                if (s.CompareTo(halfCurveOrder) > 0)
                {
                    s = curveOrder.Subtract(s);
                }

                BigInteger[] actualSignature = { r, s };
                return new MessageSignatureInt(actualSignature).ToHex();
            }
        }

//        BigInteger[] signature = signer.GenerateSignature(messageHash);
//        return new MessageSignatureInt(signature).ToHex();
    }

/*    public MessageSignatureHex Sign(string message, ECPrivateKeyParameters privateKey) {
        // init signer
        var signer = new ECDsaSigner(new HMacDsaKCalculator(new Sha256Digest()));
        signer.Init(true, privateKey);
        // message string to keccak256 hash
        byte[] messageHash = message.HashKeccak256();
        // sign
        BigInteger[] signature = signer.GenerateSignature(messageHash);
        return new MessageSignatureInt(signature).ToHex();
    }*/

/*    public MessageSignatureHex Sign(string message, ECPrivateKeyParameters privateKey) {
    var signer = new ECDsaSigner(new HMacDsaKCalculator(new Sha256Digest()));
    signer.Init(true, privateKey);
    
    byte[] messageHash = message.HashKeccak256();
    Console.WriteLine($"Message Hash: {BitConverter.ToString(messageHash).Replace("-", "")}");
    
    BigInteger[] signature = signer.GenerateSignature(messageHash);
    Console.WriteLine($"R: {signature[0].ToString(16)}");
    Console.WriteLine($"S: {signature[1].ToString(16)}");
    
    // Ensure low S
    if (signature[1].CompareTo(privateKey.Parameters.N.ShiftRight(1)) > 0)
    {
        signature[1] = privateKey.Parameters.N.Subtract(signature[1]);
        Console.WriteLine($"Adjusted S: {signature[1].ToString(16)}");
    }
    
    var result = new MessageSignatureInt(signature).ToHex();
    Console.WriteLine($"Final Signature: {result}");
    
    // Immediate verification
    //var verifier = new ECDsaSigner();
    //verifier.Init(false, privateKey.Parameters.G.Multiply(privateKey.D));
    //bool verified = verifier.VerifySignature(messageHash, signature[0], signature[1]);
    //Console.WriteLine($"Immediate Verification: {verified}");
    
    return result;
}*/


    public bool VerifySignature(SignedMessage signedMessage) {
        var publicHex = GetKeys()?.PublicKey
            ?? throw new KeyPairNotFoundException();
        var withKey = signedMessage.WithKey(publicHex);
        return VerifySignature(withKey);
    }

    public bool VerifySignature(SignedMessageWithKey signedMessage) {
        return VerifySignature(signedMessage.ToEC());
    }

    public bool VerifySignature(SignedMessageWithECKey signedMessage) {
        // signature hex to bigint
        var signatureInt = signedMessage.Signature.ToInt();
        // message string to keccak256 hash
        byte[] messageHash = signedMessage.Message.HashKeccak256();
        // verify
        var verifier = new ECDsaSigner();
        verifier.Init(false, signedMessage.PublicKey);
        return verifier.VerifySignature(messageHash, signatureInt.R, signatureInt.S);
    }

    public bool Associate(params SignedMessage[] messages) {
        if (messages.Length < 2) {
            throw new ArgumentException($"{nameof(messages)} length must be greater or equal to 2");
        }
        return messages.All(VerifySignature);
    }
}
