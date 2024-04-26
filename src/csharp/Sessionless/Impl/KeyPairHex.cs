using SessionlessNET.Impl.Exceptions;
using SessionlessNET.Util;

namespace SessionlessNET.Impl;

/// <summary> Pair of public/private keys as byte <see cref="string"/>s </summary>
public record KeyPairHex {
    public string PrivateKey { get; }
    public string PublicKey { get; }

    /// <exception cref="HexFormatRequiredException"/>
    public KeyPairHex(string privateKey, string publicKey) {
        if (!privateKey.IsBytes() || !publicKey.IsBytes()) {
            throw new HexFormatRequiredException($"{nameof(privateKey)}, {nameof(publicKey)}");
        }
        PrivateKey = privateKey;
        PublicKey = publicKey;
    }
}
