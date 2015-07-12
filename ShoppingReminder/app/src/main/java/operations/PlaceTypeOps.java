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
import utils.async_stuff.GenericAsyncOperation;
import utils.datatypes.PlaceType;
import utils.datatypes.Tuple;

/**
 * Created by akana_000 on 7/12/2015.
 */
public class PlaceTypeOps {

    public void queryList(AsyncOpCallback cb) {
        Log.d(Commons.TAG, "PlaceTypeOps.queryList requested");

        new GenericAsyncOperation<PlaceType[]>(cb) {
            @Override
            public PlaceType[] doOperation(ContentResolver cr) {
                PlaceTypesSelection where = new PlaceTypesSelection();
                PlaceTypesCursor placeTypeCursor = where.query(cr);

                ArrayList<PlaceType> al = new ArrayList<>();
                while(placeTypeCursor.moveToNext()) {
                    al.add(new PlaceType(
                            placeTypeCursor.getId(),
                            placeTypeCursor.getNote(),
                            placeTypeCursor.getColor()));
                }

                if (al.isEmpty())
                    Log.w(Commons.TAG, "Found nothing!");

                return al.toArray(new PlaceType[0]);
            }
        }.run();
    }

    // searches for usage of a given place type
    public void queryUsageStatistics(final long uid, AsyncOpCallback cb) {
        Log.d(Commons.TAG, "PlaceTypeOps.queryUsageStatistics requested");

        new GenericAsyncOperation<PlaceType.Usage>(cb) {
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

        new GenericAsyncOperation<Void>(cb) {
            @Override
            public Void doOperation(ContentResolver cr) {
                for(PlaceType placeType: data) {
                    PlaceTypesContentValues cv = new PlaceTypesContentValues();
                    cv.putNote(placeType.name);
                    cv.putColor(placeType.color);

                    if(placeType.id == 0)
                        cv.insert(cr);
                    else
                        cv.update(cr, new PlaceTypesSelection().id(placeType.id));
                }

                return null;
            }
        }.run();
    }

    // delete place type
    // uid == -1: delete all the place types
    public void delete(final long uid, AsyncOpCallback cb) {
        Log.d(Commons.TAG, "PlaceTypeOps.delete requested");

        new GenericAsyncOperation<Void>(cb) {
            @Override
            public Void doOperation(ContentResolver cr) {
                PlaceTypesSelection where = new PlaceTypesSelection();
                where.id(uid).delete(cr);

                return null;
            }
        }.run();
    }
}
