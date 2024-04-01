package com.planetnine.sessionless.util.sessionless.vaults

import com.planetnine.sessionless.util.sessionless.Sessionless
import com.planetnine.sessionless.util.sessionless.keys.KeyAccessInfo
import java.io.ByteArrayInputStream
import java.security.KeyPair
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.Certificate
import java.security.cert.CertificateFactory

/** [IVault] which uses [KeyStore] to securely get and save [KeyPair] */
class IKeyStoreVault(private val keyStore: KeyStore) : IVault {
    @Throws(java.security.cert.CertificateException::class)
    fun save(
        pair: KeyPair,
        accessInfo: KeyAccessInfo,
        certificateFactory: CertificateFactory = CertificateFactory.getInstance(Sessionless.CERTIFICATE_TYPE)
    ) = save(
        pair,
        accessInfo,
        certificateFactory.generateCertificate(
            ByteArrayInputStream(pair.public.encoded)
        )
    )

    @Throws(java.security.KeyStoreException::class)
    fun save(
        pair: KeyPair,
        accessInfo: KeyAccessInfo,
        certificate: Certificate
    ) {
        keyStore.setKeyEntry(
            accessInfo.alias,
            pair.private,
            accessInfo.password,
            arrayOf(certificate)
        )
    }

    @Throws(
        java.security.KeyStoreException::class,
        java.security.NoSuchAlgorithmException::class,
        java.security.UnrecoverableKeyException::class
    )
    fun get(alias: String, password: CharArray?): KeyPair {
        val privateKey = keyStore.getKey(alias, password) as PrivateKey
        val publicKey = keyStore.getCertificate(alias).publicKey
        return KeyPair(publicKey, privateKey)
    }
}