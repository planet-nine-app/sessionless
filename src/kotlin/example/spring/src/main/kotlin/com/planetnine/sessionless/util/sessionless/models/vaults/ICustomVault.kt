package com.planetnine.sessionless.util.sessionless.models.vaults

import com.planetnine.sessionless.util.sessionless.impl.KeyPairHex

/** [IVault] which uses custom [save]/[get] methods */
interface ICustomVault : IVault {
    fun save(pair: KeyPairHex)
    fun get(): KeyPairHex?
}