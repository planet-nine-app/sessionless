package com.planetnine.sessionless

import com.planetnine.sessionless.impl.KeyPairHex
import com.planetnine.sessionless.impl.Sessionless
import com.planetnine.sessionless.models.vaults.ICustomVault
import com.planetnine.sessionless.models.vaults.IVault
import java.security.KeyStore

object Common {
    val sessionlessKS
        get(): Sessionless.WithKeyStore {
            val keyStore: KeyStore = KeyStore.getInstance("JKS")
                .apply { load(null, null) }
            return Sessionless.WithKeyStore(IVault.getDefault(keyStore))
        }

    val sessionlessC
        get(): Sessionless.WithCustomVault {
            val vault = object : ICustomVault {
                override fun save(pair: KeyPairHex) {
                }

                override fun get(): KeyPairHex {
                    TODO("Not yet implemented")
                }
            }
            return Sessionless.WithCustomVault(vault)
        }
}