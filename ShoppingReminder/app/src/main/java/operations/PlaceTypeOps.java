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
import utils.datatypes.PlaceType;

/**
 * Created by akana_000 on 7/12/2015.
 */
public class PlaceTypeOps extends OpsInterface<PlaceType[], PlaceType.Usage> {

    @Override
    protected PlaceType[] doQuery(ContentResolver cr, long uid) {
        Log.d(Commons.TAG, "PlaceTypeOps.doQuery(" + uid + ") called");

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

    @Override
    protected Integer doAddOrModify(ContentResolver cr, PlaceType[] data) throws OpsException {
        Log.d(Commons.TAG, "PlaceTypeOps.doAddOrModify(" + data + ") called");

        //some checks
        // 1st: not empty name
        for (PlaceType placeType : data) {
            if (placeType.name == null
                    || placeType.name.trim().isEmpty())
                throw new OpsException(OpsException.MSG_EMPTY_NAME);
        }

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

    @Override
    protected Integer doDelete(ContentResolver cr, long uid) {
        Log.d(Commons.TAG, "PlaceTypeOps.doDelete(" + uid + ") called");

        PlaceTypesSelection where = new PlaceTypesSelection();
        if (uid != -1)
            where.id(uid);

        return where.delete(cr);
    }

    @Override
    protected PlaceType.Usage doQueryUsageStatistics(ContentResolver cr, long uid) {
        Log.d(Commons.TAG, "PlaceTypeOps.doQueryUsageStatistics(" + uid + ") requested");

        PlaceTypeLinkSelection sel = new PlaceTypeLinkSelection();
        PlaceTypeLinkCursor cursor = sel.placeTypeId(uid).query(cr);

        PlaceType.Usage result = new PlaceType.Usage();
        result.n_places = cursor.getCount();

        // ToDo: usage statistics for tasks

        return result;
    }
}
