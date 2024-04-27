package com.planetnine.sessionless.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.security.KeyStore


@Configuration
class KeyStoreConfig {
    @Bean
    @Throws(
        java.security.KeyStoreException::class,
        java.security.NoSuchAlgorithmException::class,
        java.security.cert.CertificateException::class,
        java.io.IOException::class,
    )
    fun keyStore(): KeyStore = KeyStore.getInstance(KEYSTORE_TYPE)
        .apply { load(null, null) }

    companion object {
        const val KEYSTORE_TYPE = "JKS"
    }
}