package home.akanashin.shoppingreminder.utils.datatypes;

import android.graphics.Bitmap;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Data structure to
 */
public class TaskData {
    public long id;

    public String description;
    public Bitmap[] images;
    public Expiration expiration;
    public Placement placement;
    public Boolean enabled;

    public TaskData() {}

    public TaskData setDescription(String text) {
        description = text;
        return this;
    }

    public TaskData setExpirationDate(DateTime date) {
        expiration = new Expiration();
        expiration.date = date.getMillis();
        return this;
    }

    public TaskData attachPlaces(List<Long> placeDataIds, boolean clear) {
        preparePlacement(true, clear);

        for (int i = 0; i < placeDataIds.size(); i++)
            placement.places.add(placeDataIds.get(i));

        return this;
    }

    public TaskData attachPlace(Long placeDataId, boolean clear) {
        preparePlacement(true, clear);

        placement.places.add(placeDataId);

        return this;
    }

    public TaskData attachPlaceTypes(List<Long> placeTypeIds, boolean clear) {
        preparePlacement(false, clear);

        for (int i = 0; i < placeTypeIds.size(); i++)
            placement.place_types.add(placeTypeIds.get(i));

        return this;
    }

    public TaskData attachPlaceType(Long placeTypeId, boolean clear) {
        preparePlacement(false, clear);

        placement.place_types.add(placeTypeId);

        return this;
    }

    public TaskData setEnabled(Boolean enabled) {
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
