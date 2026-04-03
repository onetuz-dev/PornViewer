package com.plovdev.pornviewer.encryptsupport;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoEngine {
    public static final String ALGORITHM = "ChaCha20-Poly1305/None/NoPadding";
    private int mode;
    private byte[] baseNonce;

    private final SecretKeySpec keySpec;

    public CryptoEngine(int mode, byte[] password, byte[] baseNonce) {
        try {
            this.mode = mode;
            this.baseNonce = baseNonce;
            keySpec = CipherEngineUtils.createSecretKeySpecFromPassword(password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public byte[] getBaseNonce() {
        return baseNonce;
    }

    public void setBaseNonce(byte[] baseNonce) {
        this.baseNonce = baseNonce;
    }

    public byte[] processChunk(int counter, byte[] block) {
        try {
            IvParameterSpec parameterSpec = CipherEngineUtils.createParameterSpecFromBaseNonce(counter, baseNonce);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(mode, keySpec, parameterSpec);


            return cipher.doFinal(block);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] processData(byte[] data, byte[] nonce) {
        try {
            IvParameterSpec parameterSpec = CipherEngineUtils.createParameterSpecFromNonce(nonce);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(mode, keySpec, parameterSpec);

            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}