using SessionlessNET.Models;

namespace SessionlessNET.Impl;

/// <summary> Easy instaniation for <see cref="IVault"/> </summary>
public class Vault(
    Func<KeyPairHex?> getter,
    Action<KeyPairHex> saver
) : IVault {
    public KeyPairHex? Get() => getter();
    public void Save(KeyPairHex pair) => saver(pair);
}

