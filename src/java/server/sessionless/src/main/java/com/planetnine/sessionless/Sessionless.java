package com.planetnine.sessionless;
import java.lang.reflect.Method;

public interface Sessionless {
    void generateKeys(Method setKeys, Method getKeys);

    String sign(String message);

    Boolean verifySignature(String signature, String message, String publicKey);

    String generateUUID(); 
};
