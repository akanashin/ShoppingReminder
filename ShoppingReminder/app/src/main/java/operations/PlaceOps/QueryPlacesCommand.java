package operations.PlaceOps;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

import operations.Operations;
import utils.async_stuff.GenericAsyncOperation;
import utils.Commons;
import utils.async_stuff.AsyncOpCallback;
import utils.database.DatabaseContract;
import utils.database.Field;
import utils.datatypes.PlaceData;
import utils.datatypes.PlaceType;

/**
 * Created by akana_000 on 6/20/2015.
 */
public class QueryPlacesCommand
        extends GenericAsyncOperation<PlaceData[]>
{
    private Integer mUid = 0; // query all places by default

    public QueryPlacesCommand(Operations ops, AsyncOpCallback cb) { super(ops, cb); }
    public QueryPlacesCommand(Operations ops, Integer uid, AsyncOpCallback cb)
    {
        super(ops, cb);

        mUid = uid;
    }

    /**
     * Synchronously query for types of places in the provider.
     */
    @Override
    public PlaceData[] doOperation(ContentResolver cr) {
        // Reading View i have created for this purpose
        String[] projection = {
                DatabaseContract.View_Place.COLUMN_ID,    // Place ID
                DatabaseContract.View_Place.COLUMN_NAME,  // Place Name
                DatabaseContract.View_Place.COLUMN_LAT,   // Place Latitude
                DatabaseContract.View_Place.COLUMN_LONG,  // Place Longitude
                DatabaseContract.View_Place.COLUMN_TYPE_ID,    // Type ID
                DatabaseContract.View_Place.COLUMN_TYPE_NAME,  // Type Name
                DatabaseContract.View_Place.COLUMN_TYPE_COLOR  // Type Color
                };

        Uri targetUri = Commons.ContentProvider.URI_TABLE_PLACE;
        if (mUid > 0)
            targetUri = targetUri.buildUpon().appendPath("/" + mUid).build();

        Cursor cursor = cr.query(targetUri,
                projection,
                null,
                null,
                DatabaseContract.Table_PlaceType.COLUMN_NAME // ordered by name
                );

        // now i will get many rows for the same Place
        ArrayList<PlaceData> al = new ArrayList<>();
        if (cursor != null
                && cursor.moveToFirst())
        {
            // first: create fields
            Field
                    fID        =  new Field(cursor, DatabaseContract.View_Place.COLUMN_ID),
                    fName      =  new Field(cursor, DatabaseContract.View_Place.COLUMN_NAME),
                    fLat       =  new Field(cursor, DatabaseContract.View_Place.COLUMN_LAT),
                    fLong      =  new Field(cursor, DatabaseContract.View_Place.COLUMN_LONG),
                    fTypeID    =  new Field(cursor, DatabaseContract.View_Place.COLUMN_TYPE_ID),
                    fTypeName  =  new Field(cursor, DatabaseContract.View_Place.COLUMN_TYPE_NAME),
                    fTypeColor =  new Field(cursor, DatabaseContract.View_Place.COLUMN_TYPE_COLOR);

            // iterate over rows and read every row
            do {
                int id    = fID.getInteger();

                // now check if we need to read all Place data
                PlaceData place;
                if (al.size() > 0
                        && al.get(al.size() - 1).id == id)
                    place = al.get(al.size() - 1);
                else {
                    place = new PlaceData(id, fName.getString(), fLat.getDouble(), fLong.getDouble(), new ArrayList<PlaceType>());

                    al.add(place);
                }

                // fill types into place
                place.types.add(new PlaceType(fTypeID.getInteger(), fTypeName.getString(), fTypeColor.getInteger()));
            } while (cursor.moveToNext());
        } else
            Log.w(Commons.TAG, "Found nothing!");

        return al.toArray(new PlaceData[al.size()]);
    }
}
