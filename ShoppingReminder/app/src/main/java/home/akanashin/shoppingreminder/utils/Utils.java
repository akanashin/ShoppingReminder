package home.akanashin.shoppingreminder.utils;

import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Created by akana_000 on 6/27/2015.
 */
public class Utils {
    public static void toast(String text) {
        Toast.makeText(MyApp.getContext(), text, Toast.LENGTH_LONG).show();
    }

    public static class DateUtil
    {
        public static DateTime toLocal(DateTime utc) {
            return utc.withZone(DateTimeZone.forOffsetHours(+3));
        }

        public static String toString(Long millis) {
            return Utils.DateUtil.toLocal(new DateTime(millis)).toString("d-M-x H:m:s");
        }
    }

    /**
     * Comparator of two objects with protection from 'the same object' and 'one of them is null'
     * @param obj1
     * @param obj2
     * @return true - if obj1 and obj2 are the same object/they are both null/they are 'equal'
     */
    public static <T> boolean compare(T obj1, T obj2) {
        // special case: objects are the same
        //  (this check covers case 'null == null'
        if (obj1 == obj2)
            return true;

        // one or more of objects is not null
        if (obj1 == null || obj2 == null)
            return false;

        // now not obj1 nor obj2 is null
        return obj1.equals(obj2);
    }
}
