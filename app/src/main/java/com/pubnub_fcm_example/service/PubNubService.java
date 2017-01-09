package com.pubnub_fcm_example.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by Ramesh on 1/9/17.
 */

public class PubNubService extends IntentService {
    private static final String TAG = PubNubService.class.getSimpleName();
    public static final String ACTION_RECEIVE_MESSAGE = "com.pubnub_fcm_example.service.action.RECEIVE_MESSAGE";
    public static final String EXTRA_MESSAGE = "com.pubnub_fcm_example.service.extra.MESSAGE";

    public PubNubService() {
        super(TAG);
    }

    public static void startReceiveMessage(Context context, String message) {
        Intent intent = new Intent(context, PubNubService.class);
        intent.setAction(ACTION_RECEIVE_MESSAGE);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_RECEIVE_MESSAGE.equals(action)) {
                String dataString = intent.getStringExtra(EXTRA_MESSAGE);
                Intent localIntent = new Intent(ACTION_RECEIVE_MESSAGE).putExtra(EXTRA_MESSAGE, dataString);
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            }
        }
    }
}
