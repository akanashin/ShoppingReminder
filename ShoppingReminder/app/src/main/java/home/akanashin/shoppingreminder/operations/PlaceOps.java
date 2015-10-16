package home.akanashin.shoppingreminder.operations;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import datastore.generated.provider.places.PlacesContentValues;
import datastore.generated.provider.places.PlacesCursor;
import datastore.generated.provider.places.PlacesSelection;
import datastore.generated.provider.placetypelink.PlaceTypeLinkContentValues;
import datastore.generated.provider.placetypelink.PlaceTypeLinkCursor;
import datastore.generated.provider.placetypelink.PlaceTypeLinkSelection;
import home.akanashin.shoppingreminder.utils.Commons;
import home.akanashin.shoppingreminder.utils.datatypes.PlaceData;
import home.akanashin.shoppingreminder.utils.datatypes.PlaceType;

/**
 * Created by akana_000 on 7/12/2015.
 */
public class PlaceOps extends OpsInterface<PlaceData[], Void> {

    @Override
    protected PlaceData[] doQuery(ContentResolver cr, long uid) {
        Log.d(Commons.TAG, "PlaceType.doQuery(" + uid + ") called");
        ArrayList<PlaceData> places = new ArrayList<>();

        // 1st: read all the places
        {
            PlacesSelection sel = new PlacesSelection();
            if (uid != -1)
                sel.id(uid);

            PlacesCursor cursor = sel.query(cr);
            while (cursor.moveToNext()) {
                places.add(new PlaceData(
                                cursor.getId(),
                                cursor.getName(),
                                cursor.getLatitude(),
                                cursor.getLongitude(),
                                new ArrayList<Long>() ));
            }
            cursor.close();
        }

        // 2nd: read typeIds for places
        {
            for (PlaceData place : places) {
                PlaceTypeLinkSelection sel = new PlaceTypeLinkSelection();
                sel.placeId(place.id);

                // ToDo: list of columns to query?
                PlaceTypeLinkCursor cursor = sel.query(cr);

                while (cursor.moveToNext()) {
                    // first - read type of place
                    PlaceType placeType = new PlaceType(
                            cursor.getPlaceTypeId(),
                            cursor.getPlaceTypesNote(),
                            cursor.getPlaceTypesColor());

                    // find object of PlaceData for which this type belongs
                    place.typeIds.add(placeType.id);
                }
            }
        }

        // sort resulting set by Name
        Collections.sort(places, new Comparator<PlaceData>() {
            @Override
            public int compare(PlaceData placeData, PlaceData t1) {
                return placeData.name.compareTo(t1.name);
            }
        });

        return places.toArray(new PlaceData[places.size()]);
    }

    @Override
    protected Integer doAddOrModify(ContentResolver cr, PlaceData[] data) throws OpsException {
        Log.d(Commons.TAG, "PlaceType.doAddOrModify(" + data + ") called");

        // First: some checks
        for (PlaceData place : data) {
            // 1st: not empty name
            if (place.name == null
                    || place.name.trim().isEmpty())
                throw new OpsException(OpsException.MSG_EMPTY_NAME);

            // 2nd: place nas ho typeIds
            //if (place.typeIds.isEmpty())
            //    throw new OpsException(OpsException.MSG_EMPTY_LIST_OF_TYPES);
        }

        // Now: processing
        for (PlaceData place : data) {
            PlacesContentValues place_cv = new PlacesContentValues();
            place_cv.putName(place.name);
            place_cv.putLatitude(place.loc.latitude);
            place_cv.putLongitude(place.loc.longitude);

            Boolean needToWriteLinks = false;
            if (place.id == 0) {
                // creating new record and store its ID
                Uri uri = place_cv.insert(cr);
                place.id = Long.parseLong(uri.getLastPathSegment());

                needToWriteLinks = true;
            } else {
                // updating the record

                // 1st - get old value of record
                PlaceData[] cur = doQuery(cr, place.id);
                if (cur.length != 1)
                    throw new AssertionError("Logical error: found " + cur.length + " records for ID=" + place.id);

                // 2nd: do i need to update record at all?
                if (cur[0].equals(place))
                    continue;

                // 3rd: do i need to update record data?
                if (!cur[0].name.equals(place.name) ||
                        !cur[0].loc.equals(place.loc))
                    place_cv.update(cr, new PlacesSelection().id(place.id));

                // 4th: do i need to update typeIds?
                if (!cur[0].typeIds.equals(place.typeIds)) {
                    // delete old PlaceTypeLink records (i will recreate them later)
                    PlaceTypeLinkSelection sel = new PlaceTypeLinkSelection();
                    sel.placeId(place.id).delete(cr);

                    needToWriteLinks = true;
                }
            }

            // now update PlaceTypeLink table (create all the records)
            if (needToWriteLinks)
                for (Long id : place.typeIds) {
                    PlaceTypeLinkContentValues cv = new PlaceTypeLinkContentValues();

                    cv.putPlaceId(place.id);
                    cv.putPlaceTypeId(id);
                    cv.insert(cr);
                }
        }

        return data.length;
    }

    @Override
    protected Integer doDelete(ContentResolver cr, long uid) {
        Log.d(Commons.TAG, "PlaceType.doDelete(" + uid + ") called");
        PlacesSelection where = new PlacesSelection();

        if (uid != -1)
            where = where.id(uid);

        return where.delete(cr);
    }

    @Override
    protected Void doQueryUsageStatistics(ContentResolver cr, long uid) {
        throw new UnsupportedOperationException("Not Implemented");
    }
}
