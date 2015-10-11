package home.akanashin.shoppingreminder.pages;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import home.akanashin.shoppingreminder.R;
import home.akanashin.shoppingreminder.operations.Operations;
import home.akanashin.shoppingreminder.operations.OpsException;
import home.akanashin.shoppingreminder.utils.MyApp;
import home.akanashin.shoppingreminder.utils.async_stuff.AsyncOpCallback;
import home.akanashin.shoppingreminder.utils.datatypes.PlaceData;

/**
 * My wrapper over Google Map widget
 */
public class MapWithTasks extends MapFragment
        implements GoogleMap.OnMapClickListener, GoogleMap.OnMapLoadedCallback,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener
{
    /* Hash of markers
    *  index - object of Map
    *  value - PlaceData
    */
    private HashMap<Marker, PlaceData> mMarkers = new HashMap<>();

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private Operations mOps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mOps = new Operations();

        // post initialisation of map
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                mMap = googleMap;

                googleMap.getUiSettings().setZoomControlsEnabled(true);

                // map is ready, center it on current location
                Location loc = MyApp.getInstance().getLocationRequester().getCurrentLocation();

                // it CAN happen
                if (loc != null) {
                    googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 15)
                    );

                    // add markers for places

                } else
                    Log.w(MyApp.TAG, "Location is unavailable");

                mMap.setOnMapClickListener(MapWithTasks.this);
                mMap.setOnMarkerClickListener(MapWithTasks.this);
                mMap.setOnMarkerDragListener(MapWithTasks.this);

                try {
                    PlaceData[] placeData = mOps.place().queryListSync();

                    Log.d(MyApp.TAG, "Received " + placeData.length + " records");
                    // add marker for every place we received
                    for (PlaceData place : placeData) {
                        MarkerOptions opts = new MarkerOptions()
                                .position(place.loc)
                                .title(place.name)
                                .draggable(true);

                        // set color for this marker
                        if (place.types.size() == 1) {
                            // using color from PlaceType

                            Bitmap b1 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_place);
                            Bitmap resultBitmap = Bitmap.createBitmap(b1, 0, 0, b1.getWidth() - 1, b1.getHeight() - 1);
                            Paint p = new Paint();
                            ColorFilter filter = new LightingColorFilter(place.types.get(0).color, 1);
                            p.setColorFilter(filter);

                            Canvas canvas = new Canvas(resultBitmap);
                            canvas.drawBitmap(resultBitmap, 0, 0, p);

                            opts.icon(BitmapDescriptorFactory.fromBitmap(resultBitmap));

                        } else if (place.types.size() > 1) { // many types
                            // or use special colored icon
                            opts.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_place_multi));
                        } else // 0 types?
                            opts.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_place));

                        // store PlaceData and Marker
                        mMarkers.put(mMap.addMarker(opts), place);
                    }
                } catch (OpsException e) {
                    e.printStackTrace();
                    Toast.makeText(MapWithTasks.this.getActivity(), "Error reading places", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLoaded() {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }
}
