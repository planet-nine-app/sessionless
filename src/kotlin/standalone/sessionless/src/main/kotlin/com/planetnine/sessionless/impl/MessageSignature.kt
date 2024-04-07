package com.planetnine.sessionless.impl

import com.planetnine.sessionless.impl.exceptions.HexFormatRequiredException
import com.planetnine.sessionless.util.KeyUtils.isBytes
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
            : this(ints[0], ints[1]) {
        if (ints.size != 2) {
            throw IllegalArgumentException("Length must be 2")
        }
    }

    constructor(signatureHex: MessageSignatureHex)
            : this(BigInteger(signatureHex.rHex, 16), BigInteger(signatureHex.sHex, 16))

    /** Convert to [MessageSignatureHex] */
    fun toHex() = MessageSignatureHex(this)
}

/** Message signature as hex [String]s */
open class MessageSignatureHex(
    val rHex: String,
    val sHex: String
) : IMessageSignature {
    init {
        if (!rHex.isBytes() || !sHex.isBytes()) {
            throw HexFormatRequiredException("rHex, sHex")
        }
    }

    constructor(signatureInt: MessageSignatureInt)
            : this(signatureInt.r.toString(16), signatureInt.s.toString(16))

    /** Convert to [MessageSignatureInt] */
    fun toInt() = MessageSignatureInt(this)
}