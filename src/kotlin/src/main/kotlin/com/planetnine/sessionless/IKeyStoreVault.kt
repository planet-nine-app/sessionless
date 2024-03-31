package com.planetnine.sessionless

import java.io.ByteArrayInputStream
import java.security.KeyPair
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.Certificate
import java.security.cert.CertificateFactory

/** Abstract definition of [IVault] which uses [KeyStore] */
abstract class IKeyStoreVault(protected val keyStore: KeyStore) : IVault {
    @Throws(java.security.cert.CertificateException::class)
    fun save(
        alias: String,
        pair: KeyPair,
        password: CharArray? = null,
        certificateFactory: CertificateFactory = CertificateFactory.getInstance("X.509")
    ) = save(
        alias,
        pair,
        password,
        certificateFactory.generateCertificate(
            ByteArrayInputStream(pair.public.encoded)
        )
    )

    abstract fun save(
        alias: String,
        pair: KeyPair,
        password: CharArray? = null,
        certificate: Certificate,
    )

    abstract fun get(alias: String, password: CharArray? = null): KeyPair


    /** Default implementation of [IKeyStoreVault] */
    class Default(keyStore: KeyStore) : IKeyStoreVault(keyStore) {
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