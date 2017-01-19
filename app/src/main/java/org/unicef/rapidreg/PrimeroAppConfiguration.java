package org.unicef.rapidreg;

import android.provider.Settings;

import org.unicef.rapidreg.model.User;

import java.util.Locale;

public class PrimeroAppConfiguration {
    //    public static final String API_BASE_URL = "http://10.29.3.184:3000";
    private static String apiBaseUrl = "https://10.29.3.184:8443";

    private static String cookie = null;

    private static String internalFilePath = null;

    private static User currentUser;

    public static String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public static void setApiBaseUrl(String apiBaseUrl) {
        PrimeroAppConfiguration.apiBaseUrl = apiBaseUrl;
    }

    public static String getCookie() {
        return cookie;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void setCookie(String cookie) {
        PrimeroAppConfiguration.cookie = cookie;
    }


    public static String getInternalFilePath() {
        return internalFilePath;
    }

    public static void setInternalFilePath(String internalFilePath) {
        PrimeroAppConfiguration.internalFilePath = internalFilePath;
    }

    public static String getDefaultLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public static String getAndroidId() {
        return Settings.Secure.getString(PrimeroApplication.getAppContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }
}