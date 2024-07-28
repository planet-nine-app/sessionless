package com.planetnine.sessionless.controllers

import com.planetnine.sessionless.impl.KeyAccessInfo
import com.planetnine.sessionless.impl.MessageSignatureHex
import com.planetnine.sessionless.impl.Sessionless
import com.planetnine.sessionless.impl.SignedMessageWithKey
import com.planetnine.sessionless.models.vaults.IVault
import com.planetnine.sessionless.util.KeyUtils.toECHex
import com.planetnine.sessionless.util.KeyUtils.toECPrivateKey
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletRequest
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
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
        const val USERS_FILE_PATH = "users.json"

        private var users = JSONObject()

        private fun saveUser(uuid: String, pubKey: String) = synchronized(users) {
            val usersFile = File(USERS_FILE_PATH)
            val usersString: String
            if (!usersFile.exists()) {
                usersString = "{}"
                usersFile.createNewFile()
                usersFile.writeText(usersString)
            } else {
                usersString = usersFile.readText()
            }
            val json = JSONObject(usersString).apply {
                put(uuid, pubKey)
            }
            println(usersFile.absolutePath)
            users = json
            usersFile.writeText(json.toString())
        }

        private fun getUserPublicKey(uuid: String): String? {
            val usersFile = File(USERS_FILE_PATH)
            if (!usersFile.exists()) return null
            return try {
                synchronized(users) {
                    val usersString = usersFile.readText()
                    val json = JSONObject(usersString)
                    users = json
                    json.getString(uuid)
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
    private fun webSignature(req: HttpServletRequest, message: String): MessageSignatureHex? {
        val privateKeyString = req.session.getAttribute("user") as? String
            ?: return null
        currentPrivateKey = privateKeyString
        val privateKey = privateKeyString.toECPrivateKey()
        return sessionless.sign(message, privateKey)
    }

    data class RegisterResponse(
        val uuid: String? = null,
        val welcomeMessage: String? = null,
        val color: String = "purple",
        val error: String? = null
    )

    data class RegisterReqBody(
        val signature: MessageSignatureHex?, val pubKey: String?,
        val enteredText: String?, val timestamp: Long?,
    )

    private suspend fun handleWebRegistration(req: HttpServletRequest): ResponseEntity<RegisterResponse> {
        val uuid = sessionless.generateUUID()
        val accessInfo = KeyAccessInfo(uuid)
        val pair = sessionless.generateKeysAsync(accessInfo)
        val simple = pair.toECHex()
        req.session.setAttribute("user", simple.privateKey)
        saveUser(uuid, simple.publicKey)
        return ResponseEntity.ok(RegisterResponse(uuid, "Welcome to this example!"))
    }


    @PostMapping(
        "/register",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    suspend fun register(
        @RequestBody body: RegisterReqBody,
        req: HttpServletRequest
    ): ResponseEntity<RegisterResponse> {
        val signature = body.signature
            ?: return handleWebRegistration(req)
        val pubKey = body.pubKey ?: return ResponseEntity.badRequest()
            .body(RegisterResponse(error = "Missing pubKey"))
        val timestamp = body.timestamp ?: return ResponseEntity.badRequest()
            .body(RegisterResponse(error = "Missing timestamp"))
        val enteredText = body.enteredText ?: return ResponseEntity.badRequest()
            .body(RegisterResponse(error = "Missing enteredText"))

        //! the order is mandatory
        // JsonObject does not respect the entries' order
        // so here's the brute force method
        val message =
            "{\"pubKey\":\"$pubKey\",\"enteredText\":\"$enteredText\",\"timestamp\":\"$timestamp\"}"

        val verified = sessionless.verifySignature(SignedMessageWithKey(message, signature, pubKey))
        if (!verified) {
            println("======== Signature error")
            return ResponseEntity.status(401)
                .body(RegisterResponse(error = "Signature verification failed!"))
        }

        val uuid = sessionless.generateUUID()
        saveUser(uuid, pubKey)
        println("Registered user with UUID: $uuid")
        return ResponseEntity.ok(RegisterResponse(uuid, "Welcome to this example!"))
    }

    data class CoolStuffResponse(val doubleCool: String?, val error: String?)
    data class CoolStuffReqBody(
        val uuid: String, val signature: MessageSignatureHex? = null,
        val timestamp: Long? = null, val coolness: String? = null
    )

    @PostMapping("/cool-stuff")
    fun coolStuff(
        @RequestBody body: CoolStuffReqBody,
        req: HttpServletRequest
    ): ResponseEntity<CoolStuffResponse> {
        val pubKey = getUserPublicKey(body.uuid)
            ?: return ResponseEntity.status(404).body(CoolStuffResponse(null, "User not found"))

        val message =
            "{\"uuid\":\"${body.uuid}\",\"coolness\":\"${body.coolness}\",\"timestamp\":\"${body.timestamp}\"}"

        // from body, or from session
        // or error
        val signature =
            body.signature ?: webSignature(req, message)
            ?: return ResponseEntity.internalServerError()
                .body(CoolStuffResponse(null, "Signature error"))

        val verified = sessionless.verifySignature(SignedMessageWithKey(message, signature, pubKey))
        return if (verified)
            ResponseEntity.ok(CoolStuffResponse("double cool", null))
        else ResponseEntity.status(401).body(CoolStuffResponse(null, "Auth error"))
    }
}