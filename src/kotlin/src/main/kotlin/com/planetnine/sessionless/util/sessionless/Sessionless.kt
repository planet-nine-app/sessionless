package com.planetnine.sessionless.util.sessionless

import com.planetnine.sessionless.util.sessionless.Sessionless.WithCustomVault
import com.planetnine.sessionless.util.sessionless.Sessionless.WithKeyStore
import com.planetnine.sessionless.util.sessionless.keys.KeyAccessInfo
import com.planetnine.sessionless.util.sessionless.keys.SimpleKeyPair
import com.planetnine.sessionless.util.sessionless.vaults.ICustomVault
import com.planetnine.sessionless.util.sessionless.vaults.IKeyStoreVault
import com.planetnine.sessionless.util.sessionless.vaults.IVault
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.SecureRandom
import java.security.Security
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import java.util.UUID
import kotlin.coroutines.CoroutineContext

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

        override fun sign(message: String): String {
            val privateString = getKeys().privateKey
            val privateBytes = Base64.getDecoder().decode(privateString)
            val privateSpec = PKCS8EncodedKeySpec(privateBytes)
            val privateKey = KeyFactory.getInstance(KEY_SPEC_NAME)
                .generatePrivate(privateSpec)
            return sign(message, privateKey)
        }
    }

    override fun sign(message: String, privateKey: PrivateKey): String {
        val signature = Signature.getInstance(SIGNATURE_ALGORITHM)
        signature.initSign(privateKey)
        signature.update(message.toByteArray())
        val bytes = signature.sign()
        return String(bytes, Charsets.UTF_8)
    }

    override fun verifySignature(signature: String, message: String, publicKey: String): Boolean {
        val kf = KeyFactory.getInstance(KEY_SPEC_NAME)
        val decoder = Base64.getDecoder()
        val publicKeyBytes = decoder.decode(publicKey)
        val publicKeySpec = X509EncodedKeySpec(publicKeyBytes)
        val pubKey = kf.generatePublic(publicKeySpec)

        val signatureObj = Signature.getInstance(SIGNATURE_ALGORITHM)
        signatureObj.initVerify(pubKey)
        signatureObj.update(message.toByteArray())

        val signatureBytes = decoder.decode(signature)
        return signatureObj.verify(signatureBytes)
    }

    override fun generateUUID(): String = UUID.randomUUID().toString()

    override fun associate(
        primarySignature: String,
        primaryMessage: String,
        primaryPublicKey: String,
        secondarySignature: String,
        secondaryMessage: String,
        secondaryPublicKey: String
    ): Boolean {
        val verified1 = verifySignature(primarySignature, primaryMessage, primaryPublicKey)
        val verified2 = verifySignature(secondarySignature, secondaryMessage, secondaryPublicKey)
        return verified1 && verified2
    }

//    override fun revokeKey(message: String, signature: String, publicKey: String) {
//        TODO("Not yet implemented")
//    }

    companion object {
        const val KEY_ALGORITHM = "ECDSA"
        const val KEY_SPEC_NAME = "secp256k1"
        const val KEY_PROVIDER = "BC"
        const val SIGNATURE_ALGORITHM = "SHA256withRSA"
        const val RANDOM_ALGORITHM = "SHA1PRNG"
        const val CERTIFICATE_TYPE = "X.509"

        /** Generate a new [KeyPair] based on the defaults defined in [Companion]
         * @return public/private [KeyPair]
         * @see generateKeyPairAsync */
        fun generateKeyPair(): KeyPair {
            //? This will add the provider once even if called twice
            Security.addProvider(BouncyCastleProvider())
            val spec = ECNamedCurveTable.getParameterSpec(KEY_SPEC_NAME)
            val generator = KeyPairGenerator.getInstance(KEY_ALGORITHM, KEY_PROVIDER)
            val random = SecureRandom.getInstance(RANDOM_ALGORITHM)
            generator.initialize(spec, random)
            return generator.generateKeyPair()
        }

        /** Generate a new [KeyPair] asynchronously based on the defaults defined in [Companion]
         * @return public/private [KeyPair]
         * @see generateKeyPair */
        suspend fun generateKeyPairAsync(context: CoroutineContext = Dispatchers.IO): KeyPair =
            withContext(context) { generateKeyPair() }
    }
}