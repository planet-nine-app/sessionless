package com.planetnine.sessionless.impl

import java.math.BigInteger

/** Common type for message signatures
 * - Empty. Used only to indicate that subclasses are similar */
interface IMessageSignature

/** Message signature as [BigInteger]s */
open class MessageSignatureInt(
    val r: BigInteger,
    val s: BigInteger
) : IMessageSignature {
    constructor(ints: Array<BigInteger>)
            : this(ints[0], ints[1])

    /** Convert to [MessageSignatureHex] */
    fun toHex(): MessageSignatureHex {
        return MessageSignatureHex(r.toString(16), s.toString(16))
    }
}

/** Message signature as hex [String]s */
open class MessageSignatureHex(
    val rHex: String,
    val sHex: String
) : IMessageSignature {
    /** Convert to [MessageSignatureInt] */
    fun toInt(): MessageSignatureInt {
        return MessageSignatureInt(BigInteger(rHex, 16), BigInteger(sHex, 16))
    }
}