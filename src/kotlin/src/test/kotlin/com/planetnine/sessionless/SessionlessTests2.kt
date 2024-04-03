package com.planetnine.sessionless

import com.planetnine.sessionless.util.sessionless.impl.Sessionless
import com.planetnine.sessionless.util.sessionless.models.KeyAccessInfo
import com.planetnine.sessionless.util.sessionless.models.SimpleKeyPair
import com.planetnine.sessionless.util.sessionless.models.vaults.ICustomVault
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SessionlessTests2 {

    private val sessionlessC = Sessionless.WithCustomVault(
        object : ICustomVault {
            override fun save(pair: SimpleKeyPair) {}
            override fun get() = SimpleKeyPair("", "")
        }
    )

    @Test
    fun generateKeysNoStorageTimes1000() {
        for (i in 1..1000) sessionlessC.generateKeys()
    }

    @Test
    fun generateKeysNoStorageTimes1000Recursive() {
        (1..1000).forEach { _ -> sessionlessC.generateKeys() }
    }

    private val sessionless = Common.sessionlessKS
    private val access = KeyAccessInfo("test")

    @Test
    fun generateKeysTimes1000() {
        for (i in 1..1000) sessionless.generateKeys(access)
    }

    @Test
    fun generateKeysTimes1000Recursive() {
        (1..1000).forEach { _ -> sessionless.generateKeys(access) }
    }
}