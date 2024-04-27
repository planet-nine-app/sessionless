namespace SessionlessNET.Impl.Exceptions;

internal class HexFormatRequiredException(string target)
        : FormatException($"Hex format is required {(target.Length > 0 ? $"for: {target}" : "")}") { }
