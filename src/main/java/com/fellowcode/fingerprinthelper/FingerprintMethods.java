package com.fellowcode.fingerprinthelper;

/**
 * Created by sergo on 09.09.2017.
 */

public interface FingerprintMethods {
    void onAuthenticationSucceeded();
    void onAuthenticationError();
    void onAuthenticationFailed();
}
