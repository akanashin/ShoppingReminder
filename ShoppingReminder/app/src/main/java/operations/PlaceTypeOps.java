package operations;

import android.content.ContentResolver;
import android.util.Log;

import java.util.ArrayList;

import datastore.generated.provider.placetypelink.PlaceTypeLinkCursor;
import datastore.generated.provider.placetypelink.PlaceTypeLinkSelection;
import datastore.generated.provider.placetypes.PlaceTypesContentValues;
import datastore.generated.provider.placetypes.PlaceTypesCursor;
import datastore.generated.provider.placetypes.PlaceTypesSelection;
import utils.Commons;
import utils.async_stuff.AsyncOpCallback;
import utils.async_stuff.DatabaseOperation;
import utils.datatypes.PlaceType;

/**
 * Created by akana_000 on 7/12/2015.
 */
public class PlaceTypeOps implements OpsInterface<PlaceType[]> {

    public void queryList(AsyncOpCallback cb) {
        Log.d(Commons.TAG, "PlaceTypeOps.queryList requested");

        new DatabaseOperation<PlaceType[]>(cb) {
            @Override
            public PlaceType[] doOperation(ContentResolver cr) {
                ArrayList<PlaceType> result = new ArrayList<>();
                PlaceTypesSelection where = new PlaceTypesSelection();
                PlaceTypesCursor placeTypeCursor = where.query(cr);

                while (placeTypeCursor.moveToNext()) {
                    result.add(new PlaceType(
                            placeTypeCursor.getId(),
                            placeTypeCursor.getNote(),
                            placeTypeCursor.getColor()));
                }

                return result.toArray(new PlaceType[0]);
            }
        }.run();
    }

    @Override
    public void query(long uid, AsyncOpCallback cb) {
        throw new AssertionError("Not implemented");
    }

    // searches for usage of a given place type
    public void queryUsageStatistics(final long uid, AsyncOpCallback cb) {
        Log.d(Commons.TAG, "PlaceTypeOps.queryUsageStatistics requested");

        new DatabaseOperation<PlaceType.Usage>(cb) {
            @Override
            public PlaceType.Usage doOperation(ContentResolver cr) {

                PlaceTypeLinkSelection sel = new PlaceTypeLinkSelection();
                PlaceTypeLinkCursor cursor = sel.placeTypeId(uid).query(cr);

                PlaceType.Usage result = new PlaceType.Usage();
                result.n_places = cursor.getCount();

                // ToDo: usage statistics for tasks

                return result;
            }
        }.run();
    }

    // add new place type
    public void addOrModify(final PlaceType[] data, AsyncOpCallback cb) {
        Log.d(Commons.TAG, "PlaceTypeOps.addOrModify requested");

        new DatabaseOperation<Integer>(cb) {
            @Override
            public Integer doOperation(ContentResolver cr) throws OpsException {
                //some checks
                // 1st: not empty name
                for (PlaceType placeType : data)
                    if (placeType.name == null
                        || placeType.name.trim().isEmpty())
                    throw new OpsException(OpsException.MSG_EMPTY_NAME);

                int updated = 0;
                for (PlaceType placeType : data) {
                    PlaceTypesContentValues cv = new PlaceTypesContentValues();
                    cv.putNote(placeType.name);
                    cv.putColor(placeType.color);

                    if (placeType.id == 0) {
                        cv.insert(cr);
                        updated++;
                    } else {
                        updated += cv.update(cr, new PlaceTypesSelection().id(placeType.id));
                    }
                }

                if (updated != data.length)
                    Log.w(Commons.TAG, "AddOrUpdate operation: not all data was properly processed!");

                return updated;
            }
        }.run();
    }

    // delete place type
    // uid == -1: delete all the place types
    public void delete(final long uid, AsyncOpCallback cb) {
        Log.d(Commons.TAG, "PlaceTypeOps.delete requested");

        new DatabaseOperation<Integer>(cb) {
            @Override
            public Integer doOperation(ContentResolver cr) {
                PlaceTypesSelection where = new PlaceTypesSelection();

                return where.id(uid).delete(cr);
            }
        }.run();
    }
}
