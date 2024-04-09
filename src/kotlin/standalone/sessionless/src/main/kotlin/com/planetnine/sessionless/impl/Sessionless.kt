package com.planetnine.sessionless.impl

import com.planetnine.sessionless.impl.Sessionless.WithCustomVault
import com.planetnine.sessionless.impl.Sessionless.WithKeyStore
import com.planetnine.sessionless.impl.exceptions.KeyPairNotFoundException
import com.planetnine.sessionless.models.ISessionless
import com.planetnine.sessionless.models.vaults.ICustomVault
import com.planetnine.sessionless.models.vaults.IVault
import com.planetnine.sessionless.util.KeyUtils
import com.planetnine.sessionless.util.KeyUtils.domainParameters
import com.planetnine.sessionless.util.KeyUtils.toECHex
import com.planetnine.sessionless.util.KeyUtils.toECPrivateKey
import com.planetnine.sessionless.util.KeyUtils.toHex
import com.planetnine.sessionless.util.hashKeccak256
import org.bouncycastle.crypto.params.ECPrivateKeyParameters
import org.bouncycastle.crypto.params.ECPublicKeyParameters
import org.bouncycastle.crypto.signers.ECDSASigner
import java.math.BigInteger
import java.security.KeyPair
import java.security.PrivateKey
import java.security.interfaces.ECPrivateKey
import java.util.UUID

/** [Sessionless] implementation (sealed, check the subclasses)
 * @see WithKeyStore
 * @see WithCustomVault */
sealed class Sessionless(override val vault: IVault) : ISessionless {
    /** [ISessionless.WithKeyStore] implementation with [Sessionless] as superclass */
    class WithKeyStore(override val vault: KeyStoreVault) :
        Sessionless(vault),
        ISessionless.WithKeyStore {

        override fun generateKeys(keyAccessInfo: KeyAccessInfo): KeyPair {
            val pair = KeyUtils.generateKeyPair()
            vault.save(pair, keyAccessInfo)
            return pair
        }

        override suspend fun generateKeysAsync(keyAccessInfo: KeyAccessInfo): KeyPair {
            val pair = KeyUtils.generateKeyPairAsync()
            vault.save(pair, keyAccessInfo)
            return pair
        }

        override fun getKeys(keyAccessInfo: KeyAccessInfo): KeyPair =
            vault.get(keyAccessInfo)

        override fun sign(message: String, keyAccessInfo: KeyAccessInfo): MessageSignatureHex {
            val privateKey = getKeys(keyAccessInfo).private
            return sign(message, privateKey)
        }
    }

    /** [ISessionless.WithCustomVault] implementation with [Sessionless] as superclass */
    class WithCustomVault(override val vault: ICustomVault) :
        Sessionless(vault),
        ISessionless.WithCustomVault {

        override fun generateKeys(): KeyPairHex {
            val pair = KeyUtils.generateKeyPair()
            val simple = pair.toECHex()
            vault.save(simple)
            return simple
        }

        override suspend fun generateKeysAsync(): KeyPairHex {
            val pair = KeyUtils.generateKeyPairAsync()
            val simple = pair.toECHex()
            vault.save(simple)
            return simple
        }

        override fun getKeys(): KeyPairHex? = vault.get()

        override fun sign(message: String): MessageSignatureHex {
            // throw if not found (caller should not call this before generating keys)
            val privateString = getKeys()?.privateKey
                ?: throw KeyPairNotFoundException()
            val privateKey = privateString.toECPrivateKey(KeyUtils.Defaults.parameterSpec)
            return sign(message, privateKey)
        }
    }

    override fun sign(message: String, privateKey: PrivateKey): MessageSignatureHex {
        val signer = ECDSASigner().apply {
            val privateHex = (privateKey as ECPrivateKey).toHex()
            val privateKeyFormatted = BigInteger(privateHex, 16)
            val privateKeyParameters = ECPrivateKeyParameters(
                privateKeyFormatted,
                KeyUtils.Defaults.parameterSpec.domainParameters
            )
            init(true, privateKeyParameters)
        }
        val messageHash = hashKeccak256(message)
        val signatureArray = signer.generateSignature(messageHash)
        return MessageSignatureInt(signatureArray).toHex()
    }

    override fun verifySignature(signedMessage: SignedMessage): Boolean {
        val signer = ECDSASigner().apply {
            val publicInt = BigInteger(signedMessage.publicKey, 16)
            val paramSpec = KeyUtils.Defaults.parameterSpec
            val publicKeyPoint = paramSpec.curve.decodePoint(publicInt.toByteArray())
            val publicKeyParameters = ECPublicKeyParameters(
                publicKeyPoint, paramSpec.domainParameters
            )
            init(false, publicKeyParameters)
        }
        val messageHash = hashKeccak256(signedMessage.message)
        val signature = signedMessage.signature.toInt()
        return signer.verifySignature(
            messageHash,
            signature.r,
            signature.s
        )
    }

    /** Verifies a given signature with a public key
     * - This calls [verifySignature] with [SignedMessage] out of the provided parameters
     * @see verifySignature */
    fun verifySignature(
        message: String,
        publicKey: String,
        signature: MessageSignatureHex
    ): Boolean {
        return verifySignature(SignedMessage(message, publicKey, signature))
    }

    override fun generateUUID(): String = UUID.randomUUID().toString()

    override fun associate(vararg signedMessages: SignedMessage): Boolean {
        if (signedMessages.size < 2) {
            throw IllegalArgumentException("Must have at least two messages to associate")
        }
        return signedMessages.all(::verifySignature)
    }
}