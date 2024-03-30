package com.planetnine.sessionless

interface IKeyPairStore {
    fun save(keys: SimpleKeyPair)
    fun get(): SimpleKeyPair
}