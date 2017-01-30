package com.pubnub_fcm_example.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.pubnub_fcm_example.R;
import com.pubnub_fcm_example.manager.SharedPreferenceManager;
import com.pubnub_fcm_example.util.Constant;
import com.pubnub_fcm_example.util.Util;

import net.hockeyapp.android.CrashManager;

import org.greenrobot.eventbus.EventBus;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_text;

    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Util.isNotNullAndNotEmpty(SharedPreferenceManager.getInstance().getString(Constant.preference.NAME, ""))) {
            ChatActivity.start(this);
            finish();
        }
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);
        et_text = (EditText) findViewById(R.id.et_text);
        findViewById(R.id.btn_continue).setOnClickListener(this);
        configToolbar((Toolbar) findViewById(R.id.toolbar), (getResources().getString(R.string.main_activity)), false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CrashManager.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_continue:
                String name = et_text.getText().toString();
                if (!Util.isNotNullAndNotEmpty(name)) {
                    Toast.makeText(MainActivity.this, R.string.toast_name_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferenceManager.getInstance().putString(Constant.preference.NAME, name);
                ChatActivity.start(this);
                break;
        }
    }
}
