package com.planetnine.sessionless;

public class Keys {
    public String publicKey;
    public final String privateKey;

    public Keys(byte[] privateKey, byte[] publicKey) {
        this.privateKey = SessionlessImpl.bytesToHex(privateKey);
        this.publicKey = SessionlessImpl.bytesToHex(publicKey);
    }
};
