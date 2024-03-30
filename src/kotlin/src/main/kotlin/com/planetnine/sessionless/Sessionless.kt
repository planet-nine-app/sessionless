package com.planetnine.sessionless

import org.springframework.beans.factory.annotation.Autowired
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.SecureRandom
import java.util.UUID

class Sessionless(
    private val keyFactoryInfo: KeyFactoryInfo = KeyFactoryInfo(),
    private var customKeysProvider: IKeyPairStore? = null
) : ISessionless {

    @Autowired
    private lateinit var keyStore: KeyStore

    private fun newKeyPair(): KeyPair {
        val gen = KeyPairGenerator.getInstance(
            keyFactoryInfo.algorithm,
            keyFactoryInfo.provider
        )
        val random = SecureRandom.getInstance(
            keyFactoryInfo.randomAlgorithm,
            keyFactoryInfo.provider
        )
        gen.initialize(Defaults.KEY_SIZE, random)
        return gen.generateKeyPair()
    }

    override fun generateKeys(
        saveKeys: (keys: SimpleKeyPair) -> Unit,
        getKeys: () -> SimpleKeyPair
    ) {
        val provider = object : IKeyPairStore {
            override fun save(keys: SimpleKeyPair) = saveKeys(keys)
            override fun get() = getKeys()
        }
        customKeysProvider = provider
        generateKeys()
    }


    /** Generates a private/public key pair and stores it in the platform's secure storage.
     * - ⚠️ with a custom [store] ([IKeyPairStore]).
     * @param store provides a custom way to get/save keys. */
    fun generateKeys(store: IKeyPairStore) = generateKeys(store::save, store::get)

    /** Generates a private/public key pair and stores it in the platform's secure storage.
     * - Uses platform's default secure storage ([java.security.KeyStore]) */
    fun generateKeys() {
        val pair = newKeyPair()
        val provider = customKeysProvider
        if (provider == null) {
            TODO("Not yet implemented")
            val platformStore = object : IKeyPairStore {
                override fun save(keys: SimpleKeyPair) {
//                    keyStore.setKeyEntry(
//                        keyFactoryInfo.alias,
//                        pair.private,
//                        keyFactoryInfo.password?.toCharArray(),
//                        pair.public
//                    )
                    TODO()
                }

                override fun get(): SimpleKeyPair {
//                    val entry = keyStore.getEntry(keyFactoryInfo.alias, null)
//                            as? KeyStore.PrivateKeyEntry
                    TODO()
                }
            }
            customKeysProvider = platformStore
        } else {
            val simpleKeyPair = SimpleKeyPair.from(pair.private, pair.public)
            provider.save(simpleKeyPair)
        }
    }

    override fun getKeys(): SimpleKeyPair {
        val provider = customKeysProvider
        if (provider != null) return provider.get()

        TODO("Not yet implemented")
    }

    override fun sign(message: String): String {
        TODO("Not yet implemented")
    }

    override fun verifySignature(message: String, signature: String, publicKey: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun generateUUID(): String =
        UUID.randomUUID().toString()


    override fun associateKeys(
        associationMessage: String,
        primarySignature: String,
        secondarySignature: String,
        publicKey: String
    ) {
        TODO("Not yet implemented")
    }

    override fun revokeKey(message: String, signature: String, publicKey: String) {
        TODO("Not yet implemented")
    }

    object Defaults {
        const val KEY_ALIAS = "sessionless"
        const val KEY_PROVIDER = "sessionless"
        const val KEY_ALGORITHM = "RSA"
        const val KEY_SIZE = 1024
        const val RANDOM_ALGORITHM = "SHA1PRNG"
    }

    /** Information about the creation of a key pair
     * - Default values are defined in [Defaults]
     * @param alias Key alias (used in storage)
     * @param password password securing the key pair
     * @param algorithm algorithm used to generate the key pair
     * @param provider provider used to generate the key pair
     * @param size size in bits
     * @param randomAlgorithm algorithm used to generate the secure random */
    data class KeyFactoryInfo(
        val alias: String = Defaults.KEY_ALIAS,
        val password: String? = null,
        val algorithm: String = Defaults.KEY_ALGORITHM,
        val provider: String = Defaults.KEY_PROVIDER,
        val size: Int = Defaults.KEY_SIZE,
        val randomAlgorithm: String = Defaults.RANDOM_ALGORITHM,
    )
}

