using Org.BouncyCastle.Crypto.Digests;
using System.Text;
using System.Text.RegularExpressions;
using BCMath = Org.BouncyCastle.Math;

namespace SessionlessNET.Util;

internal static partial class MathUtils {
    /// <summary> Check whether a given <see cref="BCMath.BigInteger"/> is even or not </summary>
    internal static bool IsEven(this BCMath.BigInteger bi) {
        return bi
            .Mod(BCMath.BigInteger.Two)
            .Equals(BCMath.BigInteger.Zero);
    }

    // Compile-time regex
    [GeneratedRegex("^[0-9A-Fa-f]+$")]
    private static partial Regex HexRegex();
    public static bool IsHex(this string str) {
        return HexRegex().IsMatch(str);
    }

    /// <summary> Convert a <see cref="BCMath.BigInteger"/> to hex <see cref="string"/> </summary>
    internal static string ToHex(this BCMath.BigInteger bi) {
        var bytes = bi.ToByteArray();
        StringBuilder hex = new(bytes.Length * 2);
        foreach (byte b in bytes)
            hex.Append($"{b:X2}");
        return hex.ToString();
    }

    /// <summary> Get the hash of <paramref name="input"/> in keccak256 format </summary>
    internal static byte[] HashKeccak256(this string input) {
        // input to bytes
        byte[] bytes = Encoding.UTF8.GetBytes(input);
        //init digest
        KeccakDigest keccak256 = new(256);
        keccak256.BlockUpdate(bytes, 0, bytes.Length);
        byte[] hashBytes = new byte[keccak256.GetDigestSize()];
        // fill hash bytes
        keccak256.DoFinal(hashBytes, 0);
        return hashBytes;
    }

}
