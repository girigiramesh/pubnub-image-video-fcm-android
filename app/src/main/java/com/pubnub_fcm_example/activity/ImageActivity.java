package com.pubnub_fcm_example.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.analytics.HitBuilders;
import com.pubnub_fcm_example.R;
import com.pubnub_fcm_example.util.Util;

import java.net.UnknownHostException;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageActivity extends BaseActivity {
    private static final String FILE_PATH = "file_path";
    private static final String FILE_URL = "file_url";

    private RequestListener<String, GlideDrawable> glideListener = new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            if (e instanceof UnknownHostException)
                progressBar.setVisibility(View.VISIBLE);
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            progressBar.setVisibility(View.GONE);
            if (mAttacher != null) {
                mAttacher.update();
            } else {
                mAttacher = new PhotoViewAttacher(chatImageView);
            }
            return false;
        }
    };


    private ImageView chatImageView;
    private ProgressBar progressBar;
    public static String TAG = ImageActivity.class.getName();
    PhotoViewAttacher mAttacher;


    public static void start(Context context, String fileUrl, String filePath) {
        Intent starter = new Intent(context, ImageActivity.class);
        starter.putExtra(FILE_PATH, filePath);
        starter.putExtra(FILE_URL, fileUrl);
        context.startActivity(starter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        chatImageView = (ImageView) findViewById(R.id.chat_image_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);


        String fileUrl = getIntent().getStringExtra(FILE_URL);
        String filePath = getIntent().getStringExtra(FILE_PATH);

        if (Util.isNotNullAndNotEmpty(filePath)) {
            Glide.with(this)
                    .load("file://" + filePath)
                    .listener(glideListener)
                    .into(chatImageView);
        } else {
            try {
                Glide.with(this)
                        .load("file://" + filePath)
                        .listener(glideListener)
                        .into(chatImageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Close activity back to login if no network
        if (!isNetworkAvailable()) {
            finish();
        }
//        CrashManager.register(this);
        if (mTracker != null) {
            mTracker.setScreenName(TAG);
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
