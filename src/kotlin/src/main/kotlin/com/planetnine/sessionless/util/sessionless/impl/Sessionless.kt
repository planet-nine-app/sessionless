package com.planetnine.sessionless.util.sessionless.impl

import com.planetnine.sessionless.util.sessionless.impl.Sessionless.WithCustomVault
import com.planetnine.sessionless.util.sessionless.impl.Sessionless.WithKeyStore
import com.planetnine.sessionless.util.sessionless.models.ISessionless
import com.planetnine.sessionless.util.sessionless.models.KeyAccessInfo
import com.planetnine.sessionless.util.sessionless.models.MessageSignature
import com.planetnine.sessionless.util.sessionless.models.SimpleKeyPair
import com.planetnine.sessionless.util.sessionless.models.vaults.ICustomVault
import com.planetnine.sessionless.util.sessionless.models.vaults.IVault
import com.planetnine.sessionless.util.sessionless.util.KeyUtils
import com.planetnine.sessionless.util.sessionless.util.KeyUtils.domainParameters
import com.planetnine.sessionless.util.sessionless.util.KeyUtils.toECHex
import com.planetnine.sessionless.util.sessionless.util.KeyUtils.toECPrivateKey
import com.planetnine.sessionless.util.sessionless.util.KeyUtils.toHex
import com.planetnine.sessionless.util.sessionless.util.hashKeccak256
import org.bouncycastle.crypto.params.ECPrivateKeyParameters
import org.bouncycastle.crypto.params.ECPublicKeyParameters
import org.bouncycastle.crypto.signers.ECDSASigner
import java.math.BigInteger
import java.security.KeyPair
import java.security.PrivateKey
import java.security.interfaces.ECPrivateKey
import java.util.Base64
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

        override fun getKeys(keyAccessInfo: KeyAccessInfo): KeyPair {
            return vault.get(keyAccessInfo)
        }

        override fun sign(message: String, keyAccessInfo: KeyAccessInfo): MessageSignature {
            val privateKey = getKeys(keyAccessInfo).private
            return sign(message, privateKey)
        }
    }

    /** [ISessionless.WithCustomVault] implementation with [Sessionless] as superclass */
    class WithCustomVault(override val vault: ICustomVault) :
        Sessionless(vault),
        ISessionless.WithCustomVault {

        override fun generateKeys(): SimpleKeyPair {
            val pair = KeyUtils.generateKeyPair()
            val simple = pair.toECHex()
            vault.save(simple)
            return simple
        }

        override suspend fun generateKeysAsync(): SimpleKeyPair {
            val pair = KeyUtils.generateKeyPairAsync()
            val simple = pair.toECHex()
            vault.save(simple)
            return simple
        }

        override fun getKeys(): SimpleKeyPair {
            return vault.get()
        }

        override fun sign(message: String): MessageSignature {
            val privateString = getKeys().privateKey
            val privateKey = privateString.toECPrivateKey(KeyUtils.Defaults.parameterSpec)
            return sign(message, privateKey)
        }
    }

    override fun sign(message: String, privateKey: PrivateKey): MessageSignature {
        val signer = ECDSASigner().apply {
            val privateHex = (privateKey as ECPrivateKey).toHex()
            val privateKeyFormatted = BigInteger(privateHex)
            val privateKeyParameters = ECPrivateKeyParameters(
                privateKeyFormatted,
                KeyUtils.Defaults.parameterSpec.domainParameters
            )
            init(true, privateKeyParameters)
        }
        val messageHash = hashKeccak256(message)
        val signature = signer.generateSignature(messageHash)
        return MessageSignature.from(signature)!!
    }

    override fun verify(
        publicKey: String,
        signature: MessageSignature,
        message: String
    ): Boolean {
        val signer = ECDSASigner().apply {
            val publicBytes = Base64.getDecoder().decode(publicKey)
            val publicInt = BigInteger(publicBytes)
            val paramSpec = KeyUtils.Defaults.parameterSpec
            val publicKeyPoint = paramSpec.curve.decodePoint(publicInt.toByteArray())
            val publicKeyParameters = ECPublicKeyParameters(
                publicKeyPoint, paramSpec.domainParameters
            )
            init(false, publicKeyParameters)
        }
        val messageHash = hashKeccak256(message)
        return signer.verifySignature(
            messageHash, signature.r, signature.s
        )
    }

    override fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }

    override fun associate(
        primaryPublicKey: String,
        primaryMessage: String,
        primarySignature: MessageSignature,
        secondaryPublicKey: String,
        secondaryMessage: String,
        secondarySignature: MessageSignature,
    ): Boolean {
        val verified1 = verify(primaryPublicKey, primarySignature, primaryMessage)
        val verified2 = verify(secondaryPublicKey, secondarySignature, secondaryMessage)
        return verified1 && verified2
    }
}