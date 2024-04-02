package com.planetnine.sessionless.util.sessionless.models.vaults

import com.planetnine.sessionless.util.sessionless.models.SimpleKeyPair

/** [IVault] which uses custom [save]/[get] methods */
interface ICustomVault : IVault {
    fun save(pair: SimpleKeyPair)
    fun get(): SimpleKeyPair
}