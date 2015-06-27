package utils.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Helper for database operations
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context,
                DatabaseContract.DATABASE_NAME,
                null, // SQLite... CursorFactory something
                DatabaseContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // database was just created. Execute SQL CREATEs for each table
        for(String cmd : DatabaseContract.SQL_CREATES)
            sqLiteDatabase.execSQL(cmd);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        // drop all tables
        for(String cmd : DatabaseContract.SQL_DESTROYS)
            sqLiteDatabase.execSQL(cmd);

        // recreate all the tables
        onCreate(sqLiteDatabase);
    }

    public void clearDB() {
        Log.w(DatabaseHelper.class.getName(), "Recreating database");

        // drop all tables
        for(String cmd : DatabaseContract.SQL_DESTROYS)
            getWritableDatabase().execSQL(cmd);

        // recreate all the tables
        onCreate(getWritableDatabase());
    }
}
