package home.akanashin.shoppingreminder.utils.datatypes;

import android.graphics.Bitmap;
import android.util.EventLogTags;

import java.util.ArrayList;
import java.util.Date;

import static home.akanashin.shoppingreminder.utils.Utils.compare;

/**
 *  Data structure to
 */
public class TaskDatav2 {
    public long        id;

    public Description description;
    public Expiration  expiration;
    public Placement   placement;


    // one of fields
    public static class Description {
        public enum Type {
            Photo, Text, Voice
        };

        public Type        dType;
        public String      text;
        public Bitmap      bitmap;
        // public Something voice;
    }

    public static class Expiration {
        public Date date;
        public Notification notification;

        public static class Notification {
            public boolean enabled;
            public Date    whenToNotify;
        }
    }

    // one of
    public static class Placement {
        public int[] places;
        public int[] place_types;
    }
}
