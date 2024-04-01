package com.planetnine.sessionless

import com.planetnine.sessionless.Sessionless.WithCustomVault
import com.planetnine.sessionless.Sessionless.WithKeyStore
import java.security.KeyFactory
import java.security.KeyPair
import java.security.PrivateKey
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import java.util.UUID

/** [Sessionless] implementation (sealed)
 * @see WithKeyStore
 * @see WithCustomVault */
sealed class Sessionless(override val vault: IVault) : ISessionless {
    /** [Sessionless.WithKeyStore] implementation */
    class WithKeyStore(override val vault: IKeyStoreVault) :
        Sessionless(vault),
        ISessionless.WithKeyStore {
        override fun generateKeys(info: KeyInfo.ForKeyStore): KeyPair {
            val pair = info.generateKeyPair()
            vault.save(
                info.accessInfo.alias,
                pair,
                info.accessInfo.password,
                info.certificateFactory
            )
            return pair
        }

        override fun getKeys(accessInfo: KeyInfo.ForKeyStore.AccessInfo): KeyPair =
            vault.get(accessInfo.alias, accessInfo.password)

        override fun sign(message: String, keyAccessInfo: KeyInfo.ForKeyStore.AccessInfo): String {
            val privateKey = getKeys(keyAccessInfo).private
            return sign(message, privateKey)
        }
    }

    /** [Sessionless.WithCustomVault] implementation */
    class WithCustomVault(override val vault: ICustomVault) :
        Sessionless(vault),
        ISessionless.WithCustomVault {
        override fun generateKeys(info: KeyInfo.ForCustom): SimpleKeyPair {
            val pair = info.generateKeyPair()
            val simple = SimpleKeyPair.from(pair)
            vault.save(simple)
            return simple
        }

        override fun getKeys(): SimpleKeyPair = vault.get()

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

    override fun verifySignature(message: String, signature: String, publicKey: String): Boolean {
        val kf = KeyFactory.getInstance(ISessionless.KEY_ALGORITHM)
        val kf = KeyFactory.getInstance(KEY_SPEC_NAME)
        val decoder = Base64.getDecoder()
        val publicKeyBytes = decoder.decode(publicKey)
        val publicKeySpec = X509EncodedKeySpec(publicKeyBytes)
        val pubKey = kf.generatePublic(publicKeySpec)

        val signatureObj = Signature.getInstance(ISessionless.SIGNATURE_ALGORITHM)
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

    override fun revokeKey(message: String, signature: String, publicKey: String) {
        TODO("Not yet implemented")
    }

    companion object {
        const val KEY_ALGORITHM = "ECDSA"
        const val KEY_SPEC_NAME = "secp256k1"
        const val KEY_PROVIDER = "BC"
        const val SIGNATURE_ALGORITHM = "SHA256withRSA"
        const val RANDOM_ALGORITHM = "SHA1PRNG"
        const val CERTIFICATE_TYPE = "X.509"

        /** Generate a new [KeyPair] based on the provided info in this [KeyInfo]
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

        /** Generate a new [KeyPair] asynchronously based on the provided info in this [KeyInfo]
         * @return public/private [KeyPair]
         * @see generateKeyPair */
        suspend fun generateKeyPairAsync(context: CoroutineContext = Dispatchers.IO): KeyPair =
            withContext(context) { generateKeyPair() }
    }
}