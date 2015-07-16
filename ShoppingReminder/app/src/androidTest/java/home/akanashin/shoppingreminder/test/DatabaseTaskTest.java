package home.akanashin.shoppingreminder.test;

import android.test.AndroidTestCase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import home.akanashin.shoppingreminder.test.utils.Utils;
import operations.Operations;
import operations.OpsException;
import operations.TaskOps;
import utils.CommandSyncer;
import utils.datatypes.PlaceData;
import utils.datatypes.PlaceType;
import utils.datatypes.Result;
import utils.datatypes.TaskData;

import static utils.Utils.compare;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing of database operations with PlaceType</a>
 */
public class DatabaseTaskTest extends AndroidTestCase {
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
                    new ArrayList<PlaceType>() {{ add(mTypes[0]); add(mTypes[2]); }} ),

            new PlaceData("shop", 1.5, 0.5,
                    new ArrayList<PlaceType>() {{ add(mTypes[1]); }} ),

            new PlaceData("xgarage", 2.5, 0.5,
                    new ArrayList<PlaceType>() )
    };

    private TaskData[] mTasks = new TaskData[] {
            new TaskData("Buy", "never", "",
                    null,
                    new ArrayList<PlaceData>() {{ add(mPlaces[2]); }}),

            new TaskData("Sell", "never", "SMALL REMARK",
                    new ArrayList<PlaceType>() {{ add(mTypes[0]); }},
                    null),

            new TaskData("Take", "never", "BIG REMARK",
                    null,
                    null),
    };

    private Operations mOps;
    private Utils<TaskData[], Void, TaskOps> mUtils;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mOps = new Operations();
        mOps.onConfiguration(true); // initialization of operations

        mUtils = new Utils<>(mOps.task());
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    // adding and checking
    public void testAdd() {
        prepare();

        // 1st: fill database
        mUtils.checkedInsertOrModify(mTasks.length, "", mTasks);

        // 2nd: query database
        TaskData[] data = mUtils.checkedQuery();

        // Tasks have only part of data
        assertTrue(Arrays.equals(mTasks, data));
    }

    /**
     *  Test for adding type which should produce errors
     */
    public void testAddException() {
        prepare();

        // 1st: fill database
        mUtils.checkedInsertOrModify(mTasks.length, "", mTasks);

        TaskData newTask = new TaskData(
                mTasks[0].name,
                mTasks[0].expiration,
                mTasks[0].remark,
                mTasks[0].types,
                mTasks[0].places);

        assertTrue(newTask.types == null);

        // 2rd: try to put 'empty name' into database
        newTask.name = "   ";
        mUtils.checkedInsertOrModify(
                null,
                OpsException.MSG_EMPTY_NAME,
                new TaskData[]{newTask} );

        // 3th: try to put 'places and types'
        newTask.name = "XXX";
        newTask.types = new ArrayList<>();
        mUtils.checkedInsertOrModify(
                null,
                OpsException.MSG_EITHER_PLACE_OR_TYPE,
                new TaskData[]{newTask} );

    }

    /**
     * Test for modifying existing element
     */
    public void testModify() {
        prepare();
/*
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
        */
    }

    /**
     * Test for deleting element
     */
    public void testDelete() {
        prepare();
/*
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
        */
    }

    /**
     * Test for Usage Statistics of PlaceType
     * (i do testing here because test needs PlaceData which is not present in DatabasePlaceTypeTest)
     * (i don't test number of tasks - it will be done in another test module)
     */
    public void testUsageStatisticsForPlaceType() {
        prepare();
/*
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
        */
    }

    /**
     * Prepare database
     */
    private void prepare() {
        mUtils.clearDB();

        // create table with PlaceTypes
        new CommandSyncer<Void>() {
            @Override
            public void exec() {
                mOps.placeType().addOrModify(mTypes, this);
            }
        }.doStuff();

        // read PlaceTypes and put proper IDs into my arrays
        PlaceType[] newTypes = new CommandSyncer<Result<PlaceType[]>>() {
            @Override
            public void exec() {
                mOps.placeType().queryList(this);
            }
        }.doStuff().result;

        assertTrue(Arrays.equals(mTypes, newTypes));
        for(int i = 0; i < mTypes.length; i++) {
            mTypes[i].id = newTypes[i].id;
        }

        // create table for places
        // create table with PlaceTypes
        new CommandSyncer<Void>() {
            @Override
            public void exec() {
                mOps.place().addOrModify(mPlaces, this);
            }
        }.doStuff();

        // read Places and put proper IDs into my arrays
        PlaceData[] newPlaces = new CommandSyncer<Result<PlaceData[]>>() {
            @Override
            public void exec() {
                mOps.place().queryList(this);
            }
        }.doStuff().result;

        assertTrue(Arrays.equals(mPlaces, newPlaces));
        for(int i = 0; i < mPlaces.length; i++)
            mPlaces[i].id = newPlaces[i].id;
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
