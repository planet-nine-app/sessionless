package com.planetnine.sessionless;
import java.lang.reflect.Method;

public interface Sessionless {
    public void generateKeys(Method setKeys, Method getKeys);

    public String sign(String message);

    public Boolean verifySignature(String signature, String message, String publicKey);

    public String generateUUID(); 
};
