package com.planetnine.sessionless.util.sessionless.models.vaults

import com.planetnine.sessionless.util.sessionless.impl.KeyAccessInfo
import java.security.KeyPair
import java.security.KeyStore
import java.security.cert.Certificate

/** [IVault] which uses [KeyStore] to securely get and save [KeyPair] */
interface IKeyStoreVault : IVault {
    val keyStore: KeyStore

    @Throws(java.security.KeyStoreException::class)
    fun save(
        pair: KeyPair,
        accessInfo: KeyAccessInfo,
        certificate: Certificate
    )

    @Throws(
        java.security.KeyStoreException::class,
        java.security.NoSuchAlgorithmException::class,
        java.security.UnrecoverableKeyException::class
    )
    fun get(accessInfo: KeyAccessInfo): KeyPair
}