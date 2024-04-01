package com.planetnine.sessionless.controllers

import com.planetnine.sessionless.util.sessionless.Sessionless
import com.planetnine.sessionless.util.sessionless.keys.KeyAccessInfo
import com.planetnine.sessionless.util.sessionless.keys.SimpleKeyPair
import com.planetnine.sessionless.util.sessionless.vaults.IVault
import jakarta.servlet.http.HttpServletRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.security.KeyFactory
import java.security.KeyStore
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64

@RestController
class SessionlessController {
    @Autowired
    private lateinit var keyStore: KeyStore

    private var sessionless = Sessionless.WithKeyStore(IVault.getDefault(keyStore))

    companion object {
        private const val USERS_FILE_PATH = "./users.json"

        private var users = JSONObject()

        private fun saveUser(userUUID: String, publicKey: String) = synchronized(users) {
            val usersFile = File(USERS_FILE_PATH)
            val usersString: String
            if (!usersFile.exists()) {
                usersString = "{}"
                usersFile.createNewFile()
                usersFile.writeText(usersString)
            } else {
                usersString = usersFile.readText()
            }
            val json = JSONObject(usersString)
            json.put(userUUID, publicKey)
            users = json
            usersFile.writeText(json.toString())
        }

        private fun getUserPublicKey(userUUID: String): String? {
            val usersFile = File(USERS_FILE_PATH)
            if (!usersFile.exists()) return null
            return try {
                synchronized(users) {
                    val usersString = usersFile.readText()
                    val json = JSONObject(usersString)
                    users = json
                    json.getString(userUUID)
                }
            } catch (_: Exception) {
                null
            }
        }
    }


    data class RegisterResponse(val uuid: String, val welcomeMessage: String)
    data class RegisterReqBody(
        val signature: String?, val publicKey: String?,
        val enteredText: String?, val timestamp: Long?,
    )

    private var currentPrivateKey = ""
    private fun webSignature(req: HttpServletRequest, message: String): String {
        val privateKeyString = req.session.getAttribute("user") as? String
        if (privateKeyString != null) currentPrivateKey = privateKeyString
        val privateKeyBytes = Base64.getDecoder().decode(currentPrivateKey)
        val privateKeySpec = PKCS8EncodedKeySpec(privateKeyBytes)
        val privateKey = KeyFactory.getInstance(Sessionless.KEY_ALGORITHM)
            .generatePrivate(privateKeySpec)
        return sessionless.sign(message, privateKey)
    }

    private suspend fun handleWebRegistration(req: HttpServletRequest): RegisterResponse {
        val userUUID = sessionless.generateUUID()
        val pair = sessionless.generateKeysAsync(KeyAccessInfo(userUUID))
        val simple = SimpleKeyPair.from(pair)
        req.session.setAttribute("user", simple.privateKey)
        saveUser(userUUID, simple.publicKey)
        return RegisterResponse(userUUID, "Welcome to this example!")
    }

    @PutMapping("/register")
    fun register(
        req: HttpServletRequest,
        @RequestBody body: RegisterReqBody
    ): RegisterResponse? {
        val signature = body.signature
            ?: return runBlocking(Dispatchers.IO) { handleWebRegistration(req) }
        val publicKey = body.publicKey ?: return null
        val timestamp = body.timestamp ?: return null
        val enteredText = body.enteredText ?: return null

        val message = JSONObject().apply {
            put("publicKey", publicKey)
            put("enteredText", enteredText)
            put("timestamp", timestamp)
        }.toString()

        val verified = sessionless.verifySignature(signature, message, publicKey)
        if (!verified) {
            println("Signature verification failed!")
            return null
        }

        val uuid = sessionless.generateUUID()
        saveUser(uuid, publicKey)
        println("Registered user with UUID: $uuid")
        return RegisterResponse(uuid, "Welcome to this example!")
    }

    data class CoolStuffResponse(val doubleCool: String?, val error: String?)
    data class CoolStuffReqBody(
        val userUUID: String, val signature: String?,
        val timestamp: Long, val coolness: String,
    )

    @PutMapping("/cool-stuff")
    fun coolStuff(req: HttpServletRequest, @RequestBody body: CoolStuffReqBody): CoolStuffResponse {
        val publicKey = getUserPublicKey(body.userUUID)
            ?: return CoolStuffResponse(null, "User not found")

        val message = JSONObject().apply {
            put("coolness", body.coolness)
            put("timestamp", body.timestamp)
        }.toString()

        val signature = body.signature ?: webSignature(req, message)

        val verified = sessionless.verifySignature(signature, message, publicKey)
        return if (verified)
            CoolStuffResponse("double cool", null)
        else CoolStuffResponse(null, "Auth error")
    }
}