package com.pubnub_fcm_example.activity;

import android.os.Bundle;
import android.os.Handler;

import com.pubnub_fcm_example.R;
import com.pubnub_fcm_example.manager.SessionManager;

import net.hockeyapp.android.CrashManager;


/**
 * Created by Ramesh on 1/10/17.
 */

public class SplashActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (SessionManager.getInstance().getLoginStatus()) {
                    case "0":
                        MainActivity.start(SplashActivity.this);
                        break;
                    case "1":
                        ChatActivity.start(SplashActivity.this);
                        break;
                }
                finish();
            }
        }, 3000);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        HockeyApp
        CrashManager.register(this);
    }
}
