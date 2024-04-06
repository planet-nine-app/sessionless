package com.planetnine.sessionless.models.vaults

import com.planetnine.sessionless.impl.KeyPairHex

/** [IVault] which uses custom [save]/[get] methods */
interface ICustomVault : IVault {
    fun save(pair: KeyPairHex)
    fun get(): KeyPairHex
}