package com.planetnine.sessionless

import com.planetnine.sessionless.impl.KeyPairHex
import com.planetnine.sessionless.impl.Sessionless
import com.planetnine.sessionless.models.vaults.ICustomVault
import org.junit.jupiter.api.Test
import java.io.File

class IntegrityTests {
    private val sessionless = Sessionless.WithCustomVault(Vault)

    @Test
    fun a() {

    }
}

private object Vault : ICustomVault {
    override fun save(pair: KeyPairHex) {
        privateKeyFile.writeText(pair.privateKey)
        publicKeyFile.writeText(pair.publicKey)
    }

    override fun get(): KeyPairHex? {
        val pri = privateKeyFile
        val pub = publicKeyFile
        if (!pri.exists() || !pub.exists()) {
            clear()
            return null
        }
        return KeyPairHex(pri.readText(), pub.readText())
    }

    fun clear() {
        privateKeyFile.delete()
        publicKeyFile.delete()
    }

    const val privateKeyPath = "./private.key"
    const val publicKeyPath = "./public.key"
    val privateKeyFile get() = File(privateKeyPath)
    val publicKeyFile get() = File(publicKeyPath)
}