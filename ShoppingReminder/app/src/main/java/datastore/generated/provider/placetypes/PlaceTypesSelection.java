/**
 *  This class was generated by Android-contentprovider-generator software
 *       (https://github.com/BoD/android-contentprovider-generator)
 */
package datastore.generated.provider.placetypes;

import java.util.Date;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import datastore.generated.provider.base.AbstractSelection;

/**
 * Selection for the {@code place_types} table.
 */
public class PlaceTypesSelection extends AbstractSelection<PlaceTypesSelection> {
    @Override
    protected Uri baseUri() {
        return PlaceTypesColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @param sortOrder How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort
     *            order, which may be unordered.
     * @return A {@code PlaceTypesCursor} object, which is positioned before the first entry, or null.
     */
    public PlaceTypesCursor query(ContentResolver contentResolver, String[] projection, String sortOrder) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), sortOrder);
        if (cursor == null) return null;
        return new PlaceTypesCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null)}.
     */
    public PlaceTypesCursor query(ContentResolver contentResolver, String[] projection) {
        return query(contentResolver, projection, null);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null, null)}.
     */
    public PlaceTypesCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null, null);
    }


    public PlaceTypesSelection id(long... value) {
        addEquals("place_types." + PlaceTypesColumns._ID, toObjectArray(value));
        return this;
    }

    public PlaceTypesSelection note(String... value) {
        addEquals(PlaceTypesColumns.NOTE, value);
        return this;
    }

    public PlaceTypesSelection noteNot(String... value) {
        addNotEquals(PlaceTypesColumns.NOTE, value);
        return this;
    }

    public PlaceTypesSelection noteLike(String... value) {
        addLike(PlaceTypesColumns.NOTE, value);
        return this;
    }

    public PlaceTypesSelection noteContains(String... value) {
        addContains(PlaceTypesColumns.NOTE, value);
        return this;
    }

    public PlaceTypesSelection noteStartsWith(String... value) {
        addStartsWith(PlaceTypesColumns.NOTE, value);
        return this;
    }

    public PlaceTypesSelection noteEndsWith(String... value) {
        addEndsWith(PlaceTypesColumns.NOTE, value);
        return this;
    }

    public PlaceTypesSelection color(int... value) {
        addEquals(PlaceTypesColumns.COLOR, toObjectArray(value));
        return this;
    }

    public PlaceTypesSelection colorNot(int... value) {
        addNotEquals(PlaceTypesColumns.COLOR, toObjectArray(value));
        return this;
    }

    public PlaceTypesSelection colorGt(int value) {
        addGreaterThan(PlaceTypesColumns.COLOR, value);
        return this;
    }

    public PlaceTypesSelection colorGtEq(int value) {
        addGreaterThanOrEquals(PlaceTypesColumns.COLOR, value);
        return this;
    }

    public PlaceTypesSelection colorLt(int value) {
        addLessThan(PlaceTypesColumns.COLOR, value);
        return this;
    }

    public PlaceTypesSelection colorLtEq(int value) {
        addLessThanOrEquals(PlaceTypesColumns.COLOR, value);
        return this;
    }
}
