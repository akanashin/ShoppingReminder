package activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import home.akanashin.shoppingreminder.R;
import operations.Operations;
import utils.Commons;
import utils.async_stuff.AsyncOpCallback;
import utils.async_stuff.GenericActivity;
import utils.datatypes.PlaceData;
import utils.datatypes.PlaceType;


public class ShowPlacesActivity extends GenericActivity<Operations>
    implements GoogleMap.OnMapClickListener, GoogleMap.OnMapLoadedCallback,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener
{
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    /* Hash of markers
     *  index - object of Map
     *  value - PlaceData
     */
    private HashMap<Marker, PlaceData> mMarkers = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, Operations.class);

        setContentView(R.layout.activity_setup_places);
        setUpMapIfNeeded();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setup_places_activity, menu);
        return true;
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setOnMapClickListener(this);
                mMap.setOnMarkerClickListener(this);
                mMap.setOnMarkerDragListener(this);
                mMap.setOnMapLoadedCallback(this);
            }
        }

        if (mMap == null)
            throw new AssertionError("mMap is null");
    }

    private void reloadPlaces() {
        // drop all markers first
        mMarkers.clear();

        getOps().place().queryList(new AsyncOpCallback<PlaceData[]>() {
            @Override
            public void run(PlaceData[] data) {
                Log.d(Commons.TAG, "Received " + data.length + " records");
                // add marker for every place we received
                for (PlaceData place : data) {
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
                        throw new AssertionError("PlaceData has no PlaceType");

                    // store PlaceData and Marker
                    mMarkers.put(mMap.addMarker(opts), place);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void modifyPlace(PlaceData place) {
        // Start Editing activity. When it finishes we will either reload data or just continue
        Intent intent = new Intent(this, EditPlaceActivity.class);

        intent.putExtra(EditPlaceActivity.INTENT_ID_UID, place.id)
                .putExtra(EditPlaceActivity.INTENT_ID_LAT, place.loc.latitude)
                .putExtra(EditPlaceActivity.INTENT_ID_LONG, place.loc.longitude);
        startActivityForResult(intent, EditPlaceActivity.INTENT_REQUEST_ID);
    }

    /*
     * Here i receive result of EditPlaceActivity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == EditPlaceActivity.INTENT_REQUEST_ID) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // There was something done to current Place. Reload my list
                Log.d(Commons.TAG, "Reloading items");
                reloadPlaces();
            }
        }
    }

    @Override
    public void onMapClick(LatLng point) {
        Log.d(Commons.TAG, "Map clicked!");

        modifyPlace(new PlaceData("New place", point.latitude, point.longitude, new ArrayList<PlaceType>()));
    }

    @Override
    public void onMapLoaded() {
        reloadPlaces();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // search for clicked marker in my Hash
        //  then open Editing Activity for given PlaceData
        if (mMarkers.containsKey(marker)) {
            modifyPlace(mMarkers.get(marker));
        } else
            throw new AssertionError("OnClick from unregistered marker!");

        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {}

    @Override
    public void onMarkerDrag(Marker marker) {}

    @Override
    public void onMarkerDragEnd(Marker marker) {
        // ToDO: store new coordinates
    }
}
