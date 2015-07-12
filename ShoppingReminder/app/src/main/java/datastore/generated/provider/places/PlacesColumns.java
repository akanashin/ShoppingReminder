/**
 *  This class was generated by Android-contentprovider-generator software
 *       (https://github.com/BoD/android-contentprovider-generator)
 */
package datastore.generated.provider.places;

import android.net.Uri;
import android.provider.BaseColumns;

import datastore.generated.provider.DataProvider;

/**
 * Places
 */
public class PlacesColumns implements BaseColumns {
    public static final String TABLE_NAME = "places";
    public static final Uri CONTENT_URI = Uri.parse(DataProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    /**
     * Name of place
     */
    public static final String NAME = "name";

    /**
     * Location of the place (latitude)
     */
    public static final String LATITUDE = "latitude";

    /**
     * Location of the place (longitude)
     */
    public static final String LONGITUDE = "longitude";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            NAME,
            LATITUDE,
            LONGITUDE
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(NAME) || c.contains("." + NAME)) return true;
            if (c.equals(LATITUDE) || c.contains("." + LATITUDE)) return true;
            if (c.equals(LONGITUDE) || c.contains("." + LONGITUDE)) return true;
        }
        return false;
    }

}
