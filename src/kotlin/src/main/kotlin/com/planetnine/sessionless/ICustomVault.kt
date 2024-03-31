package com.planetnine.sessionless

interface ICustomVault : IVault {
    fun save(pair: SimpleKeyPair)
    fun get(): SimpleKeyPair
}