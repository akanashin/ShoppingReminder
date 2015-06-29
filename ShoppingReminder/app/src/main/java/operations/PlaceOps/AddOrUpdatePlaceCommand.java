package operations.PlaceOps;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import operations.Operations;
import utils.async_stuff.GenericAsyncOperation;
import utils.Commons;
import utils.async_stuff.AsyncOpCallback;
import utils.database.DatabaseContract;
import utils.database.Field;
import utils.datatypes.PlaceData;

/**
 * Created by akana_000 on 6/20/2015.
 */
public class AddOrUpdatePlaceCommand
        extends GenericAsyncOperation<Void> {

    private PlaceData[] mData; // data to store into DB

    public AddOrUpdatePlaceCommand(Operations ops, PlaceData[] data, AsyncOpCallback cb) {
        super(ops, cb);

        mData = data;
    }

    @Override
    public Void doOperation(ContentResolver cr) {
        for (PlaceData place : mData) {
            ContentValues cv = new ContentValues();

            cv.put(DatabaseContract.Table_Place.COLUMN_NAME, place.name);
            cv.put(DatabaseContract.Table_Place.COLUMN_LAT,  place.loc.latitude);
            cv.put(DatabaseContract.Table_Place.COLUMN_LONG, place.loc.longitude);

            if (place.id == 0) {
                // new place
                // after adding i get URI (last part of this Uri is ID of newly added row)
                place.id = Integer.parseInt(
                        cr.insert(Commons.ContentProvider.URI_TABLE_PLACE, cv).getLastPathSegment()
                );
            } else {
                // modifying existing one
                int updated = cr.update(Commons.ContentProvider.URI_TABLE_PLACE,
                                        cv,
                                        DatabaseContract.Table_Place.COLUMN_ID + "=" + String.format("%d", place.id),
                                        null);
                if (updated != 1)
                    throw new AssertionError("ContentResolver::update returned " + updated  + " updated rows!");

                // delete place-type links (i will add all of them later)
                cr.delete(Commons.ContentProvider.URI_TABLE_PLACE_TYPE_LINK,
                          DatabaseContract.Table_PlaceTypeLink.COLUMN_PLACE_ID + "=" + String.format("%d", place.id),
                          null);
            }

            // set types for this place ID
            ContentValues[] ptCv = new ContentValues[place.types.size()];

            for (int i = 0; i < place.types.size(); i++) {
                ptCv[i] = new ContentValues();
                ptCv[i].put(DatabaseContract.Table_PlaceTypeLink.COLUMN_PLACE_ID, place.id);
                ptCv[i].put(DatabaseContract.Table_PlaceTypeLink.COLUMN_TYPE_ID, place.types.get(i).id);
            }

            if (cr.bulkInsert(Commons.ContentProvider.URI_TABLE_PLACE_TYPE_LINK, ptCv) != place.types.size())
                throw new AssertionError("Error inserting place-type links: number of inserted rows != number of types");
        }
        return null;
    }
}
