package com.planetnine.sessionless.impl

import com.planetnine.sessionless.impl.Sessionless.WithCustomVault
import com.planetnine.sessionless.impl.Sessionless.WithKeyStore
import com.planetnine.sessionless.impl.exceptions.KeyPairNotFoundException
import com.planetnine.sessionless.models.ISessionless
import com.planetnine.sessionless.models.vaults.ICustomVault
import com.planetnine.sessionless.models.vaults.IVault
import com.planetnine.sessionless.util.KeyUtils
import com.planetnine.sessionless.util.KeyUtils.toECHex
import com.planetnine.sessionless.util.KeyUtils.toECPrivateKey
import com.planetnine.sessionless.util.KeyUtils.toECPrivateKeyParameters
import com.planetnine.sessionless.util.KeyUtils.toECPublicKeyParameters
import com.planetnine.sessionless.util.KeyUtils.toHex
import com.planetnine.sessionless.util.hashKeccak256
import org.bouncycastle.crypto.signers.ECDSASigner
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

        override fun verifySignature(
            signedMessage: SignedMessageWithECKey,
            keyAccessInfo: KeyAccessInfo
        ): Boolean {
            val publicKey = getKeys(keyAccessInfo).public
            val withKey = signedMessage.withKey(publicKey)
            return verifySignature(withKey)
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

        override fun verifySignature(signedMessage: SignedMessage): Boolean {
            val pair = getKeys() ?: throw KeyPairNotFoundException()
            val withKey = signedMessage.withKey(pair.publicKey)
            return verifySignature(withKey)
        }
    }

    override fun sign(message: String, privateKey: PrivateKey): MessageSignatureHex {
        val privateHex = (privateKey as ECPrivateKey).toHex()
        val privateKeyParameters = privateHex.toECPrivateKeyParameters()
        val signer = ECDSASigner().apply {
            init(true, privateKeyParameters)
        }
        val messageHash = message.hashKeccak256()
        val signatureArray = signer.generateSignature(messageHash)
        return MessageSignatureInt(signatureArray).toHex()
    }

    override fun verifySignature(signedMessage: SignedMessageWithKey): Boolean {
        val publicKeyParameters = signedMessage.publicKey.toECPublicKeyParameters()
        val withECKey = signedMessage.withKey(publicKeyParameters)
        return verifySignature(withECKey)
    }

    override fun verifySignature(signedMessage: SignedMessageWithECKey): Boolean {
        val signer = ECDSASigner().apply {
            init(false, signedMessage.publicKey)
        }
        val messageHash = signedMessage.message.hashKeccak256()
        val signatureInt = signedMessage.signature.toInt()
        return signer.verifySignature(
            messageHash,
            signatureInt.r,
            signatureInt.s
        )
    }

    override fun generateUUID(): String = UUID.randomUUID().toString()


    override fun associate(vararg signedMessages: SignedMessageWithKey): Boolean {
        signedMessages.twoOrThrow()
        return signedMessages.all(::verifySignature)
    }

    override fun associate(vararg signedMessages: SignedMessageWithECKey): Boolean {
        signedMessages.twoOrThrow()
        return signedMessages.all(::verifySignature)
    }

    companion object {
        private fun <T> Array<T>.twoOrThrow() {
            if (size < 2) {
                throw IllegalArgumentException("Must have at least two messages to associate")
            }
        }
    }
}