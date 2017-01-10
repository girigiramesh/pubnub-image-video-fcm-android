package com.pubnub_fcm_example.manager;


import com.pubnub_fcm_example.model.Message;
import com.pubnub_fcm_example.util.Util;

import org.json.JSONObject;

/**
 * Created by Ramesh on 1/10/17.
 */
public class SessionManager {
    private static final String LOGIN_STATUS = "login_status";
    private static final String USER_LOGIN = "com.pubnub_fcm_example.manager.USER_LOGIN";
    private static final String ACCESS_TOKEN = "com.pubnub_fcm_example.manager.ACCESS_TOKEN";

    private static SessionManager ourInstance = new SessionManager();

    public static SessionManager getInstance() {
        return ourInstance;
    }

    private SessionManager() {
    }

    public void putSession(String accessToken, JSONObject userJson) {
        SharedPreferenceManager.getInstance().putString(ACCESS_TOKEN, accessToken);
        SharedPreferenceManager.getInstance().putString(USER_LOGIN, userJson.toString());
    }

    public Message getSession() {
        String info = SharedPreferenceManager.getInstance().getString(USER_LOGIN, "");
        if (!Util.isNotNullAndNotEmpty(info)) {
            throw new IllegalStateException("User not logged in.");
        }
        return Message.fromJson(info);
    }

    public String getAccessToken() {
        return SharedPreferenceManager.getInstance().getString(ACCESS_TOKEN, "");
    }

    public void setLoginStatus(String text) {
        SharedPreferenceManager.getInstance().putString(LOGIN_STATUS, text);
    }

    public String getLoginStatus() {
        return SharedPreferenceManager.getInstance().getString(LOGIN_STATUS, "0");
    }

    public boolean isSessionAvailable() {
        String info = SharedPreferenceManager.getInstance().getString(USER_LOGIN, "");
        if (Util.isNotNullAndNotEmpty(info)) {
            return true;
        }
        return false;
    }

    public void removeSession() {
        SharedPreferenceManager.getInstance().remove(USER_LOGIN);
    }
}
