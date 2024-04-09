package com.planetnine.sessionless

import com.planetnine.sessionless.impl.KeyAccessInfo
import com.planetnine.sessionless.impl.MessageSignatureHex
import com.planetnine.sessionless.impl.SignedMessage
import com.planetnine.sessionless.util.KeyUtils.toECHex
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


class SessionlessGeneralTests {

    @DisplayName("Generate and Retrieve Keys with KeyStore")
    @Test

    fun ksGenerateKeysAndRetrieve() {
        val sessionless = Common.sessionlessKS
        val access = KeyAccessInfo("test1")
        val generated = sessionless.generateKeys(access)
        val retrieved = sessionless.getKeys(access)
        // convert to EC hex then compare
        // easier than comparing objects..
        val generatedSimple = generated.toECHex()
        val retrievedSimple = retrieved.toECHex()
        assertEquals(generatedSimple.privateKey, retrievedSimple.privateKey)
        assertEquals(generatedSimple.publicKey, retrievedSimple.publicKey)
    }

    @DisplayName("Generate and Retrieve Keys with KeyStore (With password protection)")
    @Test
    fun ksGenerateKeysAndRetrieveProtected() {
        val sessionless = Common.sessionlessKS
        val access = KeyAccessInfo("test1", "password")
        val generated = sessionless.generateKeys(access)
        val retrieved = sessionless.getKeys(access)
        // convert to EC hex then compare
        // easier than comparing objects..
        val generatedSimple = generated.toECHex()
        val retrievedSimple = retrieved.toECHex()
        assertEquals(generatedSimple.privateKey, retrievedSimple.privateKey)
        assertEquals(generatedSimple.publicKey, retrievedSimple.publicKey)
    }

    @DisplayName("Sign & verify Message with user's private key")
    @Test
    fun verifySignature() {
        val text = "Hello World"
        val sessionless = Common.sessionlessKS
        val access = KeyAccessInfo("test1")
        val generated = sessionless.generateKeys(access)
        val signature = sessionless.sign(
            message = text,
            keyAccessInfo = access,
        )
        // bad signature
        val signatureBad = MessageSignatureHex(
            signature.rHex + "1",
            signature.sHex + "1",
        )
        val publicHex = generated.toECHex().publicKey
        val verified = sessionless.verifySignature(
            SignedMessage(
                message = text,
                signature = signature,
                publicKey = publicHex
            )
        )
        // bad signature should lead to verification failure
        val verifiedBad = sessionless.verifySignature(
            SignedMessage(
                message = text,
                signature = signatureBad,
                publicKey = publicHex
            )
        )
        assertEquals(verified, true)
        assertEquals(verifiedBad, false)
    }
}
