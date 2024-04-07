using Org.BouncyCastle.Math;
using SessionlessNET.Impl.Exceptions;
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

    public MessageSignatureInt(MessageSignatureHex signatureHex)
            : this(
                new(signatureHex.RHex, 16),
                new(signatureHex.SHex, 16)
            ) { }

    /// <summary> Convert to <see cref="MessageSignatureHex"/> </summary>
    public MessageSignatureHex ToHex() => new(this);


    /// <summary> Explicit caster to <see cref="MessageSignatureHex"/> </summary> 
    public static explicit operator MessageSignatureHex(MessageSignatureInt signatureInt)
        => signatureInt.ToHex();
}

/// <summary> Message signature as hex <see cref="string"/>s </summary>
public record MessageSignatureHex : IMessageSignature {
    public string RHex { get; }
    public string SHex { get; }

    /// <exception cref="HexFormatRequiredException"/>
    public MessageSignatureHex(string rHex, string sHex) {
        if (!rHex.IsBytes() || !sHex.IsBytes()) {
            throw new HexFormatRequiredException($"{nameof(rHex)}, {nameof(sHex)}");
        }
        RHex = rHex; SHex = sHex;
    }

    public MessageSignatureHex(MessageSignatureInt signatureInt)
            : this(
                signatureInt.S.ToString(16),
                signatureInt.S.ToString(16)
            ) { }

    /// <summary> Convert to <see cref="MessageSignatureInt"/> </summary>
    public MessageSignatureInt ToInt() => new(this);

    /// <summary> Explicit caster to <see cref="MessageSignatureInt"/> </summary> 
    public static explicit operator MessageSignatureInt(MessageSignatureHex signatureHex)
        => signatureHex.ToInt();
}
