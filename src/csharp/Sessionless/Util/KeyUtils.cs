
using Org.BouncyCastle.Asn1.Sec;
using Org.BouncyCastle.Asn1.X9;
using Org.BouncyCastle.Crypto;
using Org.BouncyCastle.Crypto.Generators;
using Org.BouncyCastle.Crypto.Parameters;
using Org.BouncyCastle.Math.EC;
using Org.BouncyCastle.Security;
using Org.BouncyCastle.Utilities.Encoders;
using SessionlessNET.Impl;
using SessionlessNET.Impl.Exceptions;

namespace SessionlessNET.Util;

public static class KeyUtils {
    public static class Defaults {
        public const string KEY_ALGORITHM = "ECDSA";
        public const string KEY_SPEC_NAME = "secp256k1";
        public const int KEY_SIZE = 256;

        public static X9ECParameters Parameters
            => SecNamedCurves.GetByName(KEY_SPEC_NAME);
        public static ECDomainParameters DomainParameters
            => Parameters.GetDomainParameters();
    }

    public static ECDomainParameters GetDomainParameters(this X9ECParameters param) {
        return new(param.Curve, param.G, param.N, param.H);
    }


    public static AsymmetricCipherKeyPair GenerateKeyPair() {
        ECDomainParameters curve = SecNamedCurves.GetByName(Defaults.KEY_SPEC_NAME)
            .GetDomainParameters();
        ECKeyGenerationParameters genParams = new(curve, new SecureRandom());
        ECKeyPairGenerator gen = new(Defaults.KEY_ALGORITHM);
        gen.Init(genParams);
        return gen.GenerateKeyPair();
    }
    public static async Task<AsymmetricCipherKeyPair> GenerateKeyPairAsync() {
        return await Task.Run(GenerateKeyPair);
    }

    /// <summary>
    /// Convert <see cref="AsymmetricCipherKeyPair"/> to <see cref="ECPrivateKeyParameters"/>/<see cref="ECPublicKeyParameters"/> hex <see cref="string"/>s
    /// <br/>
    /// <br/> See: <see cref="ToHex(ECPrivateKeyParameters)"/>
    /// <br/> See: <see cref="ToHex(ECPublicKeyParameters)"/>
    /// </summary>
    public static KeyPairHex ToHex(this AsymmetricCipherKeyPair pair) {
        var priv = pair.Private; var pub = pair.Public;
        var privHex = ((ECPrivateKeyParameters)priv).ToHex();
        var pubHex = ((ECPublicKeyParameters)pub).ToHex();
        return new(privHex, pubHex);
    }

    /// <summary>
    /// Convert <see cref="ECPrivateKeyParameters"/> hex <see cref="string"/>
    /// <list type="bullet">
    /// <item> Converts <see cref="ECPrivateKeyParameters.D"/> to hex </item>
    /// </list>
    /// <br/>
    /// <br/> See: <see cref="ToHex(AsymmetricCipherKeyPair)"/>
    /// <br/> See: <see cref="ToHex(ECPublicKeyParameters)"/>
    /// </summary>
    public static string ToHex(this ECPrivateKeyParameters privateKey) {
        return privateKey.D.ToString(16);
    }

    /// <summary>
    /// Convert <see cref="ECPublicKeyParameters"/> to hex <see cref="string"/> of length 66 chars
    /// <list type="bullet">
    /// <item> compression prefix + left padding of 0s </item>
    /// </list> 
    /// <br/>
    /// <br/> See: <see cref="ToHex(AsymmetricCipherKeyPair)"/>
    /// <br/> See: <see cref="ToHex(ECPrivateKeyParameters)"/>
    /// </summary>
    public static string ToHex(this ECPublicKeyParameters publicKey) {
        var ecPoint = publicKey.Q;
        var rawXAbs = ecPoint.AffineXCoord.ToBigInteger().Abs();
        var rawY = ecPoint.AffineYCoord.ToBigInteger();
        // compressed/uncompressed point 
        var prefix = rawY.IsEven() ? "02" : "03";
        // absolute raw x to hex
        var publicHex = rawXAbs.ToString(16);
        // fill hex to be 64 chars | 32 bytes | 256 bits
        var padLength = Math.Max(0, 64 - publicHex.Length);
        var publicHexPadded = new string('0', padLength) + publicHex;
        // with prefix: 66 chars
        return prefix + publicHexPadded;
    }

    /// <summary> Convert <paramref name="publicKeyHex"/> to <see cref="ECPublicKeyParameters"/> using the <see cref="Defaults"/> </summary>
    /// <param name="publicKeyHex"> Public key in hex (bytes) <see cref="string"/> format </param>
    /// <returns> <paramref name="publicKeyHex"/> as <see cref="ECPublicKeyParameters"/> </returns>
    /// <exception cref="HexFormatRequiredException"/>
    public static ECPublicKeyParameters ToEC(this string publicKeyHex) {
        if (!publicKeyHex.IsBytes()) {
            throw new HexFormatRequiredException(nameof(publicKeyHex));
        }
        byte[] publicBytes = Hex.Decode(publicKeyHex);
        ECDomainParameters curve = Defaults.DomainParameters;
        ECPoint qPoint = curve.Curve.DecodePoint(publicBytes);
        return new(qPoint, curve);
    }
}