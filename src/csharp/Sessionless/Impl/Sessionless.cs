using Org.BouncyCastle.Crypto;
using Org.BouncyCastle.Crypto.Digests;
using Org.BouncyCastle.Crypto.Parameters;
using Org.BouncyCastle.Crypto.Signers;
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
