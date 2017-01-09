package com.pubnub_fcm_example;

import android.app.Application;

import com.pubnub_fcm_example.manager.PubnubManager;
import com.pubnub_fcm_example.manager.SharedPreferenceManager;

/**
 * Created by Ramesh on 1/9/17.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferenceManager.getInstance().init(this);
        PubnubManager.getInstance().init();
    }
}
