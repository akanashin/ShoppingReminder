package operations.PlaceTypeOps;

import android.content.ContentResolver;
import android.content.ContentValues;

import operations.Operations;
import utils.AsyncOperationBase;
import utils.Commons;
import utils.async_stuff.AsyncOpCallback;
import utils.database.DatabaseContract;
import utils.datatypes.PlaceType;

/**
 * Created by akana_000 on 6/20/2015.
 */
public class AddOrUpdatePlaceTypeCommand
        extends AsyncOperationBase<Void> {

    private PlaceType[] mData; // data to store into DB

    public AddOrUpdatePlaceTypeCommand(Operations ops, PlaceType[] data, AsyncOpCallback cb) {
        super(ops, cb);

        mData = data;
    }

    @Override
    public Void doOperation(ContentResolver cr) {
        // Perform a synchronous (blocking) operation on the
        // DataProvider.
        for (PlaceType pType : mData) {
            ContentValues cv = new ContentValues();

            cv.put(DatabaseContract.Table_PlaceType.COLUMN_NAME, pType.name);
            cv.put(DatabaseContract.Table_PlaceType.COLUMN_COLOR, pType.color);

            if (pType.id > 0) {
                cr.update(Commons.ContentProvider.URI_TABLE_PLACE_TYPE,
                        cv,
                        DatabaseContract.Table_PlaceType.COLUMN_ID + "=" + String.format("%d", pType.id),
                        null
                );
            } else
                cr.insert(Commons.ContentProvider.URI_TABLE_PLACE_TYPE, cv);
        }

        return null;
    }
}
