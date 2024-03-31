package com.planetnine.sessionless

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import kotlin.coroutines.CoroutineContext

/** Information about the creation of a key pair
 * - Default values are defined in [ISessionless.Companion]
 * @param algorithm algorithm used to generate the key pair
 * @param provider provider used to generate the key pair
 * @param size size in bits
 * @param randomAlgorithm algorithm used to generate the secure random */
sealed class KeyInfo(
    val algorithm: String,
    val provider: String,
    val size: Int,
    val randomAlgorithm: String,
) {
    /** Generate a new [KeyPair] based on the provided info in this [KeyInfo]
     * @return public/private [KeyPair]
     * @see generateKeyPairAsync */
    fun generateKeyPair(): KeyPair {
        val provider = ISessionless.KEY_PROVIDER
        val gen = KeyPairGenerator.getInstance(
            ISessionless.KEY_ALGORITHM, provider
        )
        val random = SecureRandom.getInstance(
            ISessionless.RANDOM_ALGORITHM, provider
        )
        gen.initialize(ISessionless.KEY_SIZE, random)
        return gen.generateKeyPair()
    }

    /** Generate a new [KeyPair] asynchronously based on the provided info in this [KeyInfo]
     * @return public/private [KeyPair]
     * @see generateKeyPair */
    suspend fun generateKeyPairAsync(context: CoroutineContext = Dispatchers.IO): KeyPair =
        withContext(context) { generateKeyPair() }

    class ForKeyStore(
        val accessInfo: AccessInfo,
        val certificateFactory: CertificateFactory = CertificateFactory.getInstance(ISessionless.CERTIFICATE_TYPE),
        algorithm: String = ISessionless.KEY_ALGORITHM,
        provider: String = ISessionless.KEY_PROVIDER,
        size: Int = ISessionless.KEY_SIZE,
        randomAlgorithm: String = ISessionless.RANDOM_ALGORITHM,
    ) : KeyInfo(
        algorithm,
        provider,
        size,
        randomAlgorithm,
    ) {
        /** Information about accessing a key in secure storage
         * @param alias Key alias (used in storage)
         * @param password password securing the key pair */
        class AccessInfo(val alias: String, val password: CharArray? = null)
    }

    // This is because I want KeyFactory to be inheritable but only by these.. so this is basically a duplicate of KeyInfo itself
    class ForCustom(
        algorithm: String = ISessionless.KEY_ALGORITHM,
        provider: String = ISessionless.KEY_PROVIDER,
        size: Int = ISessionless.KEY_SIZE,
        randomAlgorithm: String = ISessionless.RANDOM_ALGORITHM,
    ) : KeyInfo(
        algorithm,
        provider,
        size,
        randomAlgorithm,
    )
}
