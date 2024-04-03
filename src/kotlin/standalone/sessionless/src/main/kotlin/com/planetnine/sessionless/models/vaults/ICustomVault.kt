package com.planetnine.sessionless.models.vaults

import com.planetnine.sessionless.models.SimpleKeyPair

/** [IVault] which uses custom [save]/[get] methods */
interface ICustomVault : IVault {
    fun save(pair: SimpleKeyPair)
    fun get(): SimpleKeyPair
}