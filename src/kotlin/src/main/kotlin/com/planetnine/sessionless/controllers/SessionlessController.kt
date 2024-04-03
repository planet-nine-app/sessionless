package com.planetnine.sessionless.controllers

import com.planetnine.sessionless.util.sessionless.impl.Sessionless
import com.planetnine.sessionless.util.sessionless.models.IdentifiableMessage
import com.planetnine.sessionless.util.sessionless.models.KeyAccessInfo
import com.planetnine.sessionless.util.sessionless.models.MessageSignature
import com.planetnine.sessionless.util.sessionless.models.vaults.IVault
import com.planetnine.sessionless.util.sessionless.util.KeyUtils.toECHex
import com.planetnine.sessionless.util.sessionless.util.KeyUtils.toECPrivateKey
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.security.KeyStore

@RestController
class SessionlessController {
    @Autowired
    private lateinit var keyStore: KeyStore

    private lateinit var sessionless: Sessionless.WithKeyStore

    @PostConstruct
    fun init() {
        sessionless = Sessionless.WithKeyStore(IVault.getDefault(keyStore))

        val users = File(USERS_FILE_PATH)
        if (users.exists()) users.delete()
    }

    companion object {
        const val USERS_FILE_PATH = "./users.json"

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

    @GetMapping("/")
    fun index(req: HttpServletRequest): String {
        return "Hello, World!"
    }


    private var currentPrivateKey = ""
    private fun webSignature(req: HttpServletRequest, message: String): MessageSignature? {
        val privateKeyString = req.session.getAttribute("user") as? String
            ?: return null
        currentPrivateKey = privateKeyString
        val privateKey = privateKeyString.toECPrivateKey()
        return sessionless.sign(message, privateKey)
    }

    private suspend fun handleWebRegistration(req: HttpServletRequest): RegisterResponse {
        val userUUID = sessionless.generateUUID()
        val pair = sessionless.generateKeysAsync(KeyAccessInfo(userUUID))
        val simple = pair.toECHex()
        req.session.setAttribute("user", simple.privateKey)
        saveUser(userUUID, simple.publicKey)
        return RegisterResponse(userUUID, "Welcome to this example!")
    }

    data class RegisterResponse(val uuid: String, val welcomeMessage: String)
    data class RegisterReqBody(
        val signature: MessageSignature?, val publicKey: String?,
        val enteredText: String?, val timestamp: Long?,
    )

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

        val verified = sessionless.verify(message, publicKey, signature)
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
        val userUUID: String, val signature: MessageSignature?,
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

        // from body, or from session
        // or error
        val signature =
            body.signature ?: webSignature(req, message)
            ?: return CoolStuffResponse(null, "Signature error")
 
        val verified = sessionless.verify(message, publicKey, signature)
        return if (verified)
            CoolStuffResponse("double cool", null)
        else CoolStuffResponse(null, "Auth error")
    }
}