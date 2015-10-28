package home.akanashin.shoppingreminder.utils;

import android.app.Application;
import android.content.Context;

/**
 * This is wrapper for making applicationContext available everywhere in the program
 */

public class MyApp extends Application {
    public static String TAG = "Reminder";

    private static MyApp instance;
    private LocationRequester mLocationRequester;

    /**
     * Public stuff
     */
    public static MyApp getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance.getApplicationContext();
    }

    public static LocationRequester getLocationRequester() {
        return instance.mLocationRequester;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mLocationRequester = new LocationRequester();
    }
}