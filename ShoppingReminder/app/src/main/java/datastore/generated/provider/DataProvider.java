/**
 *  This class was generated by Android-contentprovider-generator software
 *       (https://github.com/BoD/android-contentprovider-generator)
 */
package datastore.generated.provider;

import java.util.Arrays;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import home.akanashin.shoppingreminder.BuildConfig;
import datastore.generated.provider.base.BaseContentProvider;
import datastore.generated.provider.placetypelink.PlaceTypeLinkColumns;
import datastore.generated.provider.placetypes.PlaceTypesColumns;
import datastore.generated.provider.places.PlacesColumns;

public class DataProvider extends BaseContentProvider {
    private static final String TAG = DataProvider.class.getSimpleName();

    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final String TYPE_CURSOR_ITEM = "vnd.android.cursor.item/";
    private static final String TYPE_CURSOR_DIR = "vnd.android.cursor.dir/";

    public static final String AUTHORITY = "home.akanashin.shoppingreminder";
    public static final String CONTENT_URI_BASE = "content://" + AUTHORITY;

    private static final int URI_TYPE_PLACE_TYPE_LINK = 0;
    private static final int URI_TYPE_PLACE_TYPE_LINK_ID = 1;

    private static final int URI_TYPE_PLACE_TYPES = 2;
    private static final int URI_TYPE_PLACE_TYPES_ID = 3;

    private static final int URI_TYPE_PLACES = 4;
    private static final int URI_TYPE_PLACES_ID = 5;



    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, PlaceTypeLinkColumns.TABLE_NAME, URI_TYPE_PLACE_TYPE_LINK);
        URI_MATCHER.addURI(AUTHORITY, PlaceTypeLinkColumns.TABLE_NAME + "/#", URI_TYPE_PLACE_TYPE_LINK_ID);
        URI_MATCHER.addURI(AUTHORITY, PlaceTypesColumns.TABLE_NAME, URI_TYPE_PLACE_TYPES);
        URI_MATCHER.addURI(AUTHORITY, PlaceTypesColumns.TABLE_NAME + "/#", URI_TYPE_PLACE_TYPES_ID);
        URI_MATCHER.addURI(AUTHORITY, PlacesColumns.TABLE_NAME, URI_TYPE_PLACES);
        URI_MATCHER.addURI(AUTHORITY, PlacesColumns.TABLE_NAME + "/#", URI_TYPE_PLACES_ID);
    }

    @Override
    protected SQLiteOpenHelper createSqLiteOpenHelper() {
        return MySQLiteOpenHelper.getInstance(getContext());
    }

    @Override
    protected boolean hasDebug() {
        return DEBUG;
    }

    @Override
    public String getType(Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case URI_TYPE_PLACE_TYPE_LINK:
                return TYPE_CURSOR_DIR + PlaceTypeLinkColumns.TABLE_NAME;
            case URI_TYPE_PLACE_TYPE_LINK_ID:
                return TYPE_CURSOR_ITEM + PlaceTypeLinkColumns.TABLE_NAME;

            case URI_TYPE_PLACE_TYPES:
                return TYPE_CURSOR_DIR + PlaceTypesColumns.TABLE_NAME;
            case URI_TYPE_PLACE_TYPES_ID:
                return TYPE_CURSOR_ITEM + PlaceTypesColumns.TABLE_NAME;

            case URI_TYPE_PLACES:
                return TYPE_CURSOR_DIR + PlacesColumns.TABLE_NAME;
            case URI_TYPE_PLACES_ID:
                return TYPE_CURSOR_ITEM + PlacesColumns.TABLE_NAME;

        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (DEBUG) Log.d(TAG, "insert uri=" + uri + " values=" + values);
        return super.insert(uri, values);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        if (DEBUG) Log.d(TAG, "bulkInsert uri=" + uri + " values.length=" + values.length);
        return super.bulkInsert(uri, values);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (DEBUG) Log.d(TAG, "update uri=" + uri + " values=" + values + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        return super.update(uri, values, selection, selectionArgs);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (DEBUG) Log.d(TAG, "delete uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        return super.delete(uri, selection, selectionArgs);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (DEBUG)
            Log.d(TAG, "query uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs) + " sortOrder=" + sortOrder
                    + " groupBy=" + uri.getQueryParameter(QUERY_GROUP_BY) + " having=" + uri.getQueryParameter(QUERY_HAVING) + " limit=" + uri.getQueryParameter(QUERY_LIMIT));
        return super.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected QueryParams getQueryParams(Uri uri, String selection, String[] projection) {
        QueryParams res = new QueryParams();
        String id = null;
        int matchedId = URI_MATCHER.match(uri);
        switch (matchedId) {
            case URI_TYPE_PLACE_TYPE_LINK:
            case URI_TYPE_PLACE_TYPE_LINK_ID:
                res.table = PlaceTypeLinkColumns.TABLE_NAME;
                res.idColumn = PlaceTypeLinkColumns._ID;
                res.tablesWithJoins = PlaceTypeLinkColumns.TABLE_NAME;
                if (PlacesColumns.hasColumns(projection)) {
                    res.tablesWithJoins += " LEFT OUTER JOIN " + PlacesColumns.TABLE_NAME + " AS " + PlaceTypeLinkColumns.PREFIX_PLACES + " ON " + PlaceTypeLinkColumns.TABLE_NAME + "." + PlaceTypeLinkColumns.PLACE_ID + "=" + PlaceTypeLinkColumns.PREFIX_PLACES + "." + PlacesColumns._ID;
                }
                if (PlaceTypesColumns.hasColumns(projection)) {
                    res.tablesWithJoins += " LEFT OUTER JOIN " + PlaceTypesColumns.TABLE_NAME + " AS " + PlaceTypeLinkColumns.PREFIX_PLACE_TYPES + " ON " + PlaceTypeLinkColumns.TABLE_NAME + "." + PlaceTypeLinkColumns.PLACE_TYPE_ID + "=" + PlaceTypeLinkColumns.PREFIX_PLACE_TYPES + "." + PlaceTypesColumns._ID;
                }
                res.orderBy = PlaceTypeLinkColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_PLACE_TYPES:
            case URI_TYPE_PLACE_TYPES_ID:
                res.table = PlaceTypesColumns.TABLE_NAME;
                res.idColumn = PlaceTypesColumns._ID;
                res.tablesWithJoins = PlaceTypesColumns.TABLE_NAME;
                res.orderBy = PlaceTypesColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_PLACES:
            case URI_TYPE_PLACES_ID:
                res.table = PlacesColumns.TABLE_NAME;
                res.idColumn = PlacesColumns._ID;
                res.tablesWithJoins = PlacesColumns.TABLE_NAME;
                res.orderBy = PlacesColumns.DEFAULT_ORDER;
                break;

            default:
                throw new IllegalArgumentException("The uri '" + uri + "' is not supported by this ContentProvider");
        }

        switch (matchedId) {
            case URI_TYPE_PLACE_TYPE_LINK_ID:
            case URI_TYPE_PLACE_TYPES_ID:
            case URI_TYPE_PLACES_ID:
                id = uri.getLastPathSegment();
        }
        if (id != null) {
            if (selection != null) {
                res.selection = res.table + "." + res.idColumn + "=" + id + " and (" + selection + ")";
            } else {
                res.selection = res.table + "." + res.idColumn + "=" + id;
            }
        } else {
            res.selection = selection;
        }
        return res;
    }
}
