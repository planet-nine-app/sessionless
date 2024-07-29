package com.planetnine.sessionless.util

import com.planetnine.sessionless.impl.KeyPairHex
import com.planetnine.sessionless.impl.exceptions.HexFormatRequiredException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bouncycastle.asn1.x509.X509Name
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.crypto.params.ECPrivateKeyParameters
import org.bouncycastle.crypto.params.ECPublicKeyParameters
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec
import org.bouncycastle.jce.spec.ECParameterSpec
import org.bouncycastle.jce.spec.ECPrivateKeySpec
import org.bouncycastle.x509.X509V3CertificateGenerator
import java.math.BigInteger
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.PublicKey
import java.security.SecureRandom
import java.security.Security
import java.security.Signature
import java.security.SignatureException
import java.security.cert.X509Certificate
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.util.Date
import kotlin.coroutines.CoroutineContext
import kotlin.math.max

object KeyUtils {
    object Defaults {
        const val KEY_ALGORITHM = "ECDSA"
        const val KEY_PROVIDER = "BC"
        const val KEY_SPEC_NAME = "secp256k1"
        const val SIGNATURE_ALGORITHM = "SHA256withECDSA"
        const val CERTIFICATE_TYPE = "X.509"
        const val KEY_SIZE = 256
        const val CERTIFICATE_ISSUER = "CN=Sessionless"
        const val CERTIFICATE_SUBJECT = "CN=Sessionless"
        const val CERTIFICATE_VALIDITY_MS = 1000L * 60 * 60 * 24 * 365

        val certificateSerialNumberNow
            get() = BigInteger.valueOf(System.currentTimeMillis())

        val keyFactory: KeyFactory
            get() = KeyFactory.getInstance(KEY_ALGORITHM)
        val parameterSpec: ECNamedCurveParameterSpec
            get() = ECNamedCurveTable.getParameterSpec(KEY_SPEC_NAME)
        val signature: Signature
            get() = Signature.getInstance(SIGNATURE_ALGORITHM)

        val keyPairGenerator: KeyPairGenerator
            get() = KeyPairGenerator.getInstance(KEY_ALGORITHM, KEY_PROVIDER)
    }

    val ECNamedCurveParameterSpec.domainParameters
        get() = ECDomainParameters(this.curve, this.g, this.n, this.h)

    //! Useful but unused ...
    // fun PrivateKey.generatePrivateKeySpec(
    //     parameterSpec: ECParameterSpec = Defaults.parameterSpec
    // ): ECPrivateKeySpec {
    //     return ECPrivateKeySpec((this as ECPrivateKey).s, parameterSpec)
    // }

    @Throws(
        java.security.cert.CertificateEncodingException::class,
        IllegalStateException::class,
        RuntimeException::class
    )
    @Suppress("DEPRECATION") //? this works... but it's deprecated.. I didn't find another way
    fun generateSelfSignedCertificate(pair: KeyPair): X509Certificate {
        //? This shall add the provider once even if called twice
        Security.addProvider(BouncyCastleProvider())
        val startDate = Date()
        val endDate = Date().apply { time += Defaults.CERTIFICATE_VALIDITY_MS }
        try {
            val generator = X509V3CertificateGenerator().apply {
                setSerialNumber(Defaults.certificateSerialNumberNow)
                setIssuerDN(X509Name(Defaults.CERTIFICATE_ISSUER))
                setNotBefore(startDate)
                setNotAfter(endDate)
                setSubjectDN(X509Name(Defaults.CERTIFICATE_SUBJECT))
                setPublicKey(pair.public)
                setSignatureAlgorithm(Defaults.SIGNATURE_ALGORITHM)
            }
            val certificate = generator.generate(pair.private, SecureRandom()).apply {
                checkValidity(startDate)
                verify(this.publicKey)
            }
            return certificate
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: SignatureException) {
            throw RuntimeException(e)
        } catch (e: InvalidKeyException) {
            throw RuntimeException(e)
        }
    }

    /** Generate a new [KeyPair] based on the defaults defined in [KeyUtils]
     * @return public/private [KeyPair]
     * @see generateKeyPairAsync */
    fun generateKeyPair(): KeyPair {
        //? This shall add the provider once even if called twice
        Security.addProvider(BouncyCastleProvider())
        val spec = Defaults.parameterSpec
        val generator: KeyPairGenerator
        try {
            generator = Defaults.keyPairGenerator
            generator.initialize(spec, SecureRandom())
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: NoSuchProviderException) {
            throw RuntimeException(e)
        } catch (e: InvalidAlgorithmParameterException) {
            throw RuntimeException(e)
        }
        return generator.generateKeyPair()
    }

    /** Generate a new [KeyPair] asynchronously based on the defaults defined in [KeyUtils]
     * @return public/private [KeyPair]
     * @see generateKeyPair */
    suspend fun generateKeyPairAsync(context: CoroutineContext = Dispatchers.IO): KeyPair {
        return withContext(context) { generateKeyPair() }
    }

    /** Check if a [String] is comprised of only even hex characters
     * - Length must be even
     * - Allowed characters: 0-9 a-f A-F */
    fun String.isBytes(): Boolean {
        if (this.length % 2 != 0) return false
        return Regex("^[0-9a-fA-F]+$").matches(this)
    }

    /** Convert [KeyPair] to [ECPublicKey]/[ECPrivateKey] hex [String]s
     * @see ECPrivateKey.toHex
     * @see ECPublicKey.toHex */
    fun KeyPair.toECHex(): KeyPairHex {
        val ecPrivateKey = this.private as ECPrivateKey
        val ecPublicKey = this.public as ECPublicKey
        val ecPrivateKeyHex = ecPrivateKey.toHex()
        val ecPublicKeyHex = ecPublicKey.toHex()
        return KeyPairHex(ecPrivateKeyHex, ecPublicKeyHex)
    }

    fun String.toECPrivateKey(paramSpec: ECParameterSpec = Defaults.parameterSpec): ECPrivateKey {
        val privateKeyBigInt = BigInteger(this, 16)
        val privateKeySpec = ECPrivateKeySpec(privateKeyBigInt, paramSpec)
        return Defaults.keyFactory.generatePrivate(privateKeySpec) as ECPrivateKey
    }

    /** Convert [ECPrivateKey] to hex [String]
     * - [ECPrivateKey.getS] in hex */
    fun ECPrivateKey.toHex(): String {
        return this.s.toString(16)
    }

    fun String.toECPrivateKeyParameters(): ECPrivateKeyParameters {
        if (!this.isBytes()) throw HexFormatRequiredException("(this)")
        val privateInt = BigInteger(this, 16)
        return ECPrivateKeyParameters(
            privateInt,
            Defaults.parameterSpec.domainParameters
        )
    }

    fun String.toECPublicKeyParameters(): ECPublicKeyParameters {
        if (!this.isBytes()) throw HexFormatRequiredException("(this)")
        val publicInt = BigInteger(this, 16)
        val paramSpec = Defaults.parameterSpec
        val publicKeyPoint = paramSpec.curve.decodePoint(publicInt.toByteArray())
        return ECPublicKeyParameters(
            publicKeyPoint, paramSpec.domainParameters
        )
    }

    fun PublicKey.toECPublicParameters(): ECPublicKeyParameters {
        val ec = this as ECPublicKey
        val hex = ec.toHex()
        return hex.toECPublicKeyParameters()
    }

    /** Convert [ECPublicKey] hex [String] of length 66 chars
     * - compression prefix + left padding of 0s */
    fun ECPublicKey.toHex(): String {
        val ecPoint = this.w
        val rawXAbs = ecPoint.affineX.abs()
        val rawY = ecPoint.affineY

        // compressed/uncompressed point
        val isEvenY = rawY
            .mod(BigInteger("2"))
            .equals(BigInteger.ZERO)
        val prefix = if (isEvenY) "02" else "03"

        // absolute raw x to hex
        val publicHex = rawXAbs.toString(16)
        // fill hex to be 64 chars | 32 bytes | 256 bits
        val padLength = max(0, 64 - publicHex.length)
        val publicHexPadded = "0".repeat(padLength) + publicHex

        // with prefix: 66 chars
        return "$prefix$publicHexPadded"
    }

}
