package com.planetnine.sessionless.util.sessionless.models.vaults

import com.planetnine.sessionless.util.sessionless.impl.KeyStoreVault
import com.planetnine.sessionless.util.sessionless.models.ISessionless
import java.security.KeyStore


/** The governing interface for vaults used by [ISessionless] */
sealed interface IVault {
    companion object {
        /** Default implementation of [IVault] which uses [KeyStore] ([KeyStoreVault]) */
        fun getDefault(keyStore: KeyStore) = KeyStoreVault(keyStore)
    }
}

