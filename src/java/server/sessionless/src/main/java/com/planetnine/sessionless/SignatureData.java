package com.planetnine.sessionless;

public class SignatureData {
    public final String r;
    public final String s;
    private final int v = 0;

    public SignatureData(byte[] r, byte[] s) {
        this.r = SessionlessImpl.bytesToHex(r);
        this.s = SessionlessImpl.bytesToHex(s);
    }

};