package com.planetnine.sessionless;

import java.math.BigInteger;

public class ECKeyPair {
    public final BigInteger privateKey;
    public final BigInteger publicKey;

    public ECKeyPair(BigInteger privateKey, BigInteger publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }
};