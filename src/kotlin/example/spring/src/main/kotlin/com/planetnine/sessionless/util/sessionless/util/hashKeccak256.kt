package com.planetnine.sessionless.util.sessionless.util

import org.bouncycastle.jcajce.provider.digest.Keccak

fun hashKeccak256(message: String): ByteArray =
    Keccak.Digest256().digest(message.toByteArray())