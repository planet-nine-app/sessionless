package com.planetnine.sessionless;
import java.lang.reflect.Method;

import java.math.BigInteger;

import java.nio.charset.StandardCharsets;

import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.math.ec.custom.sec.SecP256K1Curve;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.math.ec.custom.sec.SecP256K1Curve;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;


public class SessionlessImpl implements Sessionless {

    public static final X9ECParameters CURVE_PARAMS = CustomNamedCurves.getByName("secp256k1");
    public static final int CHAIN_ID_INC = 35;
    public static final int LOWER_REAL_V = 27;
    // The v signature parameter starts at 37 because 1 is the first valid chainId so:
    // chainId >= 1 implies that 2 * chainId + CHAIN_ID_INC >= 37.
    // https://eips.ethereum.org/EIPS/eip-155
    public static final int REPLAY_PROTECTED_V_MIN = 37;
    static final ECDomainParameters CURVE =
            new ECDomainParameters(
                    CURVE_PARAMS.getCurve(),
                    CURVE_PARAMS.getG(),
                    CURVE_PARAMS.getN(),
                    CURVE_PARAMS.getH());
    static final BigInteger HALF_CURVE_ORDER = CURVE_PARAMS.getN().shiftRight(1);


    public SessionlessImpl() {

    }

    @Override
    public Keys generateKeys(Method setKeys, Method getKeys) throws NoSuchProviderException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException {
        SecureRandom random = new SecureRandom();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
        ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("secp256k1");
        keyPairGenerator.initialize(ecGenParameterSpec, random);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        Keys keys = new Keys(keyPair.getPrivate().getEncoded(), keyPair.getPublic().getEncoded());
        return keys;
    };

    @Override
    public SignatureData sign(String message) {
        ECKeyPair keyPair = new ECKeyPair(new BigInteger("foo"), new BigInteger("bar"));
        //SignatureData sig = signMessage(message, keyPair, true);
        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));

        ECPrivateKeyParameters privateKey = new ECPrivateKeyParameters(keyPair.privateKey, CURVE);
        signer.init(true, privateKey);

        Keccak.DigestKeccak keccak = new Keccak.Digest256();
        keccak.update(message.getBytes(StandardCharsets.UTF_8), 0, 32);
        byte[] messageHash = keccak.digest();

        BigInteger[] components = signer.generateSignature(messageHash);

        ECDSASignature ecdsaSignature = new ECDSASignature(components[0], components[1]).toCanonicalised();
        SignatureData sig = new SignatureData(ecdsaSignature.r.toByteArray(), ecdsaSignature.s.toByteArray());
        return sig;
    };

    @Override
    public Boolean verifySignature(SignatureData signature, String message, String publicKeyString) {
        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));

        ECPublicKeyParameters publicKey = new ECPublicKeyParameters(CURVE.getCurve().decodePoint(publicKeyString.getBytes(StandardCharsets.UTF_8)), CURVE);
        signer.init(false, publicKey);

        Keccak.DigestKeccak keccak = new Keccak.Digest256();
        keccak.update(message.getBytes(StandardCharsets.UTF_8), 0, 32);
        byte[] messageHash = keccak.digest();

        return signer.verifySignature(messageHash, new BigInteger(signature.r), new BigInteger(signature.s));
    };

    @Override
    public String generateUUID() {
         return "uuid";
    };

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
};




