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
import utils.database.Field;
import utils.datatypes.PlaceData;
import utils.datatypes.PlaceType;

/**
 * Created by akana_000 on 6/20/2015.
 */
public class AddOrUpdatePlaceCommand
        extends AsyncOperationBase<Void> {

    private PlaceData[] mData; // data to store into DB

    public AddOrUpdatePlaceCommand(Operations ops, PlaceData[] data, AsyncOpCallback cb) {
        super(ops, cb);

        mData = data;
    }

    @Override
    public Void doOperation(ContentResolver cr) {
        for (PlaceData place: mData) {
            if (place.id == 0) {
                // new place
                ContentValues cv = new ContentValues();

                cv.put(DatabaseContract.Table_Place.COLUMN_NAME, place.name);
                cv.put(DatabaseContract.Table_Place.COLUMN_LAT,  place.loc.latitude);
                cv.put(DatabaseContract.Table_Place.COLUMN_LONG, place.loc.longitude);

                // after adding i get URI (last part of this Uri is ID of newly added row)
                place.id = Integer.parseInt(
                        cr.insert(Commons.ContentProvider.URI_TABLE_PLACE, cv).getLastPathSegment()
                );
            } else {
                // modifying existing one
                // delete place-type links
            }

            // set types for this place ID
            ContentValues[] ptCv = new ContentValues[place.types.size()];

            for (int i = 0; i < place.types.size(); i++) {
                ptCv[i] = new ContentValues();
                ptCv[i].put(DatabaseContract.Table_PlaceTypeLink.COLUMN_PLACE_ID, place.id);
                ptCv[i].put(DatabaseContract.Table_PlaceTypeLink.COLUMN_TYPE_ID,  place.types.get(i).id);
            }

            if (cr.bulkInsert(Commons.ContentProvider.URI_TABLE_PLACE_TYPE_LINK, ptCv) != place.types.size())
                throw new AssertionError("Error inserting place-type links: number of inserted rows != number of types");
        }

        return null;
    }
}
