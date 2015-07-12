package operations;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import datastore.generated.provider.places.PlacesContentValues;
import datastore.generated.provider.places.PlacesSelection;
import datastore.generated.provider.placetypelink.PlaceTypeLinkContentValues;
import datastore.generated.provider.placetypelink.PlaceTypeLinkCursor;
import datastore.generated.provider.placetypelink.PlaceTypeLinkSelection;
import utils.Commons;
import utils.async_stuff.AsyncOpCallback;
import utils.async_stuff.DatabaseOperation;
import utils.datatypes.PlaceData;
import utils.datatypes.PlaceType;

/**
 * Created by akana_000 on 7/12/2015.
 */
public class PlaceOps implements OpsInterface<PlaceData[]> {
    public void queryList(AsyncOpCallback cb) {
        Log.d(Commons.TAG, "PlaceOps.queryList requested");

        // -1 - for querying all the records
        query(-1, cb);
    }

    // get data about existing place
    public void query(final long uid, AsyncOpCallback cb) {
        Log.d(Commons.TAG, "PlaceOps.query requested");

        new DatabaseOperation<PlaceData[]>(cb) {
            @Override
            public PlaceData[] doOperation(ContentResolver cr) {
                return queryHelper(cr, uid);
            }
        }.run();
    }

    private PlaceData[] queryHelper(ContentResolver cr, long uid) {
        ArrayList<PlaceData> arr = new ArrayList<>();

        PlaceTypeLinkSelection sel = new PlaceTypeLinkSelection();
        if (uid != -1)
            sel.placeId(uid);

        PlaceTypeLinkCursor cursor = sel.query(cr);

        while (cursor.moveToNext()) {
            // first - read type of place
            PlaceType placeType = new PlaceType(
                    cursor.getPlaceTypeId(),
                    cursor.getPlaceTypesNote(),
                    cursor.getPlaceTypesColor());

            // find object of PlaceData for which this type belongs
            PlaceData placeData = null;
            for (PlaceData p : arr)
                if (p.id == cursor.getPlaceId())
                    placeData = p;

            if (placeData == null) {
                placeData = new PlaceData(
                        cursor.getPlaceId(),
                        cursor.getPlacesName(),
                        cursor.getPlacesLatitude(),
                        cursor.getPlacesLongitude(),
                        new ArrayList<PlaceType>()
                );

                arr.add(placeData);
            }

            placeData.types.add(placeType);
        }

        // sort resulting set by Name
        Collections.sort(arr, new Comparator<PlaceData>() {
            @Override
            public int compare(PlaceData placeData, PlaceData t1) {
                return placeData.name.compareTo(t1.name);
            }
        });

        return arr.toArray(new PlaceData[0]);
    }

    // delete place
    // uid == -1: delete all the places
    public void delete(final long uid, AsyncOpCallback cb) {
        Log.d(Commons.TAG, "PlaceOps.delete requested");

        new DatabaseOperation<Integer>(cb) {
            @Override
            public Integer doOperation(ContentResolver cr) {
                PlacesSelection where = new PlacesSelection();

                if (uid != -1)
                    where = where.id(uid);

                return where.delete(cr);
            }
        }.run();
    }

    // add new or modify existing place
    public void addOrModify(final PlaceData[] data, AsyncOpCallback cb) {
        Log.d(Commons.TAG, "PlaceType.addOrModify requested");

        new DatabaseOperation<Integer>(cb) {
            @Override
            public Integer doOperation(ContentResolver cr) {
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
                        PlaceData[] cur = queryHelper(cr, place.id);
                        if (cur.length != 1)
                            throw new AssertionError("Logical error: found " + cur.length + " records for ID=" + place.id);

                        // 2nd: do i need to update record at all?
                        if (cur[0].equals(place))
                            continue;

                        // 3rd: do i need to update record data?
                        if (!cur[0].name.equals(place.name) ||
                                !cur[0].loc.equals(place.loc))
                            place_cv.update(cr, new PlacesSelection().id(place.id));

                        // 4th: do i need to update types?
                        if (!cur[0].types.equals(place.types)) {
                            // delete old PlaceTypeLink records (i will recreate them later)
                            PlaceTypeLinkSelection sel = new PlaceTypeLinkSelection();
                            sel.placeId(place.id).delete(cr);

                            needToWriteLinks = true;
                        }
                    }

                    // now update PlaceTypeLink table (create all the records)
                    if (needToWriteLinks)
                        for (PlaceType pt : place.types) {
                            PlaceTypeLinkContentValues cv = new PlaceTypeLinkContentValues();

                            cv.putPlaceId(place.id);
                            cv.putPlaceTypeId(pt.id);
                            cv.insert(cr);
                        }
                }

                return data.length;
            }
        }.run();
    }
}
