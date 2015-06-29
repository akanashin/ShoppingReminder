package utils;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

import utils.Commons;
import utils.database.DatabaseContract;
import utils.database.DatabaseHelper;

public class MySQLDataProvider extends ContentProvider {
    private DatabaseHelper dbHelper;

    private static class URI {
        public static UriMatcher mMatcher = buildUriMatcher();

        // _ID - constants for specific ID
        public static final int PLACE = 1;
        public static final int PLACE_ID = 2;
        public static final int PLACE_TYPE = 3;
        public static final int PLACE_TYPE_ID = 4;
        public static final int PLACE_TYPE_LINK = 5;
        public static final int PLACE_TYPE_LINK_ID = 6;

        private static UriMatcher buildUriMatcher() {
            // Add default 'no match' result to matcher.
            final UriMatcher matcher =
                    new UriMatcher(UriMatcher.NO_MATCH);

            // Initialize the matcher with the URIs used to access each
            // table.
            matcher.addURI(Commons.ContentProvider.AUTHORITY,
                    DatabaseContract.Table_Place.TABLE_NAME,
                    PLACE);
            matcher.addURI(Commons.ContentProvider.AUTHORITY,
                    DatabaseContract.Table_Place.TABLE_NAME + "/#",
                    PLACE_ID);
            matcher.addURI(Commons.ContentProvider.AUTHORITY,
                    DatabaseContract.Table_PlaceType.TABLE_NAME,
                    PLACE_TYPE);
            matcher.addURI(Commons.ContentProvider.AUTHORITY,
                    DatabaseContract.Table_PlaceType.TABLE_NAME + "/#",
                    PLACE_TYPE_ID);
            matcher.addURI(Commons.ContentProvider.AUTHORITY,
                    DatabaseContract.Table_PlaceTypeLink.TABLE_NAME,
                    PLACE_TYPE_LINK);
            matcher.addURI(Commons.ContentProvider.AUTHORITY,
                    DatabaseContract.Table_PlaceTypeLink.TABLE_NAME + "/#",
                    PLACE_TYPE_LINK_ID);

            return matcher;
        }
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());

        return false;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // The table to perform the insert on.
        String table;

        switch (URI.mMatcher.match(uri)) {
            case URI.PLACE:
                table = DatabaseContract.Table_Place.TABLE_NAME;
                break;
            case URI.PLACE_TYPE:
                table = DatabaseContract.Table_PlaceType.TABLE_NAME;
                break;
            case URI.PLACE_TYPE_LINK:
                table = DatabaseContract.Table_PlaceTypeLink.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        return dbHelper.getWritableDatabase().delete(table, selection, selectionArgs);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // The table to perform the insert on.
        String table;

        // The Uri containing the inserted row's id that is returned
        // to the caller.
        Uri resultUri;

        switch (URI.mMatcher.match(uri)) {
            case URI.PLACE:
                table = DatabaseContract.Table_Place.TABLE_NAME;
                resultUri = Commons.ContentProvider.URI_TABLE_PLACE;
                break;
            case URI.PLACE_TYPE:
                table = DatabaseContract.Table_PlaceType.TABLE_NAME;
                resultUri = Commons.ContentProvider.URI_TABLE_PLACE_TYPE;
                break;
            case URI.PLACE_TYPE_LINK:
                table = DatabaseContract.Table_PlaceTypeLink.TABLE_NAME;
                resultUri = Commons.ContentProvider.URI_TABLE_PLACE_TYPE_LINK;
                break;
            default:
                throw new IllegalArgumentException("Unknown or illegal URI: " + uri);
        }

        // inserting! result is number of row inserted
        final long insertRow = dbHelper.getWritableDatabase().insert(table, "", values);

        // Check to ensure that the insertion worked.
        if (insertRow > 0) {
            // Create the result URI.
            Uri newUri = ContentUris.withAppendedId(resultUri, insertRow);

            // Register to watch a content URI for changes.
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        } else
            throw new SQLException("Fail to add a new record into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Create a SQLite query builder that will be modified based
        // on the Uri passed.
        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // here we set tables we will operate with
        switch (URI.mMatcher.match(uri)) {
            case URI.PLACE_ID:
                queryBuilder.appendWhere(DatabaseContract.View_Place.COLUMN_ID + "=" + uri.getLastPathSegment());
            case URI.PLACE:
                // using view instead of table
                queryBuilder.setTables(DatabaseContract.View_Place.TABLE_NAME);
                break;
            case URI.PLACE_TYPE:
                queryBuilder.setTables(DatabaseContract.Table_PlaceType.TABLE_NAME);
                break;
            case URI.PLACE_TYPE_LINK:
                queryBuilder.setTables(DatabaseContract.Table_PlaceTypeLink.TABLE_NAME);
                break;
            case URI.PLACE_TYPE_LINK_ID:
                throw new IllegalArgumentException("Querying URI " + uri + " does not make sense (it must be error)");
            default:
                throw new IllegalArgumentException("Unknown or illegal URI: " + uri);
        }

        final Cursor cursor =
                queryBuilder.query(db,
                        projection,
                        selection,
                        selectionArgs,
                        null,	// GROUP BY (not used)
                        null,	// HAVING   (not used)
                        sortOrder);

        // Register to watch a content URI for changes.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // The table to perform the insert on.
        String table;

        switch (URI.mMatcher.match(uri)) {
            case URI.PLACE_TYPE:
                table = DatabaseContract.Table_PlaceType.TABLE_NAME;
                break;
            case URI.PLACE:
                table = DatabaseContract.Table_Place.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        final int updated = dbHelper.getWritableDatabase().update(
                table,          // Table
                values,         // What to update
                selection,      // WHERE
                selectionArgs); //  args

        if (updated == 0)
            Log.w(Commons.TAG, "Updated 0 rows!");

        return updated;
    }

    /**
     * Get all table Details from the sqlite_master table in Db.
     *
     * @return An ArrayList of table details.
     */
    private ArrayList<String[]> getDbTableDetails(String table) {
        Cursor c = dbHelper.getWritableDatabase().rawQuery(
                "SELECT * FROM " + table + " LIMIT 1", null);
        ArrayList<String[]> result = new ArrayList<String[]>();
        int i = 0;
        result.add(c.getColumnNames());
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            String[] temp = new String[c.getColumnCount()];
            for (i = 0; i < temp.length; i++) {
                temp[i] = c.getString(i);
            }
            result.add(temp);
        }

        return result;
    }}
