package com.planetnine.sessionless.models.vaults

import com.planetnine.sessionless.impl.KeyStoreVault
import com.planetnine.sessionless.models.ISessionless
import java.security.KeyStore


/** The governing interface for vaults used by [ISessionless] */
sealed interface IVault {
    companion object {
        /** Default implementation of [IVault] which uses [KeyStore] ([KeyStoreVault]) */
        fun getDefault(keyStore: KeyStore) = KeyStoreVault(keyStore)
    }
}

