package home.akanashin.shoppingreminder.test;

import android.test.AndroidTestCase;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import operations.Operations;
import utils.async_stuff.AsyncOpCallback;
import utils.database.DatabaseHelper;
import utils.datatypes.PlaceData;
import utils.datatypes.PlaceType;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing of database operations with PlaceType</a>
 */
public class DatabasePlaceTest extends AndroidTestCase {
    private DatabaseHelper mDB;

    // initial structure
    // Nb: all these need to have fixed IDs
    private PlaceType[] mTypes = new PlaceType[] {
            new PlaceType("Blue",  0xFF0000FF),
            new PlaceType("Green", 0xFF00FF00),
            new PlaceType("Red",   0xFFFF0000),
    };

    private PlaceData[] mPlaces = new PlaceData[] {
            new PlaceData("home", 1.0, 1.0,
                    new ArrayList<PlaceType>() {{ add(mTypes[0]);
                                                  add(mTypes[2]); }} ),
            new PlaceData("office", 0.5, 0.5,
                    new ArrayList<PlaceType>() {{ add(mTypes[0]); }} ),

            new PlaceData("shop", 1.5, 0.5,
                    new ArrayList<PlaceType>() {{ add(mTypes[1]); }} )
    };

    private Operations mOps;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mDB = new DatabaseHelper(mContext);
        mDB.clearDB();

        mOps = new Operations();
        mOps.onConfiguration(true); // initialization of operations
    }

    @Override
    public void tearDown() throws Exception {
        mDB.clearDB();
        mDB.close();
        super.tearDown();
    }

    // adding and checking
    public void testAdd() {
        prepare();

        // 1st: fill database
        insertOrModifyToDB(mPlaces);

        // 2nd: query database
        PlaceData[] data = queryFromDB();

        // checking place types
        assertEquals(mPlaces.length, data.length);

        for (int i = 0; i < mPlaces.length; i++) {
            // check ID of param[i] (must be equal to i)
            assertFalse(data[i] == null);
            assertTrue(data[i].id == i + 1);

            assertTrue(mPlaces[i].equals(data[i]));
        }
    }

    /**
     * Test for modifying existing element
     */
    public void testModify() {
        prepare();

        // 1st: fill database
        insertOrModifyToDB(mPlaces);

        // 2nd: query database
        PlaceData[] data = queryFromDB();
        assertEquals(data.length, mPlaces.length);

        assertTrue(data.length > 2); // i will change 2 elements
        assertTrue(data[0].types.size() >= 1); // i will delete one placeType and add another

        // 3rd: change some of places
        data[0].types.remove(0);      // remove 1st placetype (result should be 2)
        data[0].types.add(mTypes[1]); // add element index 1 (result 2,1)

        data[1].name = data[1].name + " changed";
        data[1].loc  = new LatLng(data[1].loc.latitude, -5);

        // 4rd: write modified values to database
        insertOrModifyToDB( new PlaceData[] {data[0], data[1]});

        // 5th: requery new data from database
        PlaceData[] newdata = queryFromDB();
        assertEquals(data.length, newdata.length);

        // 6th: validate
        for (int i = 0; i < newdata.length; i++) {
            // check ID of param[i] (must be equal to i)
            assertFalse(newdata[i] == null);
            assertTrue(newdata[i].id == i + 1);

            assertTrue(data[i].equals(newdata[i]));
        }
    }

    /**
     * Test for deleting element
     */
    public void testDelete() {
        prepare();

        // 1st: fill database
        insertOrModifyToDB(mPlaces);

        // 2nd: remove one of places
        final int deletedIndex = 1;
        assertTrue(mPlaces.length >= deletedIndex + 1); // i will delete element 2 (index 1)
        deleteFromDB(mPlaces[deletedIndex].id);

        // 3rd: requery new data from database
        PlaceData[] data = queryFromDB();

        // 4th: check length of new array
        //      check that every element in new data is present in initial array
        assertEquals(data.length, mPlaces.length - 1);

        for(PlaceData p: data) {
            assertTrue(p.id != mPlaces[deletedIndex].id);

            boolean found = false;
            for(PlaceData pInitial: mPlaces) {
                if (pInitial.equals(p)) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        }
    }

    /**
     * Prepare database
     */
    private void prepare() {
        mDB.clearDB();

        // create table with PlaceTypes
        writePlaceTypesToDB();

        // read PlaceTypes and fix records for Places
        //  NB: i don't check whether data was read correctly or not
        mTypes = queryPlaceTypesFromDB();

        for(PlaceData place: mPlaces)
            for(PlaceType pt: place.types) {
                // set ID based on name
                int id = -1;
                for(PlaceType placeType: mTypes)
                    if (placeType.name.equals(pt.name))
                        id = placeType.id;

                assertTrue(id != -1);

                pt.id = id;
            }

        // now i have array of places fully prepared to test
    }


    /**
     * Synchronous wrapper over adding to database
     *
     * @param places
     */
    private void insertOrModifyToDB(PlaceData[] places) {
        final CountDownLatch signal = new CountDownLatch(1);

        mOps.place().addOrModify(places, new AsyncOpCallback<Void>() {
            @Override
            public void run(Void param) {
                signal.countDown();
            }
        });

        try {
            signal.await();// wait for callback
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Synchronous wrapper over querying the database
     */
    private PlaceData[] queryFromDB() {
        final CountDownLatch signal = new CountDownLatch(1);
        final ArrayList<PlaceData[]> result = new ArrayList<>(1);

        mOps.place().queryList(new AsyncOpCallback<PlaceData[]>() {
            @Override
            public void run(PlaceData[] param) {
                result.add(param);
                signal.countDown();
            }
        });

        try {
            signal.await();// wait for callback
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result.get(0);
    }

    /**
     * Synchronous wrapper over adding PlaceTypes to database
     */
    private void writePlaceTypesToDB() {
        final CountDownLatch signal = new CountDownLatch(1);

        mOps.placeType().addOrModify(mTypes, new AsyncOpCallback<Void>() {
            @Override
            public void run(Void param) {
                signal.countDown();
            }
        });

        try {
            signal.await();// wait for callback
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Synchronous wrapper over querying the database
     */
    private PlaceType[] queryPlaceTypesFromDB() {
        final CountDownLatch signal = new CountDownLatch(1);
        final ArrayList<PlaceType[]> result = new ArrayList<>(1);

        mOps.placeType().queryList(new AsyncOpCallback<PlaceType[]>() {
            @Override
            public void run(PlaceType[] param) {
                result.add(param);
                signal.countDown();
            }
        });

        try {
            signal.await();// wait for callback
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result.get(0);
    }

    /**
     * Synchronous wrapper over deleting from database
     */
    private void deleteFromDB(int id) {
        final CountDownLatch signal = new CountDownLatch(1);

        mOps.place().delete(id, new AsyncOpCallback<Void>() {
            @Override
            public void run(Void v) {
                signal.countDown();
            }
        });

        try {
            signal.await();// wait for callback
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
