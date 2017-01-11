package com.pubnub_fcm_example.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.pubnub_fcm_example.util.Constant;
import com.pubnub_fcm_example.util.Util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadVideoService extends IntentService {
    private static final String TAG = DownloadVideoService.class.getName();
    public static final String ACTION_DOWNLOAD_VIDEO = "com.pubnub_fcm_example.service.action.DOWNLOAD_VIDEO";

    public static final String EXTRA_PATH = "com.pubnub_fcm_example.service.extra.PATH";
    public static final String EXTRA_URL = "com.pubnub_fcm_example.service.extra.URL";

    public DownloadVideoService() {
        super(TAG);
    }

    public static void startActionDownloadVideo(Context context, String url, String path) {
        Intent intent = new Intent(context, DownloadVideoService.class);
        intent.setAction(ACTION_DOWNLOAD_VIDEO);
        intent.putExtra(EXTRA_PATH, path);
        intent.putExtra(EXTRA_URL, url);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD_VIDEO.equals(action)) {
                handleActionFoo(intent.getStringExtra(EXTRA_URL), intent.getStringExtra(EXTRA_PATH));
            }
        }
    }


    private void handleActionFoo(String urlString, String path) {
        BufferedInputStream inStream = null;
        FileOutputStream outStream = null;
        String filePath = "";
        try {
            filePath = path;
            URL url = new URL(urlString);
            long startTime = System.currentTimeMillis();
            Log.i(TAG, "video download beginning: " + filePath);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            int statusCode = conn.getResponseCode();

            if (statusCode == HttpURLConnection.HTTP_OK) {
                File videoFile = new File(filePath);
                //Define InputStreams to read from the URLConnection.
                // uses 3KB download buffer
                InputStream is = conn.getInputStream();
                inStream = new BufferedInputStream(is);
                outStream = new FileOutputStream(videoFile);
                byte[] buff = new byte[8 * 1024];

                //Read bytes (and store them) until there is nothing more to read(-1)
                int len;
                while ((len = inStream.read(buff)) != -1) {
                    outStream.write(buff, 0, len);
                }

                //clean up
                outStream.flush();
                // outStream.close();
                // inStream.close();
            } else {
                sendBroadcast(true, "Error downloading file..", filePath);
            }

            Log.i(TAG, "download completed in " + (System.currentTimeMillis() - startTime) + " millisec");
        } catch (IOException ioException) {
            sendBroadcast(true, "Error downloading file..", filePath);
            ioException.printStackTrace();
        } finally {
            try {
                if (inStream != null) inStream.close();
            } catch (IOException e) {
                sendBroadcast(true, "Error downloading file..", filePath);
            }
            try {
                if (outStream != null) outStream.close();
            } catch (IOException e) {
                sendBroadcast(true, "Error downloading file..", filePath);
            }
        }

        sendBroadcast(false, "", filePath);
    }

    private void sendBroadcast(boolean isError, String errorMessage, String output) {
        Intent localIntent = new Intent(ACTION_DOWNLOAD_VIDEO);
        localIntent.putExtra(Constant.extra.ERROR, isError);
        if (Util.isNotNullAndNotEmpty(errorMessage)) {
            localIntent.putExtra(Constant.extra.ERROR_MESSAGE, errorMessage);
        }
        if (Util.isNotNullAndNotEmpty(output)) {
            localIntent.putExtra(Constant.extra.RESULTS, output);
        }
        LocalBroadcastManager.getInstance(DownloadVideoService.this).sendBroadcast(localIntent);
        return;
    }
}
