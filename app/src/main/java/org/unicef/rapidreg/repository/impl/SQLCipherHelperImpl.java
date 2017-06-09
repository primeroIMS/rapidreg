package org.unicef.rapidreg.repository.impl;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.structure.database.DatabaseHelperListener;
import com.raizlabs.dbflow.android.sqlcipher.SQLCipherOpenHelper;

import org.unicef.rapidreg.BuildConfig;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.utils.EncryptHelper;

import org.unicef.rapidreg.utils.KeyStoreUtils;

public class SQLCipherHelperImpl extends SQLCipherOpenHelper {

    public SQLCipherHelperImpl(DatabaseDefinition definition, DatabaseHelperListener listener) {
        super(definition, listener);
    }

    @Override
    protected String getCipherSecret() {
        String secret = "";
        if (BuildConfig.DEBUG) {
            secret = "primero";
        } else {
            //TODO: this is basic Keystore handling security needs to be enforced by the use of PIN/PASSWORD/PATTERN
            secret = KeyStoreUtils.readKeyFromKeyStore(KeyStoreUtils.KEY_STORE_ALIAS);
            if (secret == null) {
                secret = generateEncryptionKey();
                KeyStoreUtils.storeKey(KeyStoreUtils.KEY_STORE_ALIAS, secret);
            }
        }
        return secret;
    }

    private String generateEncryptionKey() {
        Context ctx = PrimeroApplication.getAppContext();
        String androidId = Settings.Secure.getString(ctx.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();

        String buildInfo = Build.BOARD + Build.BRAND + Build.DEVICE + Build.DISPLAY
                + Build.FINGERPRINT + Build.HOST + Build.ID + Build.MANUFACTURER + Build.MODEL
                + Build.PRODUCT + Build.TAGS + Build.TYPE + Build.USER;

        return EncryptHelper.encrypt(buildInfo + deviceId + androidId);
    }

}
