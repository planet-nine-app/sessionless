using SessionlessNET.Models;

namespace SessionlessNET.Impl;

/// <summary> Easy instaniation for <see cref="IVault"/> </summary>
public class Vault(Func<KeyPair?> getter, Action<KeyPair> saver) : IVault {
    public KeyPair? Get() => getter();
    public void Save(KeyPair pair) => saver(pair);
}

