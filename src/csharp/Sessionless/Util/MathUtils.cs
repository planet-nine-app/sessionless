using Org.BouncyCastle.Crypto.Digests;
using System.Text;
using BCMath = Org.BouncyCastle.Math;

namespace SessionlessNET.Util;

internal static partial class MathUtils {
    /// <summary> Check whether a given <see cref="BCMath.BigInteger"/> is even or not </summary>
    internal static bool IsEven(this BCMath.BigInteger bi) {
        return bi
            .Mod(BCMath.BigInteger.Two)
            .Equals(BCMath.BigInteger.Zero);
    }

    /// <summary> Checks whether a <see cref="char"/> is one of the hex characters
    /// <list type="bullet"><item> Allowed characters: 0-9 a-f A-F </item></list>
    /// </summary>
    /// <returns> <see langword="true"/> if hex </returns>
    public static bool IsHex(this char c)
        => char.IsBetween(c, '0', '9')
        || char.IsBetween(c, 'a', 'f')
        || char.IsBetween(c, 'A', 'F');

    /// <summary> Checks whether a <see cref="string"/> is made up of hex <see cref="char"/>s
    /// <list type="bullet"><item> Allowed characters: 0-9 a-f A-F </item></list>
    /// </summary>
    /// </summary>
    /// <returns> <see langword="true"/> if hex </returns>
    public static bool IsHex(this IEnumerable<char> chars) => chars.All(IsHex);

    /// <summary> Check if <paramref name="str"/> contains even hex characters only (<see cref="byte"/>s as <see cref="string"/>s)
    /// <list type="bullet">
    /// <item> Even length of <paramref name="str"/> </item>
    /// <item> Allowed characters: 0-9 a-f A-F </item>
    /// </list>
    /// </summary>
    public static bool IsBytes(this string str) {
        if (str.Length % 2 != 0) return false;
        return IsHex(str);
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
