package com.pubnub_fcm_example;

import android.app.Application;
import android.graphics.Typeface;

import com.pubnub_fcm_example.manager.PubnubManager;
import com.pubnub_fcm_example.manager.SharedPreferenceManager;

/**
 * Created by Ramesh on 1/9/17.
 */

public class App extends Application {
    public String profileImagesUrls;
    public static Typeface latoTypeface, latoBlackTypeface, latoBoldTypeface, latoLightTypeface;
    public static String latoFont, latoLightFont;
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferenceManager.getInstance().init(this);
        PubnubManager.getInstance().init();

        latoFont = "Lato-Regular.ttf";
        latoLightFont = "Lato-Light.ttf";

        latoTypeface = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
        latoBlackTypeface = Typeface.createFromAsset(getAssets(), "Lato-Black.ttf");
        latoBoldTypeface = Typeface.createFromAsset(getAssets(), "Lato-Bold.ttf");
        latoLightTypeface = Typeface.createFromAsset(getAssets(), "Lato-Light.ttf");
    }
}
