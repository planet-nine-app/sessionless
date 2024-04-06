using Org.BouncyCastle.Math;

namespace SessionlessNET.Models;

public interface IMessageSignature {
    public BigInteger R { get; }
    public BigInteger S { get; }
}
public interface IMessageSignatureHex {
    public string RHex { get; }
    public string SHex { get; }
}