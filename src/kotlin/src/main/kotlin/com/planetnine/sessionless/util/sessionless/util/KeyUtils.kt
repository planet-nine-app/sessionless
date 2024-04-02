package com.planetnine.sessionless.util.sessionless.util

import com.planetnine.sessionless.util.sessionless.models.SimpleKeyPair
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec
import org.bouncycastle.jce.spec.ECParameterSpec
import org.bouncycastle.jce.spec.ECPrivateKeySpec
import java.io.ByteArrayInputStream
import java.math.BigInteger
import java.security.InvalidAlgorithmParameterException
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.Security
import java.security.Signature
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import kotlin.coroutines.CoroutineContext
import kotlin.math.max

object KeyUtils {
    object Defaults {
        const val KEY_ALGORITHM = "ECDSA"
        const val KEY_PROVIDER = "BC"
        const val KEY_SPEC_NAME = "secp256k1"
        const val SIGNATURE_ALGORITHM = "SHA256withECDSA"
        const val CERTIFICATE_TYPE = "X.509"

        val keyFactory: KeyFactory
            get() = KeyFactory.getInstance(KEY_ALGORITHM)
        val parameterSpec: ECNamedCurveParameterSpec
            get() = ECNamedCurveTable.getParameterSpec(KEY_SPEC_NAME)
        val certificateFactory: CertificateFactory
            get() = CertificateFactory.getInstance(CERTIFICATE_TYPE, KEY_PROVIDER)
        val signature: Signature
            get() = Signature.getInstance(SIGNATURE_ALGORITHM)

        val keyPairGenerator: KeyPairGenerator
            get() = KeyPairGenerator.getInstance(KEY_ALGORITHM, KEY_PROVIDER)
    }

    val ECNamedCurveParameterSpec.domainParameters
        get() = ECDomainParameters(this.curve, this.g, this.n, this.h)

    fun PrivateKey.generatePrivateKeySpec(
        parameterSpec: ECParameterSpec = Defaults.parameterSpec
    ): ECPrivateKeySpec {
        return ECPrivateKeySpec((this as ECPrivateKey).s, parameterSpec)
    }

    fun generateCertificate(
        publicKey: PublicKey,
        certificateFactory: CertificateFactory? = null
    ): Certificate {
        //? This shall add the provider once even if called twice
        Security.addProvider(BouncyCastleProvider())
        //? not using default param to avoid getting the factory before adding BC provider
        val factory = certificateFactory ?: Defaults.certificateFactory
        return factory.generateCertificate(
            ByteArrayInputStream(publicKey.encoded)
        )
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


    /** Convert [KeyPair] to [ECPublicKey]/[ECPrivateKey] hex [String]s
     * @see ECPrivateKey.toHex
     * @see ECPublicKey.toHex */
    fun KeyPair.toECHex(): SimpleKeyPair {
        val ecPrivateKey = this.private as ECPrivateKey
        val ecPublicKey = this.public as ECPublicKey
        val ecPrivateKeyHex = ecPrivateKey.toHex()
        val ecPublicKeyHex = ecPublicKey.toHex()
        return SimpleKeyPair(ecPrivateKeyHex, ecPublicKeyHex)
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
