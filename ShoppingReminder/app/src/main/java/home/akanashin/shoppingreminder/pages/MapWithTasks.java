package home.akanashin.shoppingreminder.pages;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import home.akanashin.shoppingreminder.utils.MyApp;
import home.akanashin.shoppingreminder.utils.async_stuff.AsyncOpCallback;

/**
 * My wrapper over Google Map widget
 */
public class MapWithTasks extends MapFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // post initialisation of map
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                googleMap.getUiSettings().setZoomControlsEnabled(true);

                // map is ready, center it on current location
                Location loc = MyApp.getInstance().getLocationRequester().getCurrentLocation();

                // it CAN happen
                if (loc != null)
                    googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 15)
                    );
                else
                    Log.w(MyApp.TAG, "Location is unavailable");
            }
        });
    }
}
