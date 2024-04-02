package com.planetnine.sessionless.util.sessionless.keys

import java.math.BigInteger

data class MessageSignature(val r: BigInteger, val s: BigInteger) {
    fun toHex() = HexMessageSignature(r.toString(16), s.toString(16))

    companion object {
        fun from(ints: Array<BigInteger>): MessageSignature? {
            if (ints.size != 2) return null
            return MessageSignature(ints[0], ints[1])
        }
    }
}

data class HexMessageSignature(val r: String, val s: String) {
    fun toBigInt() = MessageSignature(r.toBigInteger(16), s.toBigInteger(16))
}