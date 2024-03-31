package com.planetnine.sessionless

import java.security.KeyPair
import java.security.PrivateKey

interface ISessionless {
    /** The way to store and retrieve key pairs */
    val vault: IVault

    interface WithKeyStore : ISessionless {
        override val vault: IKeyStoreVault

        /** Generates a private/public key pair and stores it.
         * - Uses [vault] if available
         * - Otherwise it will use [java.security.KeyStore]
         * @param info info about key pair generation */
        fun generateKeys(info: KeyInfo.ForKeyStore): KeyPair

        /** Retrieves keys from secure storage.
         * - This should use [vault] if available, otherwise [java.security.KeyStore] as the default storage
         * @return The retrieved keys object. */
        fun getKeys(accessInfo: KeyInfo.ForKeyStore.AccessInfo): KeyPair

        /** Signs a [message] with the user's stored private key (from [vault]).
         * @param message The message to be signed.
         * @param keyAccessInfo Info about accessing the key
         * @return Signature as a [String]. */
        fun sign(message: String, keyAccessInfo: KeyInfo.ForKeyStore.AccessInfo): String
    }

    interface WithCustomVault : ISessionless {
        override val vault: ICustomVault

        /** Generates a private/public key pair and stores it.
         * - Uses [vault] if available
         * - Otherwise it will use [java.security.KeyStore]
         * @param info info about key pair generation */
        fun generateKeys(info: KeyInfo.ForCustom): SimpleKeyPair

        /** Retrieves keys from secure storage.
         * - This should use [vault] if available, otherwise [java.security.KeyStore] as the default storage
         * @return The retrieved keys object. */
        fun getKeys(): SimpleKeyPair

        /** Signs a [message] with the user's stored private key (from [vault]).
         * @param message The message to be signed.
         * @return Signature as a [String]. */
        fun sign(message: String): String
    }

    /** Signs a [message] using the provided [privateKey].
     * @param message The message to be signed.
     * @return Signature as a [String]. */
    fun sign(message: String, privateKey: PrivateKey): String

    /** Verifies a given signature with a public key.
     * @param message The message that was signed earlier (ideally signed with [sign]).
     * @param signature The signature to be verified.
     * @param publicKey The public key to use for verification.
     * @return True if the [signature] is valid for the given [message] and [publicKey].
     * @see sign*/
    fun verifySignature(message: String, signature: String, publicKey: String): Boolean

    /** Creates a unique UUID for a user.
     * @return The generated UUID. */
    fun generateUUID(): String

    /** Associates a secondary's key with the user's primary key.
     * @param associationMessage Additional message for association.
     * @param primarySignature Signature using user's private key.
     * @param secondarySignature Signature using secondary's private key.
     * @param publicKey Secondary's public key. */
    fun associateKeys(
        associationMessage: String,
        primarySignature: String,
        secondarySignature: String,
        publicKey: String
    )

    /** Revokes a gateway's key from the user.
     * @param message Message for revocation.
     * @param signature Signature using user's private key.
     * @param publicKey Gateway's public key. */
    fun revokeKey(message: String, signature: String, publicKey: String)

    companion object {
        const val KEY_PROVIDER = "sessionless"
        const val KEY_ALGORITHM = "RSA"
        const val SIGNATURE_ALGORITHM = "SHA256withRSA"
        const val KEY_SIZE = 1024
        const val RANDOM_ALGORITHM = "SHA1PRNG"
        const val CERTIFICATE_TYPE = "X.509"
    }
}

