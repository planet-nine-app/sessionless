package com.planetnine.sessionless

interface ISessionless {

    /** Generates a private/public key pair and stores it in the platform's secure storage.
     * - ⚠️ with custom [getKeys]/[saveKeys] functions.
     * @param saveKeys custom way to save keys.
     * @param getKeys custom way to get keys. */
    fun generateKeys(
        saveKeys: ((keys: SimpleKeyPair) -> Unit),
        getKeys: (() -> SimpleKeyPair)
    )


    /** Retrieves keys from secure storage.
     * @return The retrieved keys object. */
    fun getKeys(): SimpleKeyPair

    /** Signs a message with the user's private key.
     * @param message The message to be signed.
     * @return The signed message. */
    fun sign(message: String): String

    /** Verifies a given signature with a public key.
     * @param message The message that was signed.
     * @param signature The signature to be verified.
     * @param publicKey The public key to use for verification.
     * @return True if the signature is valid, false otherwise. */
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
}

