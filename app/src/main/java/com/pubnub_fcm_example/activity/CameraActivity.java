package com.pubnub_fcm_example.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pubnub_fcm_example.App;
import com.pubnub_fcm_example.R;
import com.pubnub_fcm_example.util.Constant;
import com.pubnub_fcm_example.util.Util;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ramesh on 1/10/17.
 */

public class CameraActivity extends BaseActivity {
    private static final String TAG = CameraActivity.class.getSimpleName();

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private ImageView selectedImageIV, openCamIV, gallaryIV;
    private LinearLayout takeImageLL, showImageLL;
    private TextView showUs, retakeWithCam, previewNotOkRetake, doneUploadImage;
    private String urlNumber, from;

    private static final int REQUEST_CODE_IMAGE_CAMERA = 102;
    private String filePath, mCurrentPhotoPath;


    public static void start(Context context) {
        Intent starter = new Intent(context, CameraActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        configToolbar((Toolbar) findViewById(R.id.toolbar), (getResources().getString(R.string.camera)), true);

        selectedImageIV = (ImageView) findViewById(R.id.selectedImageIV);
        openCamIV = (ImageView) findViewById(R.id.openCamIV);
        gallaryIV = (ImageView) findViewById(R.id.gallaryIV);
        takeImageLL = (LinearLayout) findViewById(R.id.takeImageLL);
        showImageLL = (LinearLayout) findViewById(R.id.showImageLL);
        showUs = (TextView) findViewById(R.id.showUs);
        retakeWithCam = (TextView) findViewById(R.id.retakeWithCam);
        previewNotOkRetake = (TextView) findViewById(R.id.previewNotOkRetake);
        doneUploadImage = (TextView) findViewById(R.id.doneUploadImage);
        Util.setTypefaces(App.latoLightTypeface, showUs, retakeWithCam, previewNotOkRetake, doneUploadImage);

        Intent intent = getIntent();

        from = intent.getStringExtra(Constant.FROM_TO_CAM);
        urlNumber = intent.getStringExtra(Constant.URL_NUMBER);

        ((ImageView) findViewById(R.id.openCamIV)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = createImageFile();
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, REQUEST_CODE_IMAGE_CAMERA);
                    }
                } else {
                    showToast("You can't take pictures...");
                }
            }
        });
        ((ImageView) findViewById(R.id.gallaryIV)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
            }
        });

        ((TextView) findViewById(R.id.doneUploadImage)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog("Uploading! Please Wait..");
//                ToDo
            }
        });

        ((TextView) findViewById(R.id.previewNotOkRetake)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeImageLL.setVisibility(View.VISIBLE);
                showImageLL.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap bitmap;
            if (requestCode == SELECT_FILE) {
                filePath = getFilePath(this, data.getData());
                bitmap = createBitMap(filePath);
                selectedImageIV.setImageBitmap(bitmap);
            } else if (requestCode == REQUEST_CAMERA) {
                filePath = mCurrentPhotoPath;
                bitmap = createBitMap(filePath);
                selectedImageIV.setImageBitmap(bitmap);
            }
            takeImageLL.setVisibility(View.GONE);
            showImageLL.setVisibility(View.VISIBLE);
        } else {
            takeImageLL.setVisibility(View.VISIBLE);
            showImageLL.setVisibility(View.GONE);
        }
    }

    public static Bitmap createBitMap(String capturingImageURl) {
        File file = new File(capturingImageURl);
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        return bitmap;
    }

    public static String getFilePath(Context context, Uri contentUri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    private File createImageFile() {
        // Create an image file name
        String imageFileName = "Me" + System.currentTimeMillis();
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Me");
        if (!storageDir.exists())
            storageDir.mkdir();
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpeg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
