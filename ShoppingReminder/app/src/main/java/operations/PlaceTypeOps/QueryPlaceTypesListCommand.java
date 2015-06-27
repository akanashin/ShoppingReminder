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
import utils.database.Field;
import utils.datatypes.PlaceType;

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
            // prepare field readers
            Field
                    fID     = new Field(cursor, DatabaseContract.Table_PlaceType.COLUMN_ID),
                    fName   = new Field(cursor, DatabaseContract.Table_PlaceType.COLUMN_NAME),
                    fColor  =  new Field(cursor, DatabaseContract.Table_PlaceType.COLUMN_COLOR);

            // iterate over rows and read every row
            do {
                al.add(new PlaceType(
                        fID.getInteger(),
                        fName.getString(),
                        fColor.getInteger()
                ));
            } while (cursor.moveToNext());
        } else
            Log.w(Commons.TAG, "Found nothing!");

        return al.toArray(new PlaceType[0]);
    }
}
