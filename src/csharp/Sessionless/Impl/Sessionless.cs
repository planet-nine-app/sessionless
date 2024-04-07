using Org.BouncyCastle.Crypto;
using Org.BouncyCastle.Crypto.Digests;
using Org.BouncyCastle.Crypto.Parameters;
using Org.BouncyCastle.Crypto.Signers;
using Org.BouncyCastle.Math;
using Org.BouncyCastle.Math.EC;
using Org.BouncyCastle.Security;
using Org.BouncyCastle.Utilities.Encoders;
using SessionlessNET.Impl.Exceptions;
using SessionlessNET.Models;
using SessionlessNET.Util;

namespace SessionlessNET.Impl;

/// <summary> <see cref="ISessionless"/> implementation </summary>
/// <param name="vault"> The way to store and retrieve <see cref="KeyPairHex"/>s </param>
public class Sessionless(IVault vault) : ISessionless {
    public IVault Vault { get; } = vault;

    public string GenerateUUID() {
        return Guid.NewGuid().ToString();
    }
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


    public KeyPairHex? GetKeys() {
        return Vault.Get();
    }

    public MessageSignatureHex Sign(string message) {
        var privateHex = GetKeys()?.PrivateKey
            ?? throw new KeyPairNotFoundException();
        return Sign(message, privateHex);
    }

    public MessageSignatureHex Sign(string message, string privateKeyHex) {
        if (!privateKeyHex.IsHex()) {
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
        var paramWithRandom = new ParametersWithRandom(privateKey, new SecureRandom());
        // init signer
        var signer = new ECDsaSigner(new HMacDsaKCalculator(new Sha256Digest()));
        signer.Init(true, paramWithRandom);
        // message string to keccak256 hash
        byte[] messageHash = message.HashKeccak256();
        // sign
        BigInteger[] signature = signer.GenerateSignature(messageHash);
        return new MessageSignatureInt(signature).ToHex();
    }


    public bool Verify(SignedMessage signedMessage) {
        var publicHex = GetKeys()?.PublicKey
            ?? throw new KeyPairNotFoundException();
        if (publicHex != signedMessage.PublicKey) return false;
        return Verify(signedMessage, publicHex);
    }
    public bool Verify(SignedMessage signedMessage, string publicKeyHex) {
        if (!publicKeyHex.IsHex()) {
            throw new HexFormatRequiredException(nameof(publicKeyHex));
        }
        if (publicKeyHex != signedMessage.PublicKey) return false;
        // public hex to bytes
        var publicBytes = Hex.Decode(publicKeyHex);
        // public bytes to key object
        ECDomainParameters curve = KeyUtils.Defaults.DomainParameters;
        ECPoint qPoint = curve.Curve.DecodePoint(publicBytes);
        var publicKey = new ECPublicKeyParameters(qPoint, curve);
        return Verify(signedMessage, publicKey);
    }
    public bool Verify(SignedMessage signedMessage, ECPublicKeyParameters publicKey) {
        // signature hex to bigint (and hex still included)
        var signature = signedMessage.Signature.ToInt();
        // message string to keccak256 hash
        byte[] messageHash = signedMessage.Message
            .HashKeccak256()
            .Take(32).ToArray();
        // verify
        var verifier = new ECDsaSigner();
        verifier.Init(false, publicKey);
        return verifier.VerifySignature(messageHash, signature.R, signature.S);
    }

    public bool Associate(params SignedMessage[] messages) {
        if (messages.Length < 2) {
            throw new ArgumentException($"{nameof(messages)} length must be greater or equal to 2");
        }
        return messages.All(Verify);
    }
}
