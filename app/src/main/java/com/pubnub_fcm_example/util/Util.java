package com.pubnub_fcm_example.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
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

    public static String getFilePath(Context context, Uri contentUri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
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

    public static byte[] getResizedBitmapAsByteArrayFromFile(String filePath, int maxDimension) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int srcWidth = options.outWidth;
        int destWidth = srcWidth;
        int srcHeight = options.outHeight;
        int destHeight = srcHeight;
        if (srcWidth > maxDimension || srcHeight > maxDimension) {
            if (srcWidth > srcHeight) {
                destWidth = maxDimension;
                destHeight = (int) (((float) srcHeight / (float) srcWidth) * (float) maxDimension);
            } else {
                destWidth = (int) (((float) srcWidth / (float) srcHeight) * (float) maxDimension);
                destHeight = maxDimension;
            }
        }
        options = new BitmapFactory.Options();
        options.inScaled = true;
        options.inSampleSize = 2;
        if (srcWidth > srcHeight) {
            options.inDensity = srcWidth;
            options.inTargetDensity = destWidth * options.inSampleSize;
        } else {
            options.inDensity = srcHeight;
            options.inTargetDensity = destHeight * options.inSampleSize;
        }
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        bitmap = Util.getResizedBitmap(bitmap, maxDimension);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        }

        return stream.toByteArray();
    }

    public static String getChannelForTwoUsers(String myUserId, String hisUserId) {
        List<String> userIds = new ArrayList<>();
        userIds.add(myUserId);
        userIds.add(hisUserId);
        return Util.getChannelFromUserIds(userIds);
    }

    public static String getChannelFromUserIds(List<String> userIds) {
        List<String> sortedList = new ArrayList<>();
        sortedList.addAll(userIds);

        Collections.sort(sortedList);

        String channel = "";

        for (int i = 0; i < sortedList.size(); i++) {
            channel += i > 0 ? "_" : "";
            channel += sortedList.get(i);
        }

        return channel;
    }

    public static String stringToBase64(String string) {
        byte[] data = new byte[0];
        try {
            data = string.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        return base64.trim();
    }

    public static String base64ToString(String base64) {
        String text = null;
        byte[] data = Base64.decode(base64.replace("\n", ""), Base64.DEFAULT);
        try {
            text = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return text;
    }
}
