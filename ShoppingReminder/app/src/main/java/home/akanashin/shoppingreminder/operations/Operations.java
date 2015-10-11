package home.akanashin.shoppingreminder.operations;

import android.content.ContentResolver;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import datastore.generated.provider.places.PlacesSelection;
import datastore.generated.provider.placetypes.PlaceTypesSelection;
import datastore.generated.provider.tasks.TasksSelection;
import home.akanashin.shoppingreminder.utils.Commons;
import home.akanashin.shoppingreminder.utils.async_stuff.AsyncOpCallback;
import home.akanashin.shoppingreminder.utils.async_stuff.ConfigurableOps;
import home.akanashin.shoppingreminder.utils.async_stuff.DatabaseOperation;
import home.akanashin.shoppingreminder.utils.datatypes.PlaceData;
import home.akanashin.shoppingreminder.utils.datatypes.PlaceType;
import home.akanashin.shoppingreminder.utils.datatypes.Result;

/**
 * Created by akana_000 on 6/19/2015.
 */
public class Operations {

    private TaskOps      mTaskOps;
    private PlaceTypeOps mPlaceTypeOps;
    private PlaceOps     mPlaceOps;

    public Operations() {
        mPlaceTypeOps = new PlaceTypeOps();
        mPlaceOps = new PlaceOps();
        mTaskOps = new TaskOps();
    }

    /*
     * Accessors for groups of home.akanashin.shoppingreminder.operations
     */
    public PlaceTypeOps placeType() {
        return mPlaceTypeOps;
    }

    public PlaceOps place() {
        return mPlaceOps;
    }

    public TaskOps task() {
        return mTaskOps;
    }

    /**
     * Clearer of database
     */
    public static void clearDB(AsyncOpCallback cb) {
        new DatabaseOperation<Result<Integer>>(cb) {
            @Override
            public Result<Integer> doOperation(ContentResolver cr) {
                // 1st: clear place-types
                new PlaceTypesSelection().delete(cr);

                // 2nd: clear places
                new PlacesSelection().delete(cr);

                // 3nd: clear tasks
                new TasksSelection().delete(cr);

                return null;
            }
        }.run();
    }

    /**
     * Creater if 'demo' database
     */
    public void initDB(final AsyncOpCallback cb) {
        // initial structure
        // Nb: all these need to have fixed IDs
        final PlaceType[] mTypes = new PlaceType[]{
                new PlaceType("Blue", 0xFF0000FF),
                new PlaceType("Green", 0xFF00FF00),
                new PlaceType("Red", 0xFFFF0000),
        };

        final PlaceData[] mPlaces = new PlaceData[]{
                new PlaceData("home", 1.0, 1.0,
                        new ArrayList<PlaceType>() {{
                            add(mTypes[0]);
                            add(mTypes[2]);
                        }}),
                new PlaceData("office", 0.5, 0.5,
                        new ArrayList<PlaceType>() {{
                            add(mTypes[0]);
                        }}),

                new PlaceData("shop", 1.5, 0.5,
                        new ArrayList<PlaceType>() {{
                            add(mTypes[1]);
                        }})
        };

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                // clean the database
                place().deleteSync(-1);
                placeType().deleteSync(-1);

                // write place types and re-query it to get proper IDs
                placeType().addOrModifySync(mTypes);

                PlaceType[] newTypes;
                try {
                    newTypes = placeType().queryListSync();
                } catch (OpsException e) {
                    throw new AssertionError(e.getMessage());
                }
                if(newTypes == null)
                    throw new AssertionError("Error: cannot read place types from database!");

                // fix IDs of place types in array of places
                for (PlaceType pt : mTypes) {
                    // set ID based on name
                    long id = -1;
                    for (PlaceType placeType : newTypes)
                        if (placeType.name.equals(pt.name))
                            id = placeType.id;

                    if (id == -1)
                        throw new AssertionError("Error: cannot find placetype with name " + pt.name);

                    pt.id = id;
                }

                // write places to database
                place().addOrModifySync(mPlaces);

                return null;
            }

            @Override
            protected void onPostExecute(Void p) {
                cb.doStuff(null);
            }
        }.execute((Void[]) null);
    }
}