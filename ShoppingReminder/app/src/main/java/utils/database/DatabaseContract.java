package utils.database;

/**
 * Created by akana_000 on 6/21/2015.
 */
public final class DatabaseContract {

    public static final String DATABASE_NAME = "DBName";
    public static final int DATABASE_VERSION = 5;

    // Information about Database
    // Table "Types of places"
    public static class Table_PlaceType {
        public static String TABLE_NAME   = "place_type";
        public static String COLUMN_ID    = "id";
        public static String COLUMN_NAME  = "name";
        public static String COLUMN_COLOR = "color";

        // SQL staff
        public static String SQL_CREATE = String.format(
                    "CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT not null, %s INT not null)",
                    TABLE_NAME, COLUMN_ID, COLUMN_NAME, COLUMN_COLOR
            );

        public static String SQL_DESTROY = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    // Table "Place"
    public static class Table_Place {
        public static String TABLE_NAME   = "place";
        public static String COLUMN_ID    = "id";
        public static String COLUMN_NAME  = "name";
        public static String COLUMN_LAT   = "lat";  // coordinates - Latitude
        public static String COLUMN_LONG  = "long"; // coordinates - Longitude

        // SQL staff
        public static String SQL_CREATE = String.format(
                "CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT not null, %s DOUBLE, %s DOUBLE)",
                TABLE_NAME, COLUMN_ID, COLUMN_NAME, COLUMN_LAT, COLUMN_LONG
        );

        public static String SQL_DESTROY = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    // Table "Place-Type link"
    public static class Table_PlaceTypeLink {
        public static String TABLE_NAME      = "place_type_link";
        public static String COLUMN_ID       = "id";
        public static String COLUMN_PLACE_ID = "place_id";
        public static String COLUMN_TYPE_ID  = "type_id";

        // SQL staff
        public static String SQL_CREATE = String.format(
                "CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s INTEGER, %s INTEGER)",
                TABLE_NAME, COLUMN_ID, COLUMN_PLACE_ID, COLUMN_TYPE_ID
        );

        public static String SQL_DESTROY = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    // View for viewing places
    public static class View_Place {
        public static String TABLE_NAME      = "place_view";
        public static String COLUMN_ID       = "id"; // ID of place
        public static String COLUMN_NAME     = "name";
        public static String COLUMN_LAT      = "lat";
        public static String COLUMN_LONG     = "long";

        // data for one of types attached to this place
        public static String COLUMN_TYPE_ID    = "type_id";
        public static String COLUMN_TYPE_NAME  = "type_name";
        public static String COLUMN_TYPE_COLOR = "type_color";

        // SQL staff
        public static String SQL_CREATE = String.format(
                "CREATE VIEW %s AS" +
                        " SELECT p.%s as %s, p.%s as %s, p.%s as %s, p.%s as %s, pl.%s as %s, pl.%s as %s, pl.%s as %s" +
                        " FROM %s AS p, %s AS pl, %s AS l" +
                        " WHERE p.%s=l.%s AND l.%s=pl.%s",
                TABLE_NAME,
                // SELECT and column names
                Table_Place.COLUMN_ID,     COLUMN_ID,
                Table_Place.COLUMN_NAME,   COLUMN_NAME,
                Table_Place.COLUMN_LAT,    COLUMN_LAT,
                Table_Place.COLUMN_LONG,   COLUMN_LONG,
                Table_PlaceType.COLUMN_ID,    COLUMN_TYPE_ID,
                Table_PlaceType.COLUMN_NAME,  COLUMN_TYPE_NAME,
                Table_PlaceType.COLUMN_COLOR, COLUMN_TYPE_COLOR,
                // FROM
                Table_Place.TABLE_NAME, Table_PlaceType.TABLE_NAME, Table_PlaceTypeLink.TABLE_NAME,
                // WHERE
                Table_Place.COLUMN_ID, Table_PlaceTypeLink.COLUMN_ID,
                Table_PlaceTypeLink.COLUMN_ID, Table_PlaceType.COLUMN_ID
        );

        public static String SQL_DESTROY = "DROP VIEW IF EXISTS " + TABLE_NAME;
    }

    // NB: Update these for each change in tables!
    public static String[] SQL_CREATES = {
            Table_PlaceType.SQL_CREATE,
            Table_Place.SQL_CREATE,
            Table_PlaceTypeLink.SQL_CREATE,
            View_Place.SQL_CREATE
    };
    public static String[] SQL_DESTROYS = {
            Table_PlaceType.SQL_DESTROY,
            Table_Place.SQL_DESTROY,
            Table_PlaceTypeLink.SQL_DESTROY,
            View_Place.SQL_DESTROY
    };
}
