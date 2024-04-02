package com.planetnine.sessionless.util.sessionless.models

import com.planetnine.sessionless.util.sessionless.keys.KeyAccessInfo
import com.planetnine.sessionless.util.sessionless.keys.SimpleKeyPair
import com.planetnine.sessionless.util.sessionless.vaults.ICustomVault
import com.planetnine.sessionless.util.sessionless.vaults.IKeyStoreVault
import com.planetnine.sessionless.util.sessionless.vaults.IVault
import java.security.KeyPair
import java.security.PrivateKey

interface ISessionless {
    /** The way to store and retrieve key pairs */
    val vault: IVault
    
    interface WithKeyStore : ISessionless {
        override val vault: IKeyStoreVault

        /** Generates a private/public key pair and stores it using [vault]
         * @param keyAccessInfo Info required to access the stored key
         * @return The [KeyPair] that was generated then stored with [vault] */
        fun generateKeys(keyAccessInfo: KeyAccessInfo): KeyPair

        /** Generates a private/public key pair and stores it using [vault]
         * @param keyAccessInfo Info required to access the stored key
         * @return The [KeyPair] that was generated then stored with [vault] */
        suspend fun generateKeysAsync(keyAccessInfo: KeyAccessInfo): KeyPair

        /** Retrieves keys using [vault]
         * @param keyAccessInfo Info required to access the stored key
         * @return Key pair as objects ([KeyPair]) */
        fun getKeys(keyAccessInfo: KeyAccessInfo): KeyPair

        /** Signs a [message] with the user's stored private key (from [vault])
         * @param message The message to be signed.
         * @param keyAccessInfo Info required to access the stored key
         * @return Signature as a [String] */
        fun sign(message: String, keyAccessInfo: KeyAccessInfo): IMessageSignature
    }

    interface WithCustomVault : ISessionless {
        override val vault: ICustomVault

        /** Generates a private/public key pair and stores it using [vault]
         * @return A simplified ([SimpleKeyPair]) version of the key pair that was generated then stored with [vault] */
        fun generateKeys(): SimpleKeyPair

        /** Generates a private/public key pair and stores it using [vault]
         * @return A simplified ([SimpleKeyPair]) version of the key pair that was generated then stored with [vault] */
        suspend fun generateKeysAsync(): SimpleKeyPair

        /** Retrieves keys using [vault]
         * @return Key pair as [String]s ([SimpleKeyPair]). */
        fun getKeys(): SimpleKeyPair

        /** Signs a [message] with the user's stored private key (from [vault]).
         * @param message The message to be signed.
         * @return Signature as a [String] */
        fun sign(message: String): IMessageSignature
    }

    /** Signs a [message] using the provided [privateKey].
     * @param message The message to be signed.
     * @return Signature as a [String]. */
    fun sign(message: String, privateKey: PrivateKey): IMessageSignature

    /** Verifies a given signature with a public key.
     * @param publicKey The public key to use for verification.
     * @param signature The signature to be verified.
     * @param message The message that was signed earlier (ideally signed with [sign]).
     * @return True if the [signature] is valid for the given [message] and [publicKey].
     * @see sign */
    fun verify(publicKey: String, signature: IMessageSignature, message: String): Boolean

    /** Creates a unique UUID for a user.
     * @return The generated UUID. */
    fun generateUUID(): String

    /** Associates 2 message signatures with their respective public keys
     * @return true if both signatures were verified successfully
     * @see verify */
    fun associate(
        primaryPublicKey: String,
        primaryMessage: String,
        primarySignature: IMessageSignature,
        secondaryPublicKey: String,
        secondaryMessage: String,
        secondarySignature: IMessageSignature,
    ): Boolean

//    /** Revokes a gateway's key from the user.
//     * @param message Message for revocation.
//     * @param signature Signature using user's private key.
//     * @param publicKey Gateway's public key. */
//    fun revokeKey(message: String, signature: String, publicKey: String)
}

