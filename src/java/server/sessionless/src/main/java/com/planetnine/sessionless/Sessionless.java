package com.planetnine.sessionless;
import java.lang.reflect.Method;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public interface Sessionless {
    public Keys generateKeys(Method setKeys, Method getKeys) throws NoSuchProviderException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException;

    public SignatureData sign(String message);

    public Boolean verifySignature(SignatureData signature, String message, String publicKey);

    public String generateUUID(); 
};
