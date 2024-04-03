package com.planetnine.sessionless.util.sessionless.models

import com.planetnine.sessionless.util.sessionless.impl.KeyStoreVault
import com.planetnine.sessionless.util.sessionless.models.vaults.ICustomVault
import com.planetnine.sessionless.util.sessionless.models.vaults.IVault
import java.security.KeyPair
import java.security.PrivateKey

interface ISessionless {
    /** The way to store and retrieve key pairs */
    val vault: IVault

    interface WithKeyStore : ISessionless {
        override val vault: KeyStoreVault

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
        fun sign(message: String, keyAccessInfo: KeyAccessInfo): MessageSignature
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
         * @return Signature as a [MessageSignature] */
        fun sign(message: String): MessageSignature
    }

    /** Signs a [message] using the provided [privateKey].
     * @param message The message to be signed.
     * @return Signature as a [MessageSignature]. */
    fun sign(message: String, privateKey: PrivateKey): MessageSignature

    /** Verifies a given signature with a public key.
     * @param identifiableMessage message that was signed
     * @return True if the [IdentifiableMessage.signature] is valid
     * for the given [IdentifiableMessage.message] and [IdentifiableMessage.publicKey].
     * @see sign */
    fun verify(identifiableMessage: IdentifiableMessage): Boolean

    /** Creates a unique UUID for a user.
     * @return The generated UUID. */
    fun generateUUID(): String

    /** Associates [identifiableMessages] signatures with their respective public keys
     * @return true if all were successfully verified
     * @throws java.lang.IllegalArgumentException if array size<2
     * @see verify */
    fun associate(vararg identifiableMessages: IdentifiableMessage): Boolean

//    /** Revokes a gateway's key from the user.
//     * @param message Message for revocation.
//     * @param signature Signature using user's private key.
//     * @param publicKey Gateway's public key. */
//    fun revokeKey(message: String, signature: String, publicKey: String)
}

