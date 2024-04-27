package com.planetnine.sessionless.util

import org.bouncycastle.jcajce.provider.digest.Keccak

fun String.hashKeccak256(): ByteArray = Keccak.Digest256().digest(toByteArray())