package operations.PlaceOps;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import operations.Operations;
import utils.AsyncOperationBase;
import utils.Commons;
import utils.async_stuff.AsyncOpCallback;
import utils.database.DatabaseContract;
import utils.datatypes.PlaceData;
import utils.datatypes.PlaceType;

/**
 * Created by akana_000 on 6/20/2015.
 */
public class AddOrUpdatePlaceCommand
        extends AsyncOperationBase<Void> {

    private PlaceData mData; // data to store into DB

    public AddOrUpdatePlaceCommand(Operations ops, PlaceData data, AsyncOpCallback cb) {
        super(ops, cb);

        mData = data;
    }

    @Override
    public Void doOperation(ContentResolver cr) {

        // 1st: store place
        //  here we get UID of Place
        if (mData.id == 0) {
            // new place
            ContentValues cv = new ContentValues();

            cv.put(DatabaseContract.Table_Place.COLUMN_NAME, mData.name);
            cv.put(DatabaseContract.Table_Place.COLUMN_LAT,  mData.loc.latitude);
            cv.put(DatabaseContract.Table_Place.COLUMN_LONG, mData.loc.longitude);

            // after adding i get URI
            Uri row_uri = cr.insert(Commons.ContentProvider.URI_TABLE_PLACE, cv);

            // using URI to get ID of newly added place
            String[] projection = { DatabaseContract.View_Place.COLUMN_ID };
            Cursor cursor = cr.query(row_uri,
                    projection,
                    null, // construct WHERE
                    null,
                    DatabaseContract.Table_PlaceType.COLUMN_NAME // ordered by name
            );


        } else {
            // modifying existing one
        }
/*
        // 2nd: store types
        //  we already have Place ID, so now we remove old type links and create new ones

        ContentValues cv = new ContentValues();

        cv.put(DatabaseContract.Table_PlaceType.COLUMN_NAME, mData.name);
        cv.put(DatabaseContract.Table_PlaceType.COLUMN_COLOR, mData.color);

        if (mData.id > 0) {
            cr.update(Commons.ContentProvider.URI_TABLE_PLACE_TYPE,
                    cv,
                    DatabaseContract.Table_PlaceType.COLUMN_ID + "=" + String.format("%d", mData.id),
                    null
            );
        } else
            cr.insert(Commons.ContentProvider.URI_TABLE_PLACE_TYPE, cv);
*/
        return null;
    }
}
