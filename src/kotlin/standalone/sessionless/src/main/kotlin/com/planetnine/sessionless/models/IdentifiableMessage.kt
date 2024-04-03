package com.planetnine.sessionless.models

/** Represents a message that was signed
 * @param publicKey The public key used while signing
 * @param signature The signature of the message
 * @param message The message that was signed */
data class IdentifiableMessage(
    val message: String,
    val publicKey: String,
    val signature: MessageSignature,
)