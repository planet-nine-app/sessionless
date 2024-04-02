package com.planetnine.sessionless.util.sessionless.keys

import com.planetnine.sessionless.util.sessionless.models.IMessageSignature
import java.math.BigInteger

data class MessageSignature(
    override val r: BigInteger,
    override val s: BigInteger
) : IMessageSignature {
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