package operations;

import android.content.ContentResolver;
import android.util.Log;

import datastore.generated.provider.places.PlacesSelection;
import datastore.generated.provider.placetypes.PlaceTypesSelection;
import utils.Commons;
import utils.async_stuff.AsyncOpCallback;
import utils.async_stuff.ConfigurableOps;
import utils.async_stuff.DatabaseOperation;

/**
 * Created by akana_000 on 6/19/2015.
 */
public class Operations implements ConfigurableOps {
    private PlaceTypeOps mPlaceTypeOps;
    private PlaceOps     mPlaceOps;

    /*
     * Accessors for groups of operations
     */
    public PlaceTypeOps placeType() {
        return mPlaceTypeOps;
    }

    public PlaceOps place() {
        return mPlaceOps;
    }

    /**
     * Clearer of database
     */
    public static void clearDB(AsyncOpCallback cb) {
        new DatabaseOperation<Void>(cb) {
            @Override
            public Void doOperation(ContentResolver cr) {
                // 1st: clear place-types - this should clear types and links
                PlaceTypesSelection wPlaceTypes = new PlaceTypesSelection();
                wPlaceTypes.delete(cr);

                // 1st: clear places - this should clear places
                PlacesSelection wPlaces = new PlacesSelection();
                wPlaces.delete(cr);

                return null;
            }
        }.run();
    }

    /*
     * This method is called when configuration changes occur
     * activity is the new activity
     * firstTimeIn - flag indicates that this is first time (we need to initialization)
     */
    @Override
    public void onConfiguration(boolean firstTimeIn) {
        Log.d(Commons.TAG, "onConfiguration called: firstTimeIn=" + firstTimeIn);

        mPlaceTypeOps = new PlaceTypeOps();
        mPlaceOps = new PlaceOps();
    }
}
