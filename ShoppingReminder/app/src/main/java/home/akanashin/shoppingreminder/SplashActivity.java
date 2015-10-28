package home.akanashin.shoppingreminder;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import home.akanashin.shoppingreminder.utils.LocationRequester;
import home.akanashin.shoppingreminder.utils.MyApp;
import home.akanashin.shoppingreminder.utils.async_stuff.AsyncOpCallback;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends Activity {

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    private View mContentView;
    private View mControlsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_splash);

        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        final LocationRequester locationRequester = MyApp.getLocationRequester();
        locationRequester.connectToGoogleAPI(new AsyncOpCallback<Boolean>() {
            @Override
            public void doStuff(Boolean value) {
                if (value) {
                    // we have connected. wait until location is available
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(MyApp.TAG, "Starting to ask for location");
                            // wait for location to be ready
                            Location location;
                            int counter = 100;
                            do {
                                try {
                                    Thread.sleep(100, 0); // wait for 100 milliseconds
                                    counter--;
                                } catch (InterruptedException ignored) {
                                }
                                location = locationRequester.getCurrentLocation();
                            } while ((location == null) && (counter > 0));

                            // continue
                            if (location != null) {
                                hide();
                                Intent intent = new Intent(SplashActivity.this, FrontActivity.class);
                                startActivity(intent);
                                finish(); // no need to have this activity
                            } else {
                                // location cannot be get
                                AlertDialog dlg = new AlertDialog.Builder(SplashActivity.this)
                                        .setIcon(R.drawable.ic_action_cancel)
                                        .setTitle(R.string.locations_are_disabled_header)
                                        .setMessage(R.string.locations_are_disabled_opening_settings)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                                startActivity(intent);

                                                SplashActivity.this.finish();
                                            }
                                        })
                                        .create();

                                dlg.show();
                            }
                        }
                    }, 1000);
                } else {
                    Toast.makeText(SplashActivity.this, "Error connecting to Google API", Toast.LENGTH_LONG).show();
                    ((TextView) SplashActivity.this.findViewById(R.id.textView)).setText("Cannot continue");
                    finish(); // no need to have this activity
                }
            }
        }, SplashActivity.this);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };

    private final Handler mHideHandler = new Handler();
}
