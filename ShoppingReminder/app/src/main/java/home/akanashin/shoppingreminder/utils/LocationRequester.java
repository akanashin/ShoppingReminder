package home.akanashin.shoppingreminder.utils;

import android.app.Activity;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import home.akanashin.shoppingreminder.utils.async_stuff.AsyncOpCallback;

/**
 * Created by akana_000 on 8/14/2015.
 */
public class LocationRequester {
    private static String TAG = "PriceChecker.LOCATION";
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleApiClient mGoogleApiClient;

    /**
     * Getter of location
     *  must not be called before 'connectToGoogleAPI'
     * @return last known Location
     */
    public Location getCurrentLocation() {
        assert (mGoogleApiClient != null);
        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    /**
     * Connects Google API if not connected
     * @param cb callback for success/suspend
     * @param activity activity for resolution of failure
     */
    public void connectToGoogleAPI(final AsyncOpCallback<Boolean> cb,
                                   final Activity activity) {
        assert(mGoogleApiClient != null);

        // check if we already connected
        if (mGoogleApiClient.isConnected()) {
            cb.doStuff(true);
            return;
        }

        // we are either connecting or disconnected
        // add new callbacks and reconnect
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                Log.i(TAG, "Google API connected");
                cb.doStuff(true);

                // do i need to unregister myself?
                mGoogleApiClient.unregisterConnectionCallbacks(this);
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.i(TAG, "Google API connection suspended with code " + i);
                cb.doStuff(false);

                // do i need to unregister myself?
                mGoogleApiClient.unregisterConnectionCallbacks(this);
            }
        });
        mGoogleApiClient.registerConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                if (connectionResult.hasResolution()) {
                    try {
                        // Start an Activity that tries to resolve the error
                        connectionResult.startResolutionForResult(activity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                    } catch (IntentSender.SendIntentException e) {
                        Log.i(TAG, "System error " + e.getLocalizedMessage());
                        cb.doStuff(false);

                        // do i need to unregister myself?
                        mGoogleApiClient.unregisterConnectionFailedListener(this);
                    }
                    return;
                }

                // no luck...
                Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
                cb.doStuff(false);

                // do i need to unregister myself?
                mGoogleApiClient.unregisterConnectionFailedListener(this);
            }
        });

        mGoogleApiClient.reconnect();
    }

    public LocationRequester() {
        mGoogleApiClient = new GoogleApiClient.Builder(MyApp.getContext())
                .addApi(LocationServices.API)
                .build();
    }
}
