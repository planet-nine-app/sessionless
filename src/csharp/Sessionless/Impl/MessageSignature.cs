using Org.BouncyCastle.Math;
using SessionlessNET.Models;

namespace SessionlessNET.Impl;

public record MessageSignature : IMessageSignature, IMessageSignatureHex {
    public BigInteger R { get; }
    public BigInteger S { get; }
    public string RHex { get; }
    public string SHex { get; }

    public MessageSignature(BigInteger r, BigInteger s) {
        R = r; RHex = R.ToString(16);
        S = s; SHex = S.ToString(16);
    }
    public MessageSignature(IMessageSignature signatureInt)
            : this(signatureInt.R, signatureInt.S) { }

    /// <exception cref="FormatException" />
    public MessageSignature(string rHex, string sHex) {
        RHex = rHex;
        SHex = sHex;
        R = new BigInteger(rHex, 16);
        S = new BigInteger(sHex, 16);
    }
    public MessageSignature(IMessageSignatureHex signatureHex)
            : this(signatureHex.RHex, signatureHex.SHex) { }

    public static MessageSignature From(BigInteger[] ints) {
        if (ints.Length != 2) {
            throw new ArgumentException($"{nameof(ints)} array length must be 2 (containing [r,s])");
        }
        return new(ints[0], ints[1]);
    }
}
