package com.planetnine.sessionless.util.sessionless.vaults

import com.planetnine.sessionless.util.sessionless.keys.SimpleKeyPair

/** [IVault] which uses custom [save]/[get] methods */
interface ICustomVault : IVault {
    fun save(pair: SimpleKeyPair)
    fun get(): SimpleKeyPair
}