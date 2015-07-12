package home.akanashin.shoppingreminder.test;

import android.test.AndroidTestCase;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import operations.CommandSyncer;
import operations.Operations;
import utils.datatypes.PlaceData;
import utils.datatypes.PlaceType;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing of database operations with PlaceType</a>
 */
public class DatabasePlaceTest extends AndroidTestCase {
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

        mOps = new Operations();
        mOps.onConfiguration(true); // initialization of operations
    }

    @Override
    public void tearDown() throws Exception {
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
            assertFalse(data[i] == null);

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
            assertFalse(newdata[i] == null);

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
     * Test for Usage Statistics of PlaceType
     * (i do testing here because test needs PlaceData which is not present in DatabasePlaceTypeTest)
     * (i don't test number of tasks - it will be done in another test module)
     */
    public void testUsageStatisticsForPlaceType() {
        prepare();

        // 1st: check usage statistics - it should be 0/0
        PlaceType.Usage usage = queryPlaceTypesUsage(mTypes[1].id);
        assertEquals(0, usage.n_places);

        // 2st: fill database with some data
        insertOrModifyToDB(mPlaces);

        // 3rd: check usage statistics - it should be <something>
        usage = queryPlaceTypesUsage(mTypes[1].id);
        int counter = 0;
        for(PlaceData placeData: mPlaces)
            for(PlaceType placeType: placeData.types)
                if (placeType.id == mTypes[1].id)
                    counter++;

        assertEquals(counter, usage.n_places);
    }

    /**
     * Prepare database
     */
    private void prepare() {
        clearDB();

        // create table with PlaceTypes
        writePlaceTypesToDB();

        // read PlaceTypes and fix records for Places
        //  NB: i don't check whether data was read correctly or not
        mTypes = queryPlaceTypesFromDB();

        for(PlaceData place: mPlaces)
            for(PlaceType pt: place.types) {
                // set ID based on name
                long id = -1;
                for(PlaceType placeType: mTypes)
                    if (placeType.name.equals(pt.name))
                        id = placeType.id;

                assertTrue(id != -1);

                pt.id = id;
            }

        // now i have array of places fully prepared to test
    }

    /**
     * Synchronous wrapper over clearing database
     */
    private void clearDB() {
        new CommandSyncer<Void>() {
            @Override
            public void exec() {
                mOps.clearDB(this);
            }
        }.doStuff();
    }

    /**
     * Synchronous wrapper over adding to database
     *
     * @param places
     */
    private void insertOrModifyToDB(final PlaceData[] places) {
        new CommandSyncer<Void>() {
            @Override
            public void exec() {
                mOps.place().addOrModify(places, this);
            }
        }.doStuff();
    }

    /**
     * Synchronous wrapper over querying the database
     */
    private PlaceData[] queryFromDB() {
        return new CommandSyncer<PlaceData[]>() {
            @Override
            public void exec() {
                mOps.place().queryList(this);
            }
        }.doStuff();
    }

    /**
     * Synchronous wrapper over adding PlaceTypes to database
     */
    private void writePlaceTypesToDB() {
        new CommandSyncer<Void>() {
            @Override
            public void exec() {
                mOps.placeType().addOrModify(mTypes, this);
            }
        }.doStuff();
    }

    /**
     * Synchronous wrapper over querying the database
     */
    private PlaceType[] queryPlaceTypesFromDB() {
        return new CommandSyncer<PlaceType[]>() {
            @Override
            public void exec() {
                mOps.placeType().queryList(this);
            }
        }.doStuff();
    }

    /**
     * Synchronous wrapper over deleting from database
     */
    private void deleteFromDB(final long id) {
        new CommandSyncer<Void>() {
            @Override
            public void exec() {
                mOps.place().delete(id, this);
            }
        }.doStuff();
    }

    /**
     * Synchronous getter of usage information for PlaceType
     */
    private PlaceType.Usage queryPlaceTypesUsage(final long id) {
        return new CommandSyncer<PlaceType.Usage>() {
            @Override
            public void exec() {
                mOps.placeType().queryUsageStatistics(id, this);
            }
        }.doStuff();
    }

}
