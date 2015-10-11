package home.akanashin.shoppingreminder.test;

import android.test.AndroidTestCase;

import home.akanashin.shoppingreminder.test.utils.Utils;
import home.akanashin.shoppingreminder.operations.Operations;
import home.akanashin.shoppingreminder.operations.OpsException;
import home.akanashin.shoppingreminder.operations.PlaceTypeOps;
import home.akanashin.shoppingreminder.utils.datatypes.PlaceType;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing of database home.akanashin.shoppingreminder.operations with PlaceType</a>
 */
public class DatabasePlaceTypeTest extends AndroidTestCase {
    private PlaceType[] mTypes = new PlaceType[]{
            new PlaceType("Blue",  0xFF0000FF),
            new PlaceType("Green", 0xFF00FF00),
            new PlaceType("Red",   0xFFFF0000),
    };

    private Operations mOps;
    private Utils<PlaceType[], PlaceType.Usage, PlaceTypeOps> mUtils;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mOps = new Operations();

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
     *  Test for adding type which should produce errors
     */
    public void testAddException() {
        mUtils.clearDB();

        // 1st: fill database
        mUtils.checkedInsertOrModify(mTypes.length, "", mTypes);

        // 2nd: try to put 'the same name' into database
        mUtils.checkedInsertOrModify(
                null,
                OpsException.MSG_PLACE_TYPE_NAME_IS_NOT_UNIQUE,
                new PlaceType[]{new PlaceType(mTypes[0].name, mTypes[0].color + 10)} );

        // 3rd: try to put 'empty name' into database
        mUtils.checkedInsertOrModify(
                null,
                OpsException.MSG_EMPTY_NAME,
                new PlaceType[]{new PlaceType("", mTypes[0].color + 10)} );
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
