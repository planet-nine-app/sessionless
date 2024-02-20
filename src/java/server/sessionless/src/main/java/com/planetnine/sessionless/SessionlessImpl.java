package com.planetnine.sessionless;
import java.lang.reflect.Method;

public class SessionlessImpl implements Sessionless {
    public SessionlessImpl() {

    }

    @Override
    public void generateKeys(Method setKeys, Method getKeys) {

    };

    @Override
    public String sign(String message) {
        return "foo";
    };

    @Override
    public Boolean verifySignature(String signature, String message, String publicKey) {
        return true;
    };

    @Override
    public String generateUUID() {
         return "uuid";
    };
};
