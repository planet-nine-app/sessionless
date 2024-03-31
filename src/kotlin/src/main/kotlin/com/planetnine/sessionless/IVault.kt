package com.planetnine.sessionless

import java.security.KeyPair
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.Certificate


sealed interface IVault {

    /** Default implementation of [IVault] which uses [KeyStore] ([IKeyStoreVault]) */
    class Default(private val keyStore: KeyStore) : IKeyStoreVault() {
        @Throws(java.security.KeyStoreException::class)
        override fun save(
            alias: String,
            pair: KeyPair,
            password: CharArray?,
            certificate: Certificate
        ) {
            keyStore.setKeyEntry(
                alias,
                pair.private,
                password,
                arrayOf(certificate)
            )
        }

        @Throws(
            java.security.KeyStoreException::class,
            java.security.NoSuchAlgorithmException::class,
            java.security.UnrecoverableKeyException::class
        )
        override fun get(alias: String, password: CharArray?): KeyPair {
            val privateKey = keyStore.getKey(alias, password) as PrivateKey
            val publicKey = keyStore.getCertificate(alias).publicKey
            return KeyPair(publicKey, privateKey)
        }
    }
}

