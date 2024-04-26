﻿using Org.BouncyCastle.Crypto.Parameters;
using SessionlessNET.Impl;

namespace SessionlessNET.Models;

/// <summary> 
/// <para> Passwordless authentication </para>
/// <para> With a <see cref="Vault"/> for storing and retrieving <see cref="KeyPairHex"/>es </para>
/// </summary>
public interface ISessionless {
    /// <summary> The way to store and retrieve <see cref="KeyPairHex"/>s </summary>
    public IVault Vault { get; }

    /// <summary> Creates a unique UUID for a user. </summary> 
    public string GenerateUUID();

    /// <summary>
    /// Generates a private/public <see cref="KeyPairHex"/> and stores it using <paramref name="Vault"/>.
    /// </summary>
    /// <returns> <see cref="KeyPairHex"/> that was generated </returns>
    public KeyPairHex GenerateKeys();

    /// <summary>
    /// Generates a private/public <see cref="KeyPairHex"/> asynchronously and stores it using <paramref name="Vault"/>.
    /// </summary>
    /// <returns> <see cref="KeyPairHex"/> that was generated </returns>
    public Task<KeyPairHex> GenerateKeysAsync();

    /// <summary>
    /// Retrieves keys using <paramref name="Vault"/>.
    /// </summary>
    /// <returns>Key pair as <see cref="KeyPairHex"/>.</returns>
    public KeyPairHex? GetKeys();

    /// <summary>
    /// Signs a <paramref name="message"/> with the user's stored private key
    /// <br/> (Get from <paramref name="Vault"/> using <see cref="GetKeys"/>).
    /// </summary>
    /// <param name="message">The message to be signed.</param>
    /// <returns>Signature as a <see cref="MessageSignatureHex"/>.</returns>
    public MessageSignatureHex Sign(string message);

    /// <summary>
    /// Signs a <paramref name="message"/> using the provided <paramref name="privateKeyHex"/>
    /// </summary>
    /// <param name="message">The message to be signed.</param>
    /// <param name="privateKeyHex"> The private key in hex format to use for signing </param>
    /// <returns>Signature as a <see cref="MessageSignatureHex"/>.</returns>

    public MessageSignatureHex Sign(string message, string privateKeyHex);

    /// <summary>
    /// Signs a <paramref name="message"/> using the provided <paramref name="privateKey"/>.
    /// </summary>
    /// <param name="message">The message to be signed.</param>
    /// <param name="privateKey">The private key to use for signing.</param>
    /// <returns>Signature as a <see cref="MessageSignatureHex"/>.</returns>
    public MessageSignatureHex Sign(string message, ECPrivateKeyParameters privateKey);

    /// <summary>
    /// Verifies a given <paramref name="signedMessage"/> with the user's stored public key.
    /// <br/>
    /// <br/> See: <seealso cref="Sign(string)"/>
    /// </summary>
    /// <param name="signedMessage"> The message that was signed earlier </param>
    /// <returns> True if the signature is valid for the given message and public key </returns>
    public bool VerifySignature(SignedMessage signedMessage);
    /// <summary>
    /// Verifies a given <paramref name="signedMessage"/> with the included <see cref="SignedMessageWithKey.PublicKey"/>.
    /// <br/>
    /// <br/> See: <seealso cref="Sign(string, string)"/>
    /// </summary>
    /// <param name="signedMessage">The message that was signed earlier </param>
    /// <returns> True if the signature is valid for the given message and public key </returns>
    public bool VerifySignature(SignedMessageWithKey signedMessage);
    /// <summary>
    /// Verifies a given <paramref name="signedMessage"/> with the included <see cref="SignedMessageWithECKey.PublicKey"/>
    /// <br/>
    /// <br/> See: <seealso cref="Sign(string, ECPrivateKeyParameters)"/>
    /// </summary>
    /// <param name="signedMessage">The message that was signed earlier </param>
    /// <returns> True if the signature is valid for the given message and public key </returns>
    public bool VerifySignature(SignedMessageWithECKey signedMessage);

    /// <summary>
    /// Verifies each of the <paramref name="messages"/>
    /// <br/>
    /// <br/> See: <seealso cref="VerifySignature"/>
    /// </summary>
    /// <param name="messages"> Messages to be verified </param>
    /// <returns> True if all signatures were verified successfully </returns>
    /// <exception cref="ArgumentException"></exception> 
    public bool Associate(params SignedMessage[] messages);
}

