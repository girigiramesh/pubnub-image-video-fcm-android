package com.pubnub_fcm_example.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ramesh on 1/10/17.
 */
public class Util {
    private static final String TAG = "Util";
    public static boolean isNotNullAndNotEmpty(String string) {
        return string != null && !string.isEmpty();
    }

    public static boolean isNotNullAndNotEmpty(List list) {
        return list != null && !list.isEmpty();
    }

    public static String getUdid(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getFilePathFromUrl(Context context, Uri contentUri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int maxDimension) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        Log.d(TAG, "width: " + width + ", height: " + height);

        if (width > maxDimension || height > maxDimension) {

            int newWidth;
            int newHeight;

            if (width > height) {
                newWidth = maxDimension;
                newHeight = (int) (((float) height / (float) width) * (float) maxDimension);
            } else {
                newWidth = (int) (((float) width / (float) height) * (float) maxDimension);
                newHeight = maxDimension;
            }

            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;

            Log.d(TAG, "scaled width: " + scaleWidth + ", scaled height: " + scaleHeight);

            // Create a matrix for the manipulation
            Matrix matrix = new Matrix();
            // Resize the bitmap
            matrix.postScale(scaleWidth, scaleHeight);

            // Recreate the new bitmap
            Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

            return resizedBitmap;
        } else {
            return bm;
        }
    }

    public static void setTypefaces(Typeface typeface, TextView... textViews) {
        for (TextView tv : textViews) {
            tv.setTypeface(typeface);
        }
    }

    public static void setTypefaces(Typeface typeface, EditText... editTexts) {
        for (EditText et : editTexts) {
            et.setTypeface(typeface);
        }
    }

    public static void setTypefaces(Typeface typeface, Button... buttons) {
        for (Button btn : buttons) {
            btn.setTypeface(typeface);
        }
    }
}
