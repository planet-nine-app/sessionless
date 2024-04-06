package com.planetnine.sessionless.models

import com.planetnine.sessionless.impl.IMessageSignature
import com.planetnine.sessionless.impl.KeyAccessInfo
import com.planetnine.sessionless.impl.KeyPairHex
import com.planetnine.sessionless.impl.KeyStoreVault
import com.planetnine.sessionless.impl.SignedMessage
import com.planetnine.sessionless.models.vaults.ICustomVault
import com.planetnine.sessionless.models.vaults.IVault
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
         * @return Signature as [IMessageSignature] */
        fun sign(message: String, keyAccessInfo: KeyAccessInfo): IMessageSignature
    }

    interface WithCustomVault : ISessionless {
        override val vault: ICustomVault

        /** Generates a private/public key pair and stores it using [vault]
         * @return A simplified ([KeyPairHex]) version of the key pair that was generated then stored with [vault] */
        fun generateKeys(): KeyPairHex

        /** Generates a private/public key pair and stores it using [vault]
         * @return A simplified ([KeyPairHex]) version of the key pair that was generated then stored with [vault] */
        suspend fun generateKeysAsync(): KeyPairHex

        /** Retrieves keys using [vault]
         * @return Key pair as [String]s ([KeyPairHex]). */
        fun getKeys(): KeyPairHex?

        /** Signs a [message] with the user's stored private key (from [vault]).
         * @param message The message to be signed.
         * @return Signature as [IMessageSignature] */
        fun sign(message: String): IMessageSignature
    }

    /** Signs a [message] using the provided [privateKey].
     * @param message The message to be signed.
     * @return Signature as [IMessageSignature]. */
    fun sign(message: String, privateKey: PrivateKey): IMessageSignature

    /** Verifies a given signature with a public key.
     * @param signedMessage message that was signed
     * @return True if the [SignedMessage.signature] is valid
     * for the given [SignedMessage.message] and [SignedMessage.publicKey].
     * @see sign */
    fun verify(signedMessage: SignedMessage): Boolean

    /** Creates a unique UUID for a user.
     * @return The generated UUID. */
    fun generateUUID(): String

    /** Associates [signedMessages] signatures with their respective public keys
     * @return true if all were successfully verified
     * @throws java.lang.IllegalArgumentException if array size<2
     * @see verify */
    fun associate(vararg signedMessages: SignedMessage): Boolean

//    /** Revokes a gateway's key from the user.
//     * @param message Message for revocation.
//     * @param signature Signature using user's private key.
//     * @param publicKey Gateway's public key. */
//    fun revokeKey(message: String, signature: String, publicKey: String)
}

