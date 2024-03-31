package com.planetnine.sessionless

import java.security.KeyStore


sealed interface IVault {
    companion object {
        /** Default implementation of [IVault] which uses [KeyStore] ([IKeyStoreVault]) */
        fun getDefault(keyStore: KeyStore) = IKeyStoreVault.Default(keyStore)
    }
}

