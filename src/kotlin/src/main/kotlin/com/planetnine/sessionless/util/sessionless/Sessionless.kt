package com.planetnine.sessionless.util.sessionless

import com.planetnine.sessionless.util.sessionless.Sessionless.WithCustomVault
import com.planetnine.sessionless.util.sessionless.Sessionless.WithKeyStore
import com.planetnine.sessionless.util.sessionless.keys.HexMessageSignature
import com.planetnine.sessionless.util.sessionless.keys.KeyAccessInfo
import com.planetnine.sessionless.util.sessionless.keys.KeyUtils
import com.planetnine.sessionless.util.sessionless.keys.KeyUtils.domainParameters
import com.planetnine.sessionless.util.sessionless.keys.KeyUtils.toECHex
import com.planetnine.sessionless.util.sessionless.keys.KeyUtils.toECPrivateKey
import com.planetnine.sessionless.util.sessionless.keys.KeyUtils.toHex
import com.planetnine.sessionless.util.sessionless.keys.MessageSignature
import com.planetnine.sessionless.util.sessionless.keys.SimpleKeyPair
import com.planetnine.sessionless.util.sessionless.vaults.ICustomVault
import com.planetnine.sessionless.util.sessionless.vaults.IKeyStoreVault
import com.planetnine.sessionless.util.sessionless.vaults.IVault
import org.bouncycastle.crypto.params.ECPrivateKeyParameters
import org.bouncycastle.crypto.params.ECPublicKeyParameters
import org.bouncycastle.crypto.signers.ECDSASigner
import org.bouncycastle.jcajce.provider.digest.Keccak
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
    class WithKeyStore(override val vault: IKeyStoreVault) :
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

        override fun sign(message: String, keyAccessInfo: KeyAccessInfo): HexMessageSignature {
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

        override fun sign(message: String): HexMessageSignature {
            val privateString = getKeys().privateKey
            val privateKey = privateString.toECPrivateKey(KeyUtils.Defaults.parameterSpec)
            return sign(message, privateKey)
        }
    }

    override fun sign(message: String, privateKey: PrivateKey): HexMessageSignature {
        val signer = ECDSASigner().apply {
            val privateHex = (privateKey as ECPrivateKey).toHex()
            val privateKeyFormatted = BigInteger(privateHex)
            val privateKeyParameters = ECPrivateKeyParameters(
                privateKeyFormatted,
                KeyUtils.Defaults.parameterSpec.domainParameters
            )
            init(true, privateKeyParameters)
        }
        val messageHash = keccak256Hash(message)
        val signature = signer.generateSignature(messageHash)
        return MessageSignature.from(signature)!!.toHex()
    }

    override fun verify(
        publicKey: String,
        signature: HexMessageSignature,
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
        val messageHash = keccak256Hash(message)
        val signatureInts = signature.toBigInt()
        return signer.verifySignature(
            messageHash, signatureInts.r, signatureInts.s
        )
    }

    override fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }

    override fun associate(
        primaryPublicKey: String,
        primaryMessage: String,
        primarySignature: HexMessageSignature,
        secondaryPublicKey: String,
        secondaryMessage: String,
        secondarySignature: HexMessageSignature,
    ): Boolean {
        val verified1 = verify(primaryPublicKey, primarySignature, primaryMessage)
        val verified2 = verify(secondaryPublicKey, secondarySignature, secondaryMessage)
        return verified1 && verified2
    }

    companion object {
        fun keccak256Hash(message: String) = Keccak.Digest256().digest(message.toByteArray())
    }
}