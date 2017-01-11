package com.pubnub_fcm_example.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.pubnub_fcm_example.util.Constant;


public class SharedPreferenceManager {
    private static final String TAG = SharedPreferenceManager.class.getName();
    private static final SharedPreferenceManager ourInstance = new SharedPreferenceManager();
    private SharedPreferences sharedPreferences;

    public static SharedPreferenceManager getInstance() {
        return ourInstance;
    }

    private SharedPreferenceManager() {
    }

    public void init(Context context) {
        sharedPreferences = context.getSharedPreferences(Constant.ME, Context.MODE_PRIVATE);
    }

    public void remove(String key) {
        (sharedPreferences.edit()).remove(key).apply();
    }

    public void putString(String key, String value) {
        (sharedPreferences.edit()).putString(key, value).apply();
    }

    public String getString(String key, String defaultVal) {
        return sharedPreferences.getString(key, defaultVal);
    }

    public void putBoolean(String key, boolean value) {
        (sharedPreferences.edit()).putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defaultVal) {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(key, defaultVal);
        } else {
            return defaultVal;
        }
    }

    public void putInt(String key, int value) {
        (sharedPreferences.edit()).putInt(key, value).apply();
    }

    public int getInt(String key, int defaultVal) {
        return sharedPreferences.getInt(key, defaultVal);
    }

    public void putLong(String key, long value) {
        (sharedPreferences.edit()).putLong(key, value).apply();
    }

    public String getAccountId() {
        return SharedPreferenceManager.getInstance().getString(Constant.preference.ACCOUNT_ID, "");
    }

    public long getLong(String key, long defaultVal) {
        if (sharedPreferences != null) {
            return sharedPreferences.getLong(key, defaultVal);
        } else {
            return defaultVal;
        }
    }

    public void putFloat(String key, float value) {
        (sharedPreferences.edit()).putFloat(key, value).apply();
    }

    public float getFloat(String key, float defaultVal) {
        if (sharedPreferences != null) {
            return sharedPreferences.getFloat(key, defaultVal);
        } else {
            return defaultVal;
        }
    }

    public String getTrueVaultKey(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key, key);
        } else {
            return key;
        }
    }

    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }

    public String getRequestKey() {
        return sharedPreferences.getString(Constant.preference.REQUEST_KEY, "");
    }

    public String getFullName() {
        return (getString(Constant.preference.FIRST_NAME, "") + " " + getString(Constant.preference.LAST_NAME, "")).trim();
    }
}