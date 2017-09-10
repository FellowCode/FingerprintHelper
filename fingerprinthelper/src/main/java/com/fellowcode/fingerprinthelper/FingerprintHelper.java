package com.fellowcode.fingerprinthelper;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.os.CountDownTimer;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.widget.ImageView;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Created by sergo on 09.09.2017.
 */


@TargetApi(23)
public class FingerprintHelper {

    private Context context;

    private FingerprintHandler fph;
    private static final String KEY_NAME = "SwA";
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private FingerprintManager.CryptoObject cryptoObject;

    private FingerprintManager fingerprintManager;

    FingerprintMethods fingerprintMethods;

    public FingerprintHelper(Context context, FingerprintMethods fingerprintMethods){
        this.context = context;
        this.fingerprintMethods = fingerprintMethods;
        fph = new FingerprintHandler(fingerprintMethods);

    }

    public void setImage(long millisUpdateImg,  ImageView fingerPrintImg, int BaseStateResource, int SucceedStateResource, int FailedStateResource){
        fph.setImage(millisUpdateImg, fingerPrintImg, BaseStateResource, SucceedStateResource, FailedStateResource);
    }

    public void prepareSensor()
    {
        if (checkFinger()) {
            // We are ready to set up the cipher and the key
            try {
                generateKey();
                Cipher cipher = generateCipher();
                cryptoObject =
                        new FingerprintManager.CryptoObject(cipher);

            }
            catch(FingerprintException fpe) {
                // Handle exception
            }
            fph.doAuth(fingerprintManager, cryptoObject);
        }
    }
    public void stopSensor(){
        if (fph!=null)
            fph.Cancel();
    }
    public boolean checkFinger() {

        // Keyguard Manager
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);

        // Fingerprint Manager
        fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);

        try {
            // Check if the fingerprint sensor is present
            if (!fingerprintManager.isHardwareDetected()) {
                return false;
            }

            if (!fingerprintManager.hasEnrolledFingerprints()) {
                return false;
            }

            if (!keyguardManager.isKeyguardSecure()) {
                return false;
            }

        }
        catch(SecurityException se) {
            se.printStackTrace();
        }


        return true;

    }

    private void generateKey() throws FingerprintException {
        try {
            // Get the reference to the key store
            keyStore = KeyStore.getInstance("AndroidKeyStore");

            // Key generator to generate the key
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            keyStore.load(null);
            keyGenerator.init( new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            keyGenerator.generateKey();

        }
        catch(KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }


    }

    private Cipher generateCipher() throws FingerprintException {
        try {
            Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher;
        }
        catch (NoSuchAlgorithmException
                | NoSuchPaddingException
                | InvalidKeyException
                | UnrecoverableKeyException
                | KeyStoreException exc) {
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }
    }

    private class FingerprintException extends Exception {

        public FingerprintException(Exception e) {
            super(e);
        }
    }


    private class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

        private ImageView fingerView;
        private CancellationSignal signal;
        private boolean cancel = false;
        private long millisUpdateImg;
        int BaseStateResource, SucceedStateResource, FailedStateResource;

        private FingerprintMethods fingerprintMethods = new FingerprintMethods() {
            @Override
            public void onAuthenticationSucceeded() {

            }

            @Override
            public void onAuthenticationError() {

            }

            @Override
            public void onAuthenticationFailed() {

            }
        };


        public FingerprintHandler(FingerprintMethods fingerprintMethods) {
            this.fingerprintMethods = fingerprintMethods;
        }


        public void setImage(long millisUpdateImg, ImageView fingerView, int BaseStateResource, int SucceedStateResource, int FailedStateResource) {
            this.millisUpdateImg = millisUpdateImg;
            this.fingerView = fingerView;
            this.BaseStateResource = BaseStateResource;
            this.SucceedStateResource = SucceedStateResource;
            this.FailedStateResource = FailedStateResource;
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            if (!cancel) {
                if (fingerView != null)
                    fingerView.setVisibility(ImageView.INVISIBLE);
                fingerprintMethods.onAuthenticationError();
            }
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            super.onAuthenticationHelp(helpCode, helpString);

        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            if (fingerView != null)
                fingerView.setImageResource(SucceedStateResource);
            fingerprintMethods.onAuthenticationSucceeded();
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            if (fingerView != null) {
                fingerView.setImageResource(FailedStateResource);
                fingViewRepair();
            }
            fingerprintMethods.onAuthenticationFailed();
        }

        public void doAuth(FingerprintManager manager, FingerprintManager.CryptoObject obj) {
            signal = new CancellationSignal();

            try {
                manager.authenticate(obj, signal, 0, this, null);
                if (fingerView != null) {
                    fingerView.setImageResource(BaseStateResource);
                    fingerView.setVisibility(ImageView.VISIBLE);
                }
            } catch (SecurityException sce) {
            }
        }

        public void Cancel() {
            cancel = true;
            if (fingerView != null)
                fingerView.setVisibility(ImageView.INVISIBLE);
            if (signal != null) {
                signal.cancel();
            }
        }

        private void fingViewRepair() {
            new CountDownTimer(millisUpdateImg, millisUpdateImg) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    fingerView.setImageResource(BaseStateResource);
                }
            }.start();
        }
    }
}
