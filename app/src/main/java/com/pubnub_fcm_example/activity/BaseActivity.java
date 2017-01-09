package com.pubnub_fcm_example.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.pubnub_fcm_example.App;
import com.pubnub_fcm_example.R;
import com.pubnub_fcm_example.util.Util;

/**
 * Created by Ramesh on 1/9/17.
 */

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();
    private ProgressDialog dialog = null;

    public void showProgressDialog(String message) {
        if (dialog == null) dialog = new ProgressDialog(this);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        try {
            if (!dialog.isShowing()) dialog.show();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public void dismissProgressDialog() {
        dialog.dismiss();
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
}
