package home.akanashin.shoppingreminder.utils;

import android.app.Activity;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import home.akanashin.shoppingreminder.utils.async_stuff.AsyncOpCallback;

import static com.google.android.gms.common.GooglePlayServicesUtil.getErrorDialog;

/**
 * Created by akana_000 on 8/14/2015.
 */
public class LocationRequester implements com.google.android.gms.location.LocationListener  {
    private static String TAG = "PriceChecker.LOCATION";
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleApiClient        mGoogleApiClient;

    private LocationRequest mLocationRequest;
    private Location mLastLocation = null;

    /**
     * Getter of location
     *  must not be called before 'connectToGoogleAPI'
     * @return last known Location
     */
    public Location getCurrentLocation() {
        return mLastLocation;
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

                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, LocationRequester.this);
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.e(TAG, "Google API connection suspended with code " + i);
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
                        Log.e(TAG, "System error " + e.getLocalizedMessage());
                        cb.doStuff(false);

                        // do i need to unregister myself?
                        mGoogleApiClient.unregisterConnectionFailedListener(this);
                    }
                    return;
                }

                // no luck...
                Log.e(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());

                if (connectionResult.getErrorCode() < 3)
                    getErrorDialog(connectionResult.getErrorCode(), activity, 0).show();

                cb.doStuff(false);

                // do i need to unregister myself?
                mGoogleApiClient.unregisterConnectionFailedListener(this);
            }
        });

        mGoogleApiClient.reconnect();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(MyApp.TAG, "New location update");
        mLastLocation = location;
    }

    public LocationRequester() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10*60000); // 10 minutes
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mGoogleApiClient = new GoogleApiClient.Builder(MyApp.getContext())
                .addApi(LocationServices.API)
                .build();
    }
}
