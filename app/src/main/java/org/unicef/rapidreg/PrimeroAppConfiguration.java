package org.unicef.rapidreg;

import android.provider.Settings;

import org.unicef.rapidreg.model.SystemSettings;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.utils.TextUtils;

import java.util.HashMap;
import java.util.Map;

public class PrimeroAppConfiguration {
    public static final String MODULE_ID_GBV = "primeromodule-gbv";
    public static final int DEFAULT_DISTRICT_LEVEL = 1;

    public static final String MODULE_ID_CP = "primeromodule-cp";

    public static final String PARENT_CASE = "case";
    public static final String PARENT_INCIDENT = "incident";
    public static final String PARENT_TRACING_REQUEST = "tracing_request";

    private static final Map<String, String> localeDict = initLocaleDict();

    private static String apiBaseUrl = "https://127.0.0.1:8443";

    private static String cookie = null;

    private static String internalFilePath = null;

    private static User currentUser;

    private static SystemSettings currentSystemSettings;

    private static String currentLanguage = "en";

    private static int timeout = 90;

    public static int getTimeout() {return timeout;}

    public static String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public static void setApiBaseUrl(String apiBaseUrl) {
        apiBaseUrl = TextUtils.lintUrl(apiBaseUrl);
        PrimeroAppConfiguration.apiBaseUrl = apiBaseUrl;
    }

    public static SystemSettings getCurrentSystemSettings() {
        return currentSystemSettings;
    }

    public static void setCurrentSystemSettings(SystemSettings currentSystemSettings) {
        PrimeroAppConfiguration.currentSystemSettings = currentSystemSettings;
    }

    public static String getCookie() {
        return cookie;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static String getCurrentUsername() {
        return currentUser.getUsername();
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

    public static void setDefaultLanguage(String language) {
        PrimeroAppConfiguration.currentLanguage = convertLocale(language);
    }

    public static String getDefaultLanguage() {
        return currentLanguage;
    }

    public static String getDatabaseName() {
        return "primero";
    }

    public static String getAndroidId() {
        return Settings.Secure.getString(PrimeroApplication.getAppContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    private static String convertLocale(String locale) {
        String value = localeDict.get(locale);
        return value != null ? value : locale;
    }

    public static String getServerLocale() {
        for (String o : localeDict.keySet()) {
            if (localeDict.get(o).equals(getDefaultLanguage())) {
                return o;
            }
        }
        return getDefaultLanguage();
    }

    private static Map<String, String> initLocaleDict() {
        Map<String, String> locales = new HashMap<>();
        locales.put("id", "in_ID");
        locales.put("bn", "bn_BD");
        locales.put("pt", "pt_MZ");
        return locales;
    }
}

