package operations.PlaceTypeOps;

import android.content.ContentResolver;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

import operations.Operations;
import utils.AsyncOperationBase;
import utils.Commons;
import utils.async_stuff.AsyncOpCallback;
import utils.database.DatabaseContract;
import utils.datatypes.PlaceType;

/**
 * Created by akana_000 on 6/20/2015.
 */
public class QueryPlaceTypesListCommand
        extends AsyncOperationBase<PlaceType[]>
{
    public QueryPlaceTypesListCommand(Operations ops, AsyncOpCallback cb) {
        super(ops, cb);
    }

    /**
     * Synchronously query for types of places in the provider.
     */
    @Override
    public PlaceType[] doOperation(ContentResolver cr) {
        String[] projection = {
                DatabaseContract.Table_PlaceType.COLUMN_ID,    // ID
                DatabaseContract.Table_PlaceType.COLUMN_NAME,  // Name
                DatabaseContract.Table_PlaceType.COLUMN_COLOR  // Color
                };

        Cursor cursor = cr.query(Commons.ContentProvider.URI_TABLE_PLACE_TYPE,
                projection,
                "", //all the records
                null, // return all the data
                DatabaseContract.Table_PlaceType.COLUMN_NAME // ordered by name
                );

        ArrayList<PlaceType> al = new ArrayList<>();
        if (cursor != null
                && cursor.moveToFirst()) {
            // iterate over rows and read every row
            do {
                PlaceType placeType = new PlaceType();
                placeType.id    = cursor.getInt(cursor.getColumnIndex(DatabaseContract.Table_PlaceType.COLUMN_ID));
                placeType.name  = cursor.getString(cursor.getColumnIndex(DatabaseContract.Table_PlaceType.COLUMN_NAME));
                placeType.color = cursor.getInt(cursor.getColumnIndex(DatabaseContract.Table_PlaceType.COLUMN_COLOR));

                al.add(placeType);
            } while (cursor.moveToNext());
        } else
            Log.w(Commons.TAG, "Found nothing!");

        return al.toArray(new PlaceType[0]);
    }
}
