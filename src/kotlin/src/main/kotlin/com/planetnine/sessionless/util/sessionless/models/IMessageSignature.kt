package com.planetnine.sessionless.util.sessionless.models

import java.math.BigInteger

interface IMessageSignature {
    val r: BigInteger
    val s: BigInteger
    val rHex get() = r.toString(16)
    val sHex get() = s.toString(16)
}