package com.planetnine.sessionless.util.sessionless.vaults

import com.planetnine.sessionless.util.sessionless.keys.KeyAccessInfo
import com.planetnine.sessionless.util.sessionless.keys.KeyUtils
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
        certificateFactory: CertificateFactory = KeyUtils.Defaults.certificateFactory
    ) {
        save(
            pair,
            accessInfo,
            KeyUtils.generateCertificate(pair.public, certificateFactory)
        )
    }

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
    fun get(accessInfo: KeyAccessInfo): KeyPair {
        val privateKey = keyStore.getKey(accessInfo.alias, accessInfo.password) as PrivateKey
        val publicKey = keyStore.getCertificate(accessInfo.alias).publicKey
        return KeyPair(publicKey, privateKey)
    }
}