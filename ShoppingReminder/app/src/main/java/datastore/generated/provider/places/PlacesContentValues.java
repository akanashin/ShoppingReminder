/**
 *  This class was generated by Android-contentprovider-generator software
 *       (https://github.com/BoD/android-contentprovider-generator)
 */
package datastore.generated.provider.places;

import java.util.Date;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import datastore.generated.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code places} table.
 */
public class PlacesContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return PlacesColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable PlacesSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Name of place
     */
    public PlacesContentValues putName(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("name must not be null");
        mContentValues.put(PlacesColumns.NAME, value);
        return this;
    }


    /**
     * Location of the place (latitude)
     */
    public PlacesContentValues putLatitude(double value) {
        mContentValues.put(PlacesColumns.LATITUDE, value);
        return this;
    }


    /**
     * Location of the place (longitude)
     */
    public PlacesContentValues putLongitude(double value) {
        mContentValues.put(PlacesColumns.LONGITUDE, value);
        return this;
    }

}
