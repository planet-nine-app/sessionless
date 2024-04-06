package com.planetnine.sessionless.impl

data class SignedMessage(
    val message: String,
    val publicKey: String,
    val signature: MessageSignatureHex,
)