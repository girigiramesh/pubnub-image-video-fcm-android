package com.pubnub_fcm_example.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.pubnub_fcm_example.App;
import com.pubnub_fcm_example.R;
import com.pubnub_fcm_example.model.Message;
import com.pubnub_fcm_example.util.Constant;
import com.pubnub_fcm_example.util.Util;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ramesh on 1/9/17.
 */

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();
    private String mCurrentPhotoPath;
    protected Tracker mTracker;
    private InputMethodManager imm;

    public static final int REQUEST_TAKE_PICTURE = 1;
    public static final int REQUEST_PICK_PHOTO = 2;
    public static final int REQUEST_IMAGE_CHOOSER = 3;
    public static final int REQUEST_VIDEO_CHOOSER = 4;
    public static final int REQUEST_PERMISSION_TAKE_PICTURE = 11;
    public static final int REQUEST_PERMISSION_PICK_PHOTO = 12;
    public static final int REQUEST_PERMISSION_IMAGE_CHOOSER = 13;
    public static final int REQUEST_PERMISSION_VIDEO_CHOOSER = 14;
    public static final int REQUEST_PERMISSION_READ_WRITE_STORAGE = 17;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTracker = GoogleAnalytics.getInstance(this).newTracker("UA-79476606-2");
    }

    private ProgressDialog dialog = null;

    public void showProgressDialog(String message, Boolean... setCancelable) {
        try {
            if (dialog != null) {
                dialog.dismiss();
            }
        }catch (Exception ex){
            Log.d(TAG,ex.getMessage());
        }
        if (dialog == null) dialog = new ProgressDialog(this);

        dialog.setMessage(message);
        try {
            if (setCancelable != null && setCancelable.length > 0) {
                dialog.setCancelable(setCancelable[0]);
            } else {
                dialog.setCancelable(true);
            }

        } catch (Exception ex1) {
            dialog.setCancelable(true);
        }
        try {
                if (!dialog.isShowing()) dialog.show();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public void showProgressDialog(String message, String title, Boolean... setCancelable) {
        if (dialog == null) dialog = new ProgressDialog(this);
        dialog.setMessage(message);
        dialog.setTitle(title);
        try {
            if (setCancelable != null && setCancelable.length > 0) {
                dialog.setCancelable(setCancelable[0]);
            } else {
                dialog.setCancelable(true);
            }

        } catch (Exception ex1) {
            dialog.setCancelable(true);
        }
        try {
            if (!dialog.isShowing()) dialog.show();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public void dismissProgressDialog() {
        try {
            if ((dialog != null) && (dialog.isShowing())) dialog.dismiss();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        return nf != null && nf.isConnected();
    }

    /**
     * Displays a simple toast message
     *
     * @param message
     */
    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Hides the keyboard
     */
    public void hideKeyboard() {
        // Hide views
        imm.hideSoftInputFromWindow(
                getWindow().getDecorView().findViewById(android.R.id.content).getWindowToken(), 0);
    }

    // Permissions
    public static String[] PERMISSIONS_TAKE_PICTURE = {
            Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public static String[] PERMISSIONS_PICK_PHOTO = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public static String[] PERMISSIONS_VIDEO_CHOOSER = {
            Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public static String[] PERMISSIONS_READ_WRITE_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public void configToolbar(Toolbar toolbar, String title, boolean isHomeAsUp) {
        toolbar.setTitle("");
//        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        TextView titleTextView = (TextView) findViewById(R.id.tv_toolbar);
        setSupportActionBar(toolbar);
        titleTextView.setText(title);
        if (isHomeAsUp) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public File getMediaDirectory() {
        File mediaDirectory = new File(getApplicationContext().getFilesDir() + "/" + Constant.CHAT + "/");
        if (!mediaDirectory.exists())
            mediaDirectory.mkdirs();
        return mediaDirectory;
    }

    public File createFile(String type) {
        getMediaDirectory();
        String imageFileName = Constant.CHAT + "-" + System.currentTimeMillis();
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/" + Constant.CHAT);
        if (!storageDir.exists())
            storageDir.mkdir();
        File image = null;
        try {
            image = File.createTempFile(imageFileName, Message.IMAGE.equals(type) ? ".jpg" : ".mp4", storageDir);
        } catch (IOException e) {
            Log.e(TAG, "Error in createImageFile: " + e.getMessage());
        }
        setCurrentPhotoPath(image.getAbsolutePath());
        return image;
    }

    public String getCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public void setCurrentPhotoPath(String mCurrentPhotoPath) {
        this.mCurrentPhotoPath = mCurrentPhotoPath;
    }

    public void addTrackerEntry(String Category,String Action){
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(Category)
                .setAction(Action)
                .build());
    }

          /* ----------------------------- DISPATCH INTENT METHODS ----------------------------- */

    protected void dispatchTakePictureIntent() {
        int camPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int storePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (camPermission != PackageManager.PERMISSION_GRANTED || storePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_TAKE_PICTURE,
                    REQUEST_PERMISSION_TAKE_PICTURE);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = createFile(Message.IMAGE);
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PICTURE);
                }
            } else {
                showToast("You can't take pictures...");
            }
        }
    }

    public void dispatchPickPhotoIntent() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Permission not granted, granting now...");
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_PICK_PHOTO,
                    REQUEST_PERMISSION_PICK_PHOTO
            );
        } else {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, REQUEST_PICK_PHOTO);
        }
    }

    public void dispatchVideoChooserIntent() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_VIDEO_CHOOSER,
                    REQUEST_PERMISSION_VIDEO_CHOOSER);
        } else {
            Intent pickVideoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);

            String pickTitle = "Choose Action";
            Intent chooserIntent = Intent.createChooser(pickVideoIntent, pickTitle);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takeVideoIntent});

            if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
//                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
//                    File photoFile = createFile(Message.VIDEO);
//                    if (photoFile != null) {
//                        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
//                        startActivityForResult(chooserIntent, REQUEST_VIDEO_CHOOSER);
//                    } else {
//                        startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CHOOSER);
//                    }
//                } else {
//                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CHOOSER);
//                }
                startActivityForResult(chooserIntent, REQUEST_VIDEO_CHOOSER);
            } else {
                startActivityForResult(pickVideoIntent, REQUEST_VIDEO_CHOOSER);
            }
        }
    }
}
