package com.pubnub_fcm_example.manager;

import android.content.Context;
import android.util.Log;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import com.pubnub_fcm_example.service.PubNubService;
import com.pubnub_fcm_example.util.Constant;
import com.pubnub_fcm_example.util.Util;

import org.json.JSONObject;

/**
 * Created by Ramesh on 1/9/17.
 */

public class PubnubManager {
    private static final String TAG = PubnubManager.class.getSimpleName();
    //START - SINGLETON
    private static PubnubManager ourInstance = new PubnubManager();

    public static PubnubManager getInstance() {
        return ourInstance;
    }

    private PubnubManager() {
    }

    //END - SINGLETON
    private Pubnub pubnub;

    public void init() {
        pubnub = new Pubnub(Constant.pubnub.PUBLISH_KEY, Constant.pubnub.SUBSCRIBE_KEY);
    }

    public void subscribe(final Context context, String channel) {

        Callback callback = new Callback() {
            @Override
            public void connectCallback(String channel, Object message) {
                Log.d(TAG, "connectCallback() called with: " + "channel = [" + channel + "], message = [" + message + "]");
            }

            @Override
            public void disconnectCallback(String channel, Object message) {
                Log.i(TAG, "SUBSCRIBE : DISCONNECT on channel:" + channel + " : " + message.getClass() + " : " + message.toString());
            }

            public void reconnectCallback(String channel, Object message) {
                Log.i(TAG, "SUBSCRIBE : RECONNECT on channel:" + channel + " : " + message.getClass() + " : " + message.toString());
            }

            @Override
            public void successCallback(String channel, Object message) {
                Log.d(TAG, "SUBSCRIBE : " + channel + " : " + message.getClass() + " : " + message.toString());
                PubNubService.startReceiveMessage(context, message.toString());
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                Log.e(TAG, "SUBSCRIBE : ERROR on channel " + channel + " : " + error.toString());
            }
        };
        try {
            pubnub.subscribe(channel, callback);
        } catch (PubnubException e) {
            Log.e(TAG, e.toString());
        }
    }

    public void unsubscribe(String channel) {
        pubnub.unsubscribe(channel);
    }

    public void publish(String channel, String message) {
        Callback callback = new Callback() {
            public void successCallback(String channel, Object response) {
                System.out.println(response.toString());
            }

            public void errorCallback(String channel, PubnubError error) {
                System.out.println(error.toString());
            }
        };
        pubnub.publish(channel, message, callback);
    }

    public void publish(final String channel, final JSONObject message) {
        if(!Util.isNotNullAndNotEmpty(pubnub.getAuthKey()) && Util.isNotNullAndNotEmpty(channel)) {
            // Set uuid & auth_key to a base64 representation of user_id

            String channelTobase64 = Util.stringToBase64(channel);
            pubnub.setUUID(channelTobase64);
            pubnub.setAuthKey(channelTobase64);
        }

        try {
            assert pubnub != null;
            if(Util.isNotNullAndNotEmpty(channel)) {
                pubnub.publish(channel, message, new Callback() {
                    @Override
                    public void connectCallback(String channel, Object message) {
                        Log.i(TAG, "publish connectCallback CONNECT on channel:" + channel);
                        //start the resync task to speedup geo-location flow
                        // SyncUtils.TriggerRefresh(GenericAccountService.GetAccount());
                    }

                    @Override
                    public void disconnectCallback(String channel, Object message) {
                        Log.i(TAG, "publish disconnectCallback DISCONNECT on channel:" + channel);
                    }

                    @Override
                    public void reconnectCallback(String channel, Object message) {
                        Log.i(TAG, "publish reconnectCallback RECONNECT on channel:" + channel);
                    }

                    @Override
                    public void successCallback(String channel, Object message) {
                        Log.i(TAG, "publish successCallback RECONNECT on channel:" + channel);
                    }

                    @Override
                    public void errorCallback(String channel, PubnubError error) {
                        super.errorCallback(channel, error);
                        Log.e("publish errorCallback", channel + " " + error.getErrorString());
                    }
                });
            }

        } catch (Exception e) {
            if (e.getMessage() == null)
                e = new Exception(TAG + e);
            Log.e(TAG, "subscribe: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
