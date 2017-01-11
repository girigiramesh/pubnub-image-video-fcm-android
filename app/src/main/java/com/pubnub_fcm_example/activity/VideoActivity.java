package com.pubnub_fcm_example.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.google.android.gms.analytics.HitBuilders;
import com.pubnub_fcm_example.R;
import com.pubnub_fcm_example.service.DownloadVideoService;
import com.pubnub_fcm_example.util.Util;

import java.io.File;

public class VideoActivity extends BaseActivity {
    private static final String TAG = VideoActivity.class.getSimpleName();
    private static final String FILE_PATH = "file_path";
    private static final String FILE_URL = "file_url";
    private static final String THUMBNAIL_URL = "file_url";

    private VideoView consultVideoView;
    private View videoLayout, progressView;
    private ImageView playButton;
    private Uri uri;

    public static void start(Context context, String fileUrl, String thumbnailUrl, String filePath) {
        Intent starter = new Intent(context, VideoActivity.class);
        starter.putExtra(FILE_PATH, filePath);
        starter.putExtra(FILE_URL, fileUrl);
        starter.putExtra(THUMBNAIL_URL, thumbnailUrl);
        context.startActivity(starter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        hideKeyboard();
        pullViews();
        init();
        String fileUrl = getIntent().getStringExtra(FILE_URL);
        String filePath = getIntent().getStringExtra(FILE_PATH);
        String thumbnailUrl = getIntent().getStringExtra(FILE_PATH);

        if (Util.isNotNullAndNotEmpty(filePath)) {
            consultVideoView.setVideoPath(filePath);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTracker != null) {
            mTracker.setScreenName(TAG);
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }

        // Close activity back to login if no network
        if (!isNetworkAvailable()) {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_READ_WRITE_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DownloadVideoService.startActionDownloadVideo(this, uri.toString(), getMediaDirectory() + File.separator + "SIKKA-VID-" + System.currentTimeMillis() + ".mp4");
                } else {
                    finish();
                    showToast("You cannot play video unless you allow PubNub access to the storage on your device...");
                }
                return;
            }
        }
    }

    private void pullViews() {
        videoLayout = findViewById(R.id.video_layout);
        progressView = findViewById(R.id.progress_bar);
        playButton = (ImageView) findViewById(R.id.play_button);
        consultVideoView = (VideoView) findViewById(R.id.consult_video_view);
    }

    private void init() {
        View.OnClickListener actionbarListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (getSupportActionBar().isShowing()) {
//                    getSupportActionBar().hide();
//                } else {
//                    getSupportActionBar().show();
//                }
            }
        };

        videoLayout.setOnClickListener(actionbarListener);
        consultVideoView.setOnClickListener(actionbarListener);
        progressView.setOnClickListener(actionbarListener);

        consultVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.d(TAG, "onError:");
                finish();
                showToast("Error playing video, incompatible format");
                return false;
            }
        });

        consultVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "onPrepared");
                progressView.setVisibility(View.GONE);

                mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mp) {
                        Log.d(TAG, "setOnSeekCompleteListener");
                        mp.pause();
                    }
                });
                mp.start();
                mp.seekTo(1000);
            }
        });

        consultVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (consultVideoView.isPlaying()) {
                    consultVideoView.pause();
                    playButton.setBackgroundResource(android.R.color.transparent);
                    playButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                } else {
                    consultVideoView.start();
                    playButton.setBackgroundResource(android.R.color.transparent);
                    playButton.setImageResource(R.drawable.ic_pause_white_24dp);
                }
            }
        });
    }
}