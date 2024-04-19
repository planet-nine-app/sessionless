using SessionlessNET.Models;

namespace SessionlessNET.Impl;

// <!> Source note:
//      Inheritence at runtime isn't a thing in C# (unlike java/kotlin)
//      > so implement IVault at compile time
//      > or use this at runtime
/// <summary> Easy instaniation for <see cref="IVault"/> </summary>
public class Vault(
    Func<KeyPairHex?> getter,
    Action<KeyPairHex> saver
) : IVault {
    public KeyPairHex? Get() => getter();
    public void Save(KeyPairHex pair) => saver(pair);
}

