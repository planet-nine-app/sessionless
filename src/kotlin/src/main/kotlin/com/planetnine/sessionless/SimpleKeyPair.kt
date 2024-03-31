package com.planetnine.sessionless

import java.security.KeyPair
import java.util.Base64

/** Simplified key pair (as [String]s)*/
data class SimpleKeyPair(val privateKey: String, val publicKey: String) {
    companion object {
        fun from(pair: KeyPair): SimpleKeyPair {
            val encoder = Base64.getEncoder()
            return SimpleKeyPair(
                encoder.encodeToString(pair.private.encoded),
                encoder.encodeToString(pair.public.encoded)
            )
        }
    }
}