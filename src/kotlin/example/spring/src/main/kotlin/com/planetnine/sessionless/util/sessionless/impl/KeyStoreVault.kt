package com.planetnine.sessionless.util.sessionless.impl

import com.planetnine.sessionless.util.sessionless.models.KeyAccessInfo
import com.planetnine.sessionless.util.sessionless.models.vaults.IKeyStoreVault
import com.planetnine.sessionless.util.sessionless.util.KeyUtils
import java.security.KeyPair
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.Certificate

/** [IKeyStoreVault] implementation */
class KeyStoreVault(override val keyStore: KeyStore) : IKeyStoreVault {
    @Throws(java.security.cert.CertificateException::class)
    fun save(pair: KeyPair, accessInfo: KeyAccessInfo) {
        save(
            pair,
            accessInfo,
            KeyUtils.generateSelfSignedCertificate(pair)
        )
    }

    @Throws(java.security.KeyStoreException::class)
    override fun save(
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
    override fun get(accessInfo: KeyAccessInfo): KeyPair {
        val privateKey = keyStore.getKey(accessInfo.alias, accessInfo.password) as PrivateKey
        val publicKey = keyStore.getCertificate(accessInfo.alias).publicKey
        return KeyPair(publicKey, privateKey)
    }
}