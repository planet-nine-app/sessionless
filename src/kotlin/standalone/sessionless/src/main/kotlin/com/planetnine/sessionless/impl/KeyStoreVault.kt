package com.planetnine.sessionless.impl

import com.planetnine.sessionless.impl.exceptions.KeyPairNotFoundException
import com.planetnine.sessionless.models.vaults.IKeyStoreVault
import com.planetnine.sessionless.util.KeyUtils
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

    @Throws(KeyPairNotFoundException::class)
    override fun get(accessInfo: KeyAccessInfo): KeyPair {
        try {
            val privateKey = keyStore.getKey(accessInfo.alias, accessInfo.password) as PrivateKey
            val publicKey = keyStore.getCertificate(accessInfo.alias).publicKey
            return KeyPair(publicKey, privateKey)
        } catch (e: Exception) {
            throw KeyPairNotFoundException(e.message)
        }
    }
}