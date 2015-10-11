package home.akanashin.shoppingreminder.test;

import android.test.AndroidTestCase;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import home.akanashin.shoppingreminder.test.utils.Utils;
import home.akanashin.shoppingreminder.operations.Operations;
import home.akanashin.shoppingreminder.operations.OpsException;
import home.akanashin.shoppingreminder.operations.PlaceOps;
import home.akanashin.shoppingreminder.utils.CommandSyncer;
import home.akanashin.shoppingreminder.utils.datatypes.PlaceData;
import home.akanashin.shoppingreminder.utils.datatypes.PlaceType;
import home.akanashin.shoppingreminder.utils.datatypes.Result;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing of database home.akanashin.shoppingreminder.operations with PlaceType</a>
 */
public class DatabasePlaceTest extends AndroidTestCase {
    // initial structure
    // Nb: all these need to have fixed IDs
    private PlaceType[] mTypes = new PlaceType[] {
            new PlaceType("Blue",  0xFF0000FF),
            new PlaceType("Green", 0xFF00FF00),
            new PlaceType("Red",   0xFFFF0000),
            new PlaceType("White", 0xFFFFFFFF),
    };

    private PlaceData[] mPlaces = new PlaceData[] {
            new PlaceData("home", 1.0, 1.0,
                    new ArrayList<PlaceType>() {{ add(mTypes[0]);
                                                  add(mTypes[2]); }} ),
            new PlaceData("office", 0.5, 0.5,
                    new ArrayList<PlaceType>() {{ add(mTypes[0]); }} ),

            new PlaceData("shop", 1.5, 0.5,
                    new ArrayList<PlaceType>() {{ add(mTypes[1]); }} ),

            new PlaceData("xgarage", 2.5, 0.5,
                    new ArrayList<PlaceType>() )

    };

    private Operations mOps;
    private Utils<PlaceData[], Void, PlaceOps> mUtils;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mOps = new Operations();

        mUtils = new Utils<>(mOps.place());
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    // adding and checking
    public void testAdd() {
        prepare();

        // 1st: fill database
        mUtils.checkedInsertOrModify(mPlaces.length, "", mPlaces);

        // 2nd: query database
        PlaceData[] data = mUtils.checkedQuery();

        // checking place types
        assertEquals(mPlaces.length, data.length);

        for (int i = 0; i < mPlaces.length; i++) {
            assertFalse(data[i] == null);

            assertTrue(mPlaces[i].equals(data[i]));
        }
    }

    /**
     *  Test for adding type which should produce errors
     */
    public void testAddException() {
        prepare();

        // 1st: fill database
        mUtils.checkedInsertOrModify(mPlaces.length, "", mPlaces);

        PlaceData newPlace = new PlaceData(
                mPlaces[0].name,
                mPlaces[0].loc.latitude,
                mPlaces[0].loc.longitude,
                mPlaces[0].types);

        // 2nd: try to put 'the same name' into database
        mUtils.checkedInsertOrModify(
                null,
                OpsException.MSG_PLACE_NAME_IS_NOT_UNIQUE,
                new PlaceData[]{newPlace} );

        // 3rd: try to put 'empty name' into database
        newPlace.name = "   ";
        mUtils.checkedInsertOrModify(
                null,
                OpsException.MSG_EMPTY_NAME,
                new PlaceData[]{newPlace} );

        // 4th: try to put 'empty list of types' into database
        /*
        newPlace.name = "XXX";
        newPlace.types.clear();
        mUtils.checkedInsertOrModify(
                null,
                OpsException.MSG_EMPTY_LIST_OF_TYPES,
                new PlaceData[]{newPlace} );
                */
    }

    /**
     * Test for modifying existing element
     */
    public void testModify() {
        prepare();

        // 1st: fill database
        mUtils.checkedInsertOrModify(mPlaces.length, "", mPlaces);

        // 2nd: query database
        PlaceData[] data = mUtils.checkedQuery();
        assertEquals(data.length, mPlaces.length);

        assertTrue(data.length > 2); // i will change 2 elements
        assertTrue(data[0].types.size() >= 1); // i will delete one placeType and add another

        // 3rd: change some of places
        data[0].types.remove(0);      // remove 1st placetype (result should be 2)
        data[0].types.add(mTypes[1]); // add element index 1 (result 2,1)

        data[1].name = data[1].name + " changed";
        data[1].loc  = new LatLng(data[1].loc.latitude, -5);

        // 4rd: write modified values to database
        mUtils.checkedInsertOrModify(2, "", new PlaceData[] {data[0], data[1]});

        // 5th: requery new data from database
        PlaceData[] newdata = mUtils.checkedQuery();
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
        mUtils.checkedInsertOrModify(mPlaces.length, "", mPlaces);

        // 2nd: remove one of places
        final int deletedIndex = 1;
        assertTrue(mPlaces.length >= deletedIndex + 1); // i will delete element 2 (index 1)
        mUtils.verifyResult(1, "", mUtils.deleteFromDB(mPlaces[deletedIndex].id));

        // 3rd: requery new data from database
        PlaceData[] data = mUtils.checkedQuery();

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
        Result<PlaceType.Usage> usage = queryPlaceTypesUsage(mTypes[1].id);
        assertEquals(0, usage.result.n_places);

        // 2st: fill database with some data
        mUtils.checkedInsertOrModify(mPlaces.length, "", mPlaces);

        // 3rd: check usage statistics - it should be <something>
        usage = queryPlaceTypesUsage(mTypes[1].id);
        int counter = 0;
        for(PlaceData placeData: mPlaces)
            for(PlaceType placeType: placeData.types)
                if (placeType.id == mTypes[1].id)
                    counter++;

        assertEquals(counter, usage.result.n_places);
    }

    /**
     * Prepare database
     */
    private void prepare() {
        mUtils.clearDB();

        // create table with PlaceTypes
        writePlaceTypesToDB();

        // read PlaceTypes and fix records for Places
        //  NB: i don't check whether data was read correctly or not
        Result<PlaceType[]> newTypes = queryPlaceTypesFromDB();

        for(PlaceData place: mPlaces)
            for(PlaceType pt: place.types) {
                // set ID based on name
                long id = -1;
                for(PlaceType placeType: newTypes.result)
                    if (placeType.name.equals(pt.name))
                        id = placeType.id;

                assertTrue(id != -1);

                pt.id = id;
            }

        // now i have array of places fully prepared to test
    }

    /**
     *  Stuff for PlaceType
     *   I don't check it because it is checked in another test
     */

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
    private Result<PlaceType[]> queryPlaceTypesFromDB() {
        return new CommandSyncer<Result<PlaceType[]>>() {
            @Override
            public void exec() {
                mOps.placeType().queryList(this);
            }
        }.doStuff();
    }

    /**
     * Synchronous getter of usage information for PlaceType
     */
    private Result<PlaceType.Usage> queryPlaceTypesUsage(final long id) {
        return new CommandSyncer<Result<PlaceType.Usage>>() {
            @Override
            public void exec() {
                mOps.placeType().queryUsageStatistics(id, this);
            }
        }.doStuff();
    }

}
