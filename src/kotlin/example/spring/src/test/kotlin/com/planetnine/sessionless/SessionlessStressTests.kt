package com.planetnine.sessionless

import com.planetnine.sessionless.util.sessionless.impl.Sessionless
import com.planetnine.sessionless.util.sessionless.models.KeyAccessInfo
import com.planetnine.sessionless.util.sessionless.models.SimpleKeyPair
import com.planetnine.sessionless.util.sessionless.models.vaults.ICustomVault
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class SessionlessStressTests {

    private val sessionlessC = Sessionless.WithCustomVault(
        object : ICustomVault {
            override fun save(pair: SimpleKeyPair) {}
            override fun get() = SimpleKeyPair("", "")
        }
    )

    @DisplayName("Generate keys 1 time (with no storage)")
    @Test
    fun generateKeysNoStorageTimes1() {
        sessionlessC.generateKeys()
    }

    @DisplayName("Generate keys 10000 times (with no storage)")
    @Test
    fun generateKeysNoStorageTimes10000() {
        for (i in 1..10000) sessionlessC.generateKeys()
    }

    @DisplayName("Generate keys 10000 times (recursive) (with no storage)")
    @Test
    fun generateKeysNoStorageTimes10000Recursive() {
        (1..10000).forEach { _ -> sessionlessC.generateKeys() }
    }

    private val sessionless = Common.sessionlessKS
    private val access = KeyAccessInfo("test")

    @DisplayName("Generate keys 1 time (+ store into KeyStore)")
    @Test
    fun generateKeysTimes1() {
        sessionless.generateKeys(access)
    }

    @DisplayName("Generate keys 10000 times (+ store into KeyStore)")
    @Test
    fun generateKeysTimes10000() {
        for (i in 1..10000) sessionless.generateKeys(access)
    }

    @DisplayName("Generate keys 10000 times (recursive) (+ store into KeyStore)")
    @Test
    fun generateKeysTimes10000Recursive() {
        (1..10000).forEach { _ -> sessionless.generateKeys(access) }
    }
}