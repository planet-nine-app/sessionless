package com.planetnine.sessionless.util

import org.bouncycastle.jcajce.provider.digest.Keccak

fun hashKeccak256(message: String) = Keccak.Digest256().digest(message.toByteArray())