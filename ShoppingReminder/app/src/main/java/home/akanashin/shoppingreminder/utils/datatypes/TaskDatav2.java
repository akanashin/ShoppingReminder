package home.akanashin.shoppingreminder.utils.datatypes;

import android.graphics.Bitmap;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Data structure to
 */
public class TaskDatav2 {
    public long id;

    public String description;
    public Bitmap[] images;
    public Expiration expiration;
    public Placement placement;
    public Boolean enabled;

    public TaskDatav2() {}

    public TaskDatav2 setDescription(String text) {
        description = text;
        return this;
    }

    public TaskDatav2 setExpirationDate(DateTime date) {
        expiration = new Expiration();
        expiration.date = date.getMillis();
        return this;
    }

    public TaskDatav2 attachPlaces(List<Long> placeDataIds, boolean clear) {
        preparePlacement(true, clear);

        for (int i = 0; i < placeDataIds.size(); i++)
            placement.places.add(placeDataIds.get(i));

        return this;
    }

    public TaskDatav2 attachPlace(Long placeDataId, boolean clear) {
        preparePlacement(true, clear);

        placement.places.add(placeDataId);

        return this;
    }

    public TaskDatav2 attachPlaceTypes(List<Long> placeTypeIds, boolean clear) {
        preparePlacement(false, clear);

        for (int i = 0; i < placeTypeIds.size(); i++)
            placement.place_types.add(placeTypeIds.get(i));

        return this;
    }

    public TaskDatav2 attachPlaceType(Long placeTypeId, boolean clear) {
        preparePlacement(false, clear);

        placement.place_types.add(placeTypeId);

        return this;
    }

    public TaskDatav2 setEnabled(Boolean enabled) {
        this.enabled = enabled;

        return this;
    }

    // here we check that all data is valid and not empty
    public boolean validate() {
        return true;
    }

    public static class Expiration {
        public long date;
    }

    // one of
    public static class Placement {
        public List<Long> places;
        public List<Long> place_types;
    }

    private void preparePlacement(boolean isPlace, boolean clear) {
        if (placement == null) {
            placement = new Placement();
        } else {
            if (isPlace)
                placement.place_types = null;
            else
                placement.places = null;
        }

        if (isPlace) {
            if (placement.places == null || clear)
                placement.places = new ArrayList<>();
        } else {
            if (placement.place_types == null || clear)
                placement.place_types = new ArrayList<>();
        }
    }

}
