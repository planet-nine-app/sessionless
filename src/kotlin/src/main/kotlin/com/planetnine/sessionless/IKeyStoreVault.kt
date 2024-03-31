package com.planetnine.sessionless

import java.io.ByteArrayInputStream
import java.security.KeyPair
import java.security.cert.Certificate
import java.security.cert.CertificateFactory

abstract class IKeyStoreVault : IVault {
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
}