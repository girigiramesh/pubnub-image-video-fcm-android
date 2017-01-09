package com.pubnub_fcm_example.util;

import android.content.Context;
import android.provider.Settings;

import java.util.List;

/**
 * Created by Ramesh on 1/10/17.
 */
public class Util {
    public static boolean isNotNullAndNotEmpty(String string) {
        return string != null && !string.isEmpty();
    }

    public static boolean isNotNullAndNotEmpty(List list) {
        return list != null && !list.isEmpty();
    }

    public static String getUdid(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
