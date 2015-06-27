package utils;

import android.app.Application;
import android.content.Context;

/**
 * This is wrapper for making applicationContext available everywhere in the program
 */

public class MyApp extends Application {
    private static MyApp instance;

    public static MyApp getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}