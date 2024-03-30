package com.planetnine.sessionless

import java.security.PrivateKey
import java.security.PublicKey
import java.util.Base64

/** Simplified key pair (as [String]s)*/
data class SimpleKeyPair(val privateKey: String, val publicKey: String) {
    companion object {
        fun from(privateKey: PrivateKey, publicKey: PublicKey): SimpleKeyPair {
            val encoder = Base64.getEncoder()
            return SimpleKeyPair(
                encoder.encodeToString(privateKey.encoded),
                encoder.encodeToString(publicKey.encoded)
            )
        }
    }
}