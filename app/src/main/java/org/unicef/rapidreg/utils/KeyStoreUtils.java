package org.unicef.rapidreg.utils;

import org.unicef.rapidreg.PrimeroApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Handles KeyStore operations: store and read
 */

public class KeyStoreUtils {
    public final static String KEY_STORE_FILE = "SecretKeyStore.bks";
    public final static String KEY_STORE_ALIAS = "secret_key";
    public final static String KEY_STORE_TYPE = "BKS";

    public static void storeKey(final String alias, final String key) {
        KeyStore keyStore = null;
        SecretKey secret = null;
        FileOutputStream fos = null;
        try {
            keyStore = KeyStore.getInstance(KeyStoreUtils.KEY_STORE_TYPE);
            keyStore.load(null, null);
            secret = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            keyStore.setKeyEntry(alias, secret, null, null);
            fos = new FileOutputStream(new File(PrimeroApplication.getAppContext().getFilesDir(), KeyStoreUtils.KEY_STORE_FILE));
            keyStore.store(fos, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String readKeyFromKeyStore(final String keyAlias) {
        KeyStore keyStore = null;
        SecretKey secret = null;
        String key = null;
        FileInputStream fis = null;
        try {
            keyStore = KeyStore.getInstance(KeyStoreUtils.KEY_STORE_TYPE);
            fis = new FileInputStream(new File(PrimeroApplication.getAppContext().getFilesDir(), KeyStoreUtils.KEY_STORE_FILE));
            keyStore.load(fis, null);
            KeyStore.SecretKeyEntry keyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(keyAlias, null);
            secret = keyEntry.getSecretKey();
            key = new String(secret.getEncoded(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return key;
    }
}
