package com.planetnine.sessionless.impl

import com.planetnine.sessionless.impl.exceptions.HexFormatRequiredException
import com.planetnine.sessionless.util.KeyUtils.isBytes
import com.planetnine.sessionless.util.KeyUtils.toECParameters
import org.bouncycastle.crypto.params.ECPublicKeyParameters
import java.security.PublicKey

open class SignedMessage(
    val message: String,
    val signature: MessageSignatureHex,
) {
    fun withKey(publicKey: String) =
        SignedMessageWithKey(this, publicKey)

    fun withKey(publicKey: PublicKey) =
        SignedMessageWithECKey(this, publicKey)

    fun withKey(publicKey: ECPublicKeyParameters) =
        SignedMessageWithECKey(this, publicKey)
}

class SignedMessageWithKey(
    message: String,
    signature: MessageSignatureHex,
    val publicKey: String,
) : SignedMessage(message, signature) {
    init {
        if (!publicKey.isBytes()) {
            throw HexFormatRequiredException("publicKey")
        }
    }

    constructor(signedMessage: SignedMessage, publicKey: String) :
            this(signedMessage.message, signedMessage.signature, publicKey)
}

class SignedMessageWithECKey(
    message: String,
    signature: MessageSignatureHex,
    val publicKey: ECPublicKeyParameters,
) : SignedMessage(message, signature) {

    constructor(
        message: String,
        signature: MessageSignatureHex,
        publicKey: PublicKey
    ) : this(message, signature, publicKey.toECParameters())

    constructor(
        signedMessage: SignedMessage,
        publicKey: PublicKey
    ) : this(signedMessage.message, signedMessage.signature, publicKey)

    constructor(
        signedMessage: SignedMessage,
        publicKey: ECPublicKeyParameters
    ) : this(signedMessage.message, signedMessage.signature, publicKey)
}