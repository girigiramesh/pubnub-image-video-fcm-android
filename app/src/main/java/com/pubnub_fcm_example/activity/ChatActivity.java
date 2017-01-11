package com.pubnub_fcm_example.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.gms.analytics.HitBuilders;
import com.pubnub_fcm_example.R;
import com.pubnub_fcm_example.adapter.ChatAdapter;
import com.pubnub_fcm_example.manager.PubnubManager;
import com.pubnub_fcm_example.manager.SharedPreferenceManager;
import com.pubnub_fcm_example.model.Message;
import com.pubnub_fcm_example.service.CreateMessageService;
import com.pubnub_fcm_example.service.PubNubService;
import com.pubnub_fcm_example.util.Constant;
import com.pubnub_fcm_example.util.Util;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = ChatActivity.class.getSimpleName();

    private class ChatReceiver extends BroadcastReceiver {
        // Prevents instantiation
        private ChatReceiver() {
        }

        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case PubNubService.ACTION_RECEIVE_MESSAGE:
                    MessageModels.add(Message.fromJson(intent.getStringExtra(PubNubService.EXTRA_MESSAGE)));
                    buildList();
                    if (MessageModels.size() > 1)
                        rv_chat.scrollToPosition(MessageModels.size() - 1);
                    break;
            }
        }
    }

    private ImageView send_tv, chat_bot_iv;
    private EditText et_text_message;
    private RecyclerView rv_chat;
    private ChatAdapter chatAdapter;
    private ChatReceiver chatReceiver;
    private SwipeRefreshLayout swipe_view_srl;
    private List<Message> MessageModels;
    private String udid;
    private String filePath;
    private String channel, hisId, myId;
    private LocalBroadcastManager localBroadcastManager;

    public static void start(Context context) {
        Intent starter = new Intent(context, ChatActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        configToolbar((Toolbar) findViewById(R.id.toolbar), (getResources().getString(R.string.chatting)), true);
        init();
        initBroadcastReceiver();

        myId = SharedPreferenceManager.getInstance().getAccountId();
        hisId = getIntent().getStringExtra(Message.FROM_ID);

        if (myId.equals(hisId)) {
            finish();
            return;
        }

        PubnubManager.getInstance().subscribe(this, myId);

        showProgressDialog("Getting messages! Please wait..");
//        SearchMessagesService.startActionConnections(this, channel);

        int storePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readStorePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (storePermission != PackageManager.PERMISSION_GRANTED && readStorePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_READ_WRITE_STORAGE,
                    REQUEST_PERMISSION_READ_WRITE_STORAGE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        CrashManager.register(this);
        SharedPreferenceManager.getInstance().putString(Constant.preference.OPEN_CHANNEL_ID, channel);
        SharedPreferenceManager.getInstance().putString(Constant.preference.SENDER_ID, hisId);
        if (mTracker != null) {
            mTracker.setScreenName(TAG);
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferenceManager.getInstance().remove(Constant.preference.OPEN_CHANNEL_ID);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (localBroadcastManager != null)
            localBroadcastManager.unregisterReceiver(chatReceiver);

        SharedPreferenceManager.getInstance().remove(Constant.preference.OPEN_CHANNEL_ID);
        SharedPreferenceManager.getInstance().remove(Constant.preference.SENDER_ID);
        SharedPreferenceManager.getInstance().remove(Constant.preference.SIKKA_SENDER_ID);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_tv:
                String message = et_text_message.getText().toString();
                if (!Util.isNotNullAndNotEmpty(message.trim())) {
                    Toast.makeText(ChatActivity.this, R.string.toast_empty_message, Toast.LENGTH_SHORT).show();
                    return;
                }

                PubnubManager.getInstance().publish(Constant.pubnub.CHANNEL,
                        new Message(udid, SharedPreferenceManager.getInstance().getString(Constant.preference.NAME, ""), message).toJson().toString());
                et_text_message.setText("");
                break;
            case R.id.chat_bot_iv:
//                CameraActivity.start(ChatActivity.this);
//                finish();
                new BottomSheet.Builder(ChatActivity.this).grid().sheet(R.menu.menu_bottom_chat).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == R.id.take_photo) {
                            // Dispatch take photo intent
                            //Google Analytics
                            addTrackerEntry(Constant.GA.MESSAGE_TAB, Constant.GA.MESSAGE_TAB_CAMERA_STARTED_PHOTO);
                            dispatchTakePictureIntent();
                        } else if (which == R.id.photo_gallery) {
                            //Google Analytics
                            addTrackerEntry(Constant.GA.MESSAGE_TAB, Constant.GA.MESSAGE_TAB_GALLARY_OPENED);
                            // Dispatch pick photo intent
                            dispatchPickPhotoIntent();
                        } else if (which == R.id.send_video) {
                            //Google Analytics
                            addTrackerEntry(Constant.GA.MESSAGE_TAB, Constant.GA.MESSAGE_TAB_CAMERA_STARTED_VIDEO);
                            // Dispatch video chooser intent
                            dispatchVideoChooserIntent();
                        }
                    }
                }).show();
                break;
        }
    }


    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PICTURE || requestCode == REQUEST_PICK_PHOTO || requestCode == REQUEST_IMAGE_CHOOSER) {
                if (data != null && data.getData() != null) {
                    filePath = Util.getFilePath(ChatActivity.this, data.getData());
                } else {
                    filePath = getCurrentPhotoPath();
                }

                final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
                dialog.setContentView(R.layout.dialog_media_confirmation);
                dialog.setCancelable(true);
                ((TextView) dialog.findViewById(R.id.tv_confiramtion_text)).setText("Are you sure you want to upload this image?");
                ImageView selected_media = (ImageView) dialog.findViewById(R.id.selected_media);
                Glide.with(this)
                        .load(filePath)
                        .into(selected_media);

                (dialog.findViewById(R.id.tv_ok_upload)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        String progressDialogMessage = "Encrypting your image...";
                        sendMediaMessage(filePath, progressDialogMessage, requestCode);
                    }
                });
                (dialog.findViewById(R.id.tv_cancel_upload)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();


            } else if (requestCode == REQUEST_VIDEO_CHOOSER) {
                Uri videoUri = data.getData();
                filePath = Util.getRealPathFromURI(ChatActivity.this, videoUri);

                final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
                dialog.setContentView(R.layout.dialog_media_confirmation);
                dialog.setCancelable(true);
                ((TextView) dialog.findViewById(R.id.tv_confiramtion_text)).setText("Are you sure you want to upload this video?");
                ImageView selected_media = (ImageView) dialog.findViewById(R.id.selected_media);
                selected_media.setImageBitmap(ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND));
                (dialog.findViewById(R.id.play_arrow)).setVisibility(View.VISIBLE);
                (dialog.findViewById(R.id.tv_ok_upload)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        String progressDialogMessage = "Encrypting your video. This may take up to a minute.";
                        sendMediaMessage(filePath, progressDialogMessage, requestCode);
                    }
                });
                (dialog.findViewById(R.id.tv_cancel_upload)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        } else {
            showToast("Something went wrong.");
        }
    }

    private void initBroadcastReceiver() {
        chatReceiver = new ChatReceiver();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(chatReceiver, new IntentFilter(CreateMessageService.ACTION_MESSAGE));
    }

    private void sendMediaMessage(String filePath, String progressDialogMessage, int requestCode) {
        if (filePath != null) {
            showProgressDialog(progressDialogMessage, false);
            Message MessageModel = new Message(hisId, requestCode == REQUEST_VIDEO_CHOOSER ? Message.VIDEO : Message.IMAGE, "");
            MessageModel.setFilePath(filePath);
            MessageModels.add(MessageModel);
            ChatAdapter.build(ChatActivity.this, rv_chat, MessageModels);
            CreateMessageService.start(this, MessageModel.toJson().toString(), hisId, filePath);
        } else {
            showToast("Error uploading media, Unable locate image..!");
        }
    }

    private void init() {
        udid = Util.getUdid(this);

        //pull views
        send_tv = (ImageView) findViewById(R.id.send_tv);
        chat_bot_iv = (ImageView) findViewById(R.id.chat_bot_iv);
        et_text_message = (EditText) findViewById(R.id.et_text_message);
        rv_chat = (RecyclerView) findViewById(R.id.rv_chat);
        //init listeners
        send_tv.setOnClickListener(this);
        chat_bot_iv.setOnClickListener(this);
        swipe_view_srl = (SwipeRefreshLayout) findViewById(R.id.messages_swipe_layout);
        swipe_view_srl.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipe_view_srl.setEnabled(true);

        ChatReceiver chatReceiver = new ChatReceiver();
        IntentFilter receiveMessageIntentFilter = new IntentFilter(PubNubService.ACTION_RECEIVE_MESSAGE);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(chatReceiver, receiveMessageIntentFilter);

        MessageModels = new ArrayList<>();
        //subscribe to pubnub
        PubnubManager.getInstance().subscribe(this, Constant.pubnub.CHANNEL);
    }

    private void buildList() {
        if (null == rv_chat.getAdapter()) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            linearLayoutManager.setStackFromEnd(true);
            rv_chat.setLayoutManager(linearLayoutManager);
            chatAdapter = new ChatAdapter(this, MessageModels);
            rv_chat.setAdapter(chatAdapter);
        } else {
            chatAdapter.rebuild(MessageModels);
        }
    }
}
