package com.planetnine.sessionless.util.sessionless.vaults

import com.planetnine.sessionless.util.sessionless.ISessionless
import java.security.KeyStore


/** The governing interface for vaults used by [ISessionless] */
sealed interface IVault {
    companion object {
        /** Default implementation of [IVault] which uses [KeyStore] ([IKeyStoreVault]) */
        fun getDefault(keyStore: KeyStore) = IKeyStoreVault(keyStore)
    }
}

