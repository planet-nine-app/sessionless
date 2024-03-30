package app.planentnine.springsessionless.application.util;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPoint;
import java.util.UUID;

//TODO: upload artifact and reimport
public class Sessionless {
    
    public static String[] generateKeysAsHex() {
        KeyPairGenerator generator;
        Security.addProvider(new BouncyCastleProvider());
        ECNamedCurveParameterSpec ecNamedCurveParameterSpec =
                ECNamedCurveTable.getParameterSpec("secp256k1");
        
        try {
            generator = KeyPairGenerator.getInstance("ECDSA", "BC");
            generator.initialize(ecNamedCurveParameterSpec, new SecureRandom());
            
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
        
        
        KeyPair keyPair = generator.generateKeyPair();
        ECPrivateKey ecPrivateKey = (ECPrivateKey) keyPair.getPrivate();
        ECPublicKey ecPublicKey = (ECPublicKey) keyPair.getPublic();
        
        String privateKeyHex = extractPrivateKeyHex(ecPrivateKey);
        String publicKeyHex = extractPublicKeyHex(ecPublicKey);
        
        return new String[]{privateKeyHex, publicKeyHex};
    }
    
    public static String[] signMessage(String privateKey, String message) {
        ECNamedCurveParameterSpec ecNamedCurveParameterSpec =
                ECNamedCurveTable.getParameterSpec("secp256k1");
        ECDomainParameters domain =
                new ECDomainParameters(
                        ecNamedCurveParameterSpec.getCurve(),
                        ecNamedCurveParameterSpec.getG(),
                        ecNamedCurveParameterSpec.getN(),
                        ecNamedCurveParameterSpec.getH());
        
        BigInteger privateKeyFormatted = new BigInteger(privateKey, 16);
        
        ECPrivateKeyParameters privateKeyParameters = new ECPrivateKeyParameters(privateKeyFormatted, domain);
        ECDSASigner signer = new ECDSASigner();
        signer.init(true, privateKeyParameters);
        
        byte[] messageHash = keccakMessageHash(message);
        BigInteger[] signature = signer.generateSignature(messageHash);
        
        return new String[]{signature[0].toString(16), signature[1].toString(16)};
    }
    
    public static boolean verify(String publicKey, String[] signature, String message) {
        BigInteger publicKeyFormatted = new BigInteger(publicKey, 16);
        ECNamedCurveParameterSpec ecNamedCurveParameterSpec =
                ECNamedCurveTable.getParameterSpec("secp256k1");
        ECDomainParameters domain =
                new ECDomainParameters(
                        ecNamedCurveParameterSpec.getCurve(),
                        ecNamedCurveParameterSpec.getG(),
                        ecNamedCurveParameterSpec.getN(),
                        ecNamedCurveParameterSpec.getH());
        org.bouncycastle.math.ec.ECPoint publicKeyPoint = ecNamedCurveParameterSpec.getCurve().decodePoint(publicKeyFormatted.toByteArray());
        ECPublicKeyParameters publicKeyParameters = new ECPublicKeyParameters(publicKeyPoint, domain);
        
        ECDSASigner signer = new ECDSASigner();
        signer.init(false, publicKeyParameters);
        
        MessageDigest digest = new Keccak.Digest256();
        byte[] messageHash = digest.digest(message.getBytes());
        
        return signer.verifySignature(messageHash, new BigInteger(signature[0], 16), new BigInteger(signature[1], 16));
    }
    
    public static boolean associate(String primaryPublicKey, String[] primarySignature, String primaryMessage,
                                    String secondaryPublicKey, String[] secondarySignature, String secondaryMessage) {
        return verify(primaryPublicKey, primarySignature, primaryMessage)
                && verify(secondaryPublicKey, secondarySignature, secondaryMessage);
    }
    
    public static UUID generateUuid() {
        return UUID.randomUUID();
    }
    
    private static String extractPrivateKeyHex(ECPrivateKey ecPrivateKey) {
        BigInteger privateKeyBigInt = ecPrivateKey.getS();
        return privateKeyBigInt.toString(16);
    }
    
    private static String extractPublicKeyHex(ECPublicKey ecPublicKey) {
        
        ECPoint ecPoint = ecPublicKey.getW();
        BigInteger publicBytesRawX = ecPoint.getAffineX();
        BigInteger pubicBytesRawY = ecPoint.getAffineY();
        
        //Add compression prefix based on sign
        boolean yIsEven = pubicBytesRawY.mod(new BigInteger("2")).equals(BigInteger.ZERO);
        publicBytesRawX = publicBytesRawX.abs();
        String prefix = yIsEven ? "02" : "03";
        
        //Ensure stripped 0's are sign only
        String publicKeyHex = publicBytesRawX.toString(16);
        publicKeyHex = StringUtils.leftPad(publicKeyHex, 64, '0');
        publicKeyHex = prefix + publicKeyHex;
        
        return publicKeyHex;
    }
    
    private static byte[] keccakMessageHash(String message) {
        MessageDigest digest = new Keccak.Digest256();
        return digest.digest(message.getBytes());
    }
}
