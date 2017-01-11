package com.pubnub_fcm_example.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.pubnub_fcm_example.manager.PubnubManager;
import com.pubnub_fcm_example.manager.SharedPreferenceManager;
import com.pubnub_fcm_example.model.Message;
import com.pubnub_fcm_example.network.RetrofitHandler;
import com.pubnub_fcm_example.util.Constant;
import com.pubnub_fcm_example.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

/**
 * Created by Ramesh on 1/10/17.
 */

public class CreateMessageService extends IntentService {

    private static final String TAG = "CreateMessageService";
    public static final String ACTION_MESSAGE = "com.pubnub_fcm_example.service.action.message";
    private static final String EXTRA_MESSAGE = "com.pubnub_fcm_example.service.extra.message";
    private static final String EXTRA_TO_ID = "com.pubnub_fcm_example.service.extra.to_id";
    private static final String EXTRA_FILE_PATH = "com.pubnub_fcm_example.service.extra.FILE_PATH ";

    public CreateMessageService() {
        super("CreateMessageService");
    }


    public static void start(Context context, String message, String toId, String filePath) {
        Intent intent = new Intent(context, CreateMessageService.class);
        intent.setAction(ACTION_MESSAGE);
        intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra(EXTRA_TO_ID, toId);
        intent.putExtra(EXTRA_FILE_PATH, filePath);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_MESSAGE.equals(action)) {
                handleActionCreateMessage(intent.getStringExtra(EXTRA_MESSAGE), intent.getStringExtra(EXTRA_TO_ID), intent.getStringExtra(EXTRA_FILE_PATH));
            }
        }
    }

    private void handleActionCreateMessage(final String message, final String receiverUserId, String path) {
        Message MessageModel = Message.fromJson(message);
        Map<String, RequestBody> requestMap = new HashMap<>();

        switch (MessageModel.getType()) {
            case Message.TEXT:
                handleActionMessage(message, receiverUserId, "", null);
                break;
            case Message.IMAGE:
                String imageName = "Pubnub" + System.currentTimeMillis();
                //Retrofit body for image
                RequestBody imgRequestBody = RequestBody.create(MediaType.parse("image/*"), Util.getResizedBitmapAsByteArrayFromFile(path, Constant.value.MAX_MSG_IMAGE_DIMENSION));
                //name as key and retrofit body as value
                requestMap.put("file\"; filename=\"" + imageName + ".png", imgRequestBody);
                handleActionMessage(message, receiverUserId, imageName, requestMap);
                break;
            case Message.VIDEO:

                String videoName = "Pubnub" + System.currentTimeMillis();

                //Retrofit body for image
                RequestBody vidRequestBody = RequestBody.create(MediaType.parse("video/*"), new File(path));
                //map for images
                //name as key and retrofit body as value
                requestMap.put("video\"; filename=\"" + videoName + ".mp4", vidRequestBody);

                // Create the video thumbnail
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                if (null == thumb) {
                    sendBroadcast(true, "Sorry! Error sending message.", null);
                }
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                thumb.compress(Bitmap.CompressFormat.PNG, 100, stream);

                //Retrofit body for image
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), stream.toByteArray());
                //name as key and retrofit body as value
                requestMap.put("thumbnail\"; filename=\"" + "Pubnub" + System.currentTimeMillis() + ".png", requestBody);

                handleActionMessage(message, receiverUserId, videoName, requestMap);
                break;
        }
    }

    private void handleActionMessage(String message, String toId, String resourceName, Map<String, RequestBody> requestBodyMap) {
        Response<String> response;
        Message MessageModel = Message.fromJson(message);
        try {
            response = RetrofitHandler.getInstance().createMessage(
                    SharedPreferenceManager.getInstance().getFullName(),
                    MessageModel, resourceName, requestBodyMap, SharedPreferenceManager.getInstance().getRequestKey()).execute();
            if (response.isSuccessful()) {
                Log.d(TAG, "handleActionMessage: " + response.raw());
                sendBroadcast(false, null, response.body());
                sendPubnubMessage(MessageModel.getType(),
                        MessageModel.getText(),
                        MessageModel.getChannel(),
                        toId);
            } else {
                Log.e(TAG, "handleActionMessage: " + response.raw());
                sendBroadcast(true, "Sorry! Error sending message.", null);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "handleActionMessage: " + e.getLocalizedMessage());
            sendBroadcast(true, "Sorry! Error sending message.", null);
        }
    }

    /**
     * Method to send pubnub notification message
     */
    private void sendPubnubMessage(String type, String text, String channel, String receiverUserId) {
        // Create new pubnub message to notify other user of new message
        if (type.equalsIgnoreCase(Message.TEXT))
            text = "New Text";
        else if (type.equalsIgnoreCase(Message.IMAGE))
            text = "New Image";
        else if (type.equalsIgnoreCase(Message.VIDEO))
            text = "New Video";
        else
            text = "New Message";

        // Publish pubnub message to notify other user/s
        PubnubManager.getInstance().publish(receiverUserId,
                Message.getPubnubMessage(type, text, SharedPreferenceManager.getInstance().getAccountId(), channel));
    }

    private void sendBroadcast(boolean isError, String errorMessage, String output) {
        Intent localIntent = new Intent(ACTION_MESSAGE);
        localIntent.putExtra(Constant.extra.ERROR, isError);
        if (Util.isNotNullAndNotEmpty(errorMessage)) {
            localIntent.putExtra(Constant.extra.ERROR_MESSAGE, errorMessage);
        }
        if (Util.isNotNullAndNotEmpty(output)) {
            localIntent.putExtra(Constant.extra.RESULTS, output);
        }
        LocalBroadcastManager.getInstance(CreateMessageService.this).sendBroadcast(localIntent);
    }
}
