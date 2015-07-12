package home.akanashin.shoppingreminder.test;

import android.test.AndroidTestCase;

import operations.CommandSyncer;
import operations.Operations;
import utils.database.DatabaseHelper;
import utils.datatypes.PlaceType;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing of database operations with PlaceType</a>
 */
public class DatabasePlaceTypeTest extends AndroidTestCase {
    private DatabaseHelper mDB;

    private PlaceType[] mTypes = new PlaceType[]{
            new PlaceType("Blue",  0xFF0000FF),
            new PlaceType("Green", 0xFF00FF00),
            new PlaceType("Red",   0xFFFF0000),
    };

    private Operations mOps;

    @Override
    public void setUp() throws Exception {
        super.setUp();
//        mDB = new DatabaseHelper(mContext);
//        mDB.clearDB();

        mOps = new Operations();
        mOps.onConfiguration(true); // initialization of operations
    }

    @Override
    public void tearDown() throws Exception {
//        mDB.clearDB();
//        mDB.close();
        super.tearDown();
    }

    // adding and checking
    public void testAdd() {
        clearDB();

        // 1st: fill database
        insertOrModifyToDB(mTypes);

        // 2nd: query database
        PlaceType[] data = queryFromDB();

        // checking place types
        assertEquals(mTypes.length, data.length);

        for (int i = 0; i < mTypes.length; i++) {
            // check ID of param[i] (must be equal to i)
            assertFalse(data[i] == null);
            // do not test IDs: they WILL be different if database already existed
            //assertEquals(i+1, data[i].id);

            assertTrue(mTypes[i].equals(data[i]));
        }
    }

    /**
     * Test for modifying existing element
     */
    public void testModify() {
        clearDB();

        // 1st: fill database
        insertOrModifyToDB(mTypes);

        // 2nd: query database
        PlaceType[] data = queryFromDB();

        // Nb: i don't check whether i read data successfully or not
        //  this is done in another test

        // make one of elements different
        // Nb: database returns elements sorted by name so we don't want to change order
        data[1].color = data[1].color + 3;
        data[1].name  = data[1].name + " changed";

        // 3rd: modify data in database
        insertOrModifyToDB(new PlaceType[]{data[1]});

        // 4th: requery and validate data
        PlaceType[] newData = queryFromDB();
        assertEquals(data.length, newData.length);

        // all elements should be equal
        // Nb: database returns elements sorted by name so order should be the same
        for (int i = 0; i < data.length; i++) {
            assertEquals(data[i].id, newData[i].id);
            assertEquals(data[i].color, newData[i].color);
            assertEquals(data[i].name, newData[i].name);
        }
    }

    /**
     * Test for deleting element
     */
    public void testDelete() {
        clearDB();

        // 1st: fill database
        insertOrModifyToDB(mTypes);

        // 2nd: query database
        PlaceType[] data = queryFromDB();

        // Nb: i don't check whether i read data successfully or not
        //  this is done in another test
        long id    = data[1].id;

        // 3rd: delete from database
        deleteFromDB(id);

        // 4th: requery and validate data
        //  check that we haven't received element with ID we deleted
        data = queryFromDB();
        for(PlaceType pt: data)
            assertFalse(pt.id == id);
    }

    /**
     * Synchronous wrapper over adding to database
     *
     * @param places
     */
    private void insertOrModifyToDB(final PlaceType[] places) {
        new CommandSyncer<Void>() {
            @Override
            public void exec() {
                mOps.placeType().addOrModify(places, this);
            }
        }.doStuff();
    }

    /**
     * Synchronous wrapper over querying the database
     */
    private PlaceType[] queryFromDB() {
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
                mOps.placeType().delete(id, this);
            }
        }.doStuff();
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
}
