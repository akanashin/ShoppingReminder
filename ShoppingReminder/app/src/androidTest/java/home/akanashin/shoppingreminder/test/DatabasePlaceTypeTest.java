package home.akanashin.shoppingreminder.test;

import android.test.AndroidTestCase;

import home.akanashin.shoppingreminder.test.utils.Utils;
import operations.Operations;
import operations.PlaceTypeOps;
import utils.datatypes.PlaceType;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing of database operations with PlaceType</a>
 */
public class DatabasePlaceTypeTest extends AndroidTestCase {
    private PlaceType[] mTypes = new PlaceType[]{
            new PlaceType("Blue",  0xFF0000FF),
            new PlaceType("Green", 0xFF00FF00),
            new PlaceType("Red",   0xFFFF0000),
    };

    private Operations mOps;
    private Utils<PlaceType[], PlaceTypeOps> mUtils;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mOps = new Operations();
        mOps.onConfiguration(true); // initialization of operations

        mUtils = new Utils<>(mOps.placeType());
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test for adding and checking
     */
    public void testAdd() {
        mUtils.clearDB();

        // 1st: fill database
        mUtils.checkedInsertOrModify(mTypes.length, "", mTypes);

        // 2nd: query database
        PlaceType[] data = mUtils.checkedQuery();

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
     *  Test for adding type with already existing name
     *  (should produce error!)
     */
    public void testAddException() {
        mUtils.clearDB();

        // 1st: fill database
        mUtils.checkedInsertOrModify(mTypes.length, "", mTypes);

        // 2nd: try to put one more type into database
        PlaceType newType = new PlaceType(mTypes[0].name, mTypes[0].color + 10);
        mUtils.checkedInsertOrModify(null, "column note is not unique (code 19)", new PlaceType[]{newType});
    }

    /**
     * Test for modifying existing element
     */
    public void testModify() {
        mUtils.clearDB();

        // 1st: fill database
        mUtils.checkedInsertOrModify(mTypes.length, "", mTypes);

        // 2nd: query database
        PlaceType[] data = mUtils.checkedQuery();

        // Nb: i don't check whether i read data successfully or not
        //  this is done in another test

        // make one of elements different
        // Nb: database returns elements sorted by name so we don't want to change order
        data[1].color = data[1].color + 3;
        data[1].name  = data[1].name + " changed";

        // 3rd: modify data in database
        mUtils.checkedInsertOrModify(1, "", new PlaceType[]{data[1]});

        // 4th: requery and validate data
        PlaceType[] newData = mUtils.checkedQuery();
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
        mUtils.clearDB();

        // 1st: fill database
        mUtils.checkedInsertOrModify(mTypes.length, "", mTypes);

        // 2nd: query database
        PlaceType[] data = mUtils.checkedQuery();

        // Nb: i don't check whether i read data successfully or not
        //  this is done in another test
        long id    = data[1].id;

        // 3rd: delete from database
        mUtils.deleteFromDB(id);

        // 4th: requery and validate data
        //  check that we haven't received element with ID we deleted
        data = mUtils.checkedQuery();

        for(PlaceType pt: data)
            assertFalse(pt.id == id);
    }
}
