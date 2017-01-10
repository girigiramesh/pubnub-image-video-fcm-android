package com.pubnub_fcm_example.model;

import android.graphics.Bitmap;

/**
 * Created by Ramesh on 1/10/17.
 */
public class Image {

    String filename;
    Bitmap bitmap;

    public Image(String filename, Bitmap bitmap) {
        this.filename = filename;
        this.bitmap = bitmap;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
