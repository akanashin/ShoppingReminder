package utils.database;

import android.database.Cursor;

/**
 * This class is wrapper over Cursor to store its object and column index
 */
public class Field {
    private Cursor  mCursor;
    private Integer mColumnIndex;
    public Field(Cursor cursor, String columnName) {
        mColumnIndex = cursor.getColumnIndex(columnName);

        if (mColumnIndex < 0)
            throw new AssertionError("Column " + columnName + " not found");

        mCursor = cursor;
    }

    /*
     * Getters for various type
     */
    public String getString() {
        return mCursor.getString(mColumnIndex);
    }
    public Integer getInteger() {
        return mCursor.getInt(mColumnIndex);
    }
    public Double getDouble() {
        return mCursor.getDouble(mColumnIndex);
    }
}
