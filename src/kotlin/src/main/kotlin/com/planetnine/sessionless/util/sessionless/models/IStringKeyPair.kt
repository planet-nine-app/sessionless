package com.planetnine.sessionless.util.sessionless.models


/** [String] pair of public/private keys */
interface IStringKeyPair {
    val publicKey: String
    val privateKey: String
}