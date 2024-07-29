package com.planetnine.sessionless.impl

/** Simplified key pair type, comprised of hex [String]s */
data class KeyPairHex(
    val privateKey: String,
    val publicKey: String
)