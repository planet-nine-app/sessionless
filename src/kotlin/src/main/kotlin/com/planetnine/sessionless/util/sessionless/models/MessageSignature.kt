package com.planetnine.sessionless.util.sessionless.models

import java.math.BigInteger

data class MessageSignature(val r: BigInteger, val s: BigInteger) {
    constructor(rHex: String, sHex: String) : this(
        rHex.toBigInteger(16),
        sHex.toBigInteger(16)
    )

    companion object {
        fun from(ints: Array<BigInteger>): MessageSignature? {
            if (ints.size != 2) return null
            return MessageSignature(ints[0], ints[1])
        }
    }
}