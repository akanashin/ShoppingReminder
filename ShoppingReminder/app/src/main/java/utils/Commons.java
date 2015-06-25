package utils;

import android.net.Uri;

import utils.database.DatabaseContract;

/**
 * Created by akana_000 on 6/19/2015.
 */
public class Commons {
    public static String TAG = "Shopping_Reminder";

    public static final Integer DEFAULT_MARKER_COLOR = 0xFFFF0000;
    /*
     * Here we place Uris for Content provider
     */
    public static class ContentProvider {
        public  static final String AUTHORITY = "home.akanashin.shoppingreminder";
        private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

        public  static Uri   URI_TABLE_PLACE =
                BASE_URI.buildUpon().appendPath(DatabaseContract.Table_Place.TABLE_NAME).build();
        public  static Uri   URI_TABLE_PLACE_TYPE =
                BASE_URI.buildUpon().appendPath(DatabaseContract.Table_PlaceType.TABLE_NAME).build();
        public  static Uri   URI_TABLE_PLACE_TYPE_LINK =
                BASE_URI.buildUpon().appendPath(DatabaseContract.Table_PlaceTypeLink.TABLE_NAME).build();
    }
}
