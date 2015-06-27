package home.akanashin.shoppingreminder.test;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.ApplicationTestCase;
import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;

import java.util.concurrent.CountDownLatch;

import operations.Operations;
import utils.async_stuff.AsyncOpCallback;
import utils.database.DatabaseHelper;
import utils.datatypes.PlaceType;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing of database operations with PlaceType</a>
 */
public class DatabasePlaceTypeTest extends AndroidTestCase {
    private DatabaseHelper db;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        db = new DatabaseHelper(mContext);
        db.clearDB();
        //db.getWritableDatabase();
    }

    @Override
    public void tearDown() throws Exception {
        db.clearDB();
        db.close();
        super.tearDown();
    }

    public void test() {
        //new DatabaseHelper(mContext).clearDB();

        final CountDownLatch signal = new CountDownLatch(1);

        final Operations ops = new Operations();
        ops.onConfiguration(true); // initialization

        final PlaceType[] pTypes = new PlaceType[]{
                new PlaceType("Blue",  0xFF0000FF),
                new PlaceType("Green", 0xFF00FF00),
                new PlaceType("Red",   0xFFFF0000),
        };

        // 2nd: fill database
        ops.placeType().addOrModify(pTypes, new AsyncOpCallback<Void>() {
            @Override
            public void run(Void param) {
                // first we need to query place types (to get IDs)
                ops.placeType().queryList(new AsyncOpCallback<PlaceType[]>() {
                    @Override
                    public void run(PlaceType[] param) {
                        // checking place types
                        assertEquals (pTypes.length, param.length);

                        for (int i = 0; i < pTypes.length; i++) {
                            // check ID of param[i] (must be equal to i)
                            assertFalse (param[i] == null);
                            assertEquals (param[i].id, i+1);

                            assertTrue (pTypes[i].equals(param[i]));
                        }

                        signal.countDown();
                    }
                });
            }
        });

        try {
            signal.await();// wait for callback
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
