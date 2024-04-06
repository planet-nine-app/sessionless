using Org.BouncyCastle.Math;
using SessionlessNET.Util;

namespace SessionlessNET.Impl;

/// <summary> Common type for message signatures 
/// <br/> ● Empty. Used only to indicate that subclasses are similar </summary>
public interface IMessageSignature { }

/// <summary>  Message signature as <see cref="BigInteger"/>s </summary>
public record MessageSignatureInt : IMessageSignature {
    public BigInteger R { get; }
    public BigInteger S { get; }

    public MessageSignatureInt(BigInteger r, BigInteger s) {
        R = r; S = s;
    }

    /// <exception cref="IndexOutOfRangeException"/>
    public MessageSignatureInt(BigInteger[] ints)
            : this(ints[0], ints[1]) { }

    /// <summary> Convert to <see cref="MessageSignatureHex"/> </summary>
    public MessageSignatureHex ToHex() {
        return new(R.ToString(16), S.ToString(16));
    }
}

/// <summary> Message signature as hex <see cref="string"/>s </summary>
public record MessageSignatureHex : IMessageSignature {
    public string RHex { get; }
    public string SHex { get; }

    /// <exception cref="FormatException"/>
    public MessageSignatureHex(string rHex, string sHex) {
        if (!rHex.IsHex() || !sHex.IsHex()) {
            throw new FormatException("R & S must be in hex format (allowed characters: 0-9 A-F a-f)");
        }
        RHex = rHex; SHex = sHex;
    }

    /// <summary> Convert to <see cref="MessageSignatureInt"/> </summary>
    public MessageSignatureInt ToInt() {
        return new(new(RHex, 16), new(SHex, 16));
    }
}
