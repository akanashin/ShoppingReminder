package home.akanashin.shoppingreminder.navdrawer;

import android.app.Fragment;
import android.content.Context;

import java.lang.reflect.Constructor;

import home.akanashin.shoppingreminder.R;
import home.akanashin.shoppingreminder.pages.About;
import home.akanashin.shoppingreminder.pages.MapWithTasks;
import home.akanashin.shoppingreminder.pages.NewTask;
import home.akanashin.shoppingreminder.pages.Settings;
import home.akanashin.shoppingreminder.pages.Welcome;

/**
 * Manager of pages in Navigation Drawer
 */
public class PagesManager {

    static class PageItem {
        public String   name;
        public Class    className;
        public Fragment object;

        public PageItem(String name, Class className) {
            this.name = name;
            this.className = className;
            this.object = null;
        }

        public Fragment getInstance() {
            if (object == null) {
                try {
                    Constructor constructor = className.getConstructor();

                    object = (Fragment) constructor.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Logical error: cannot create fragment");
                }
            }

            return object;
        }
    }

    private static PageItem[] mItems;

    public static void initialize(Context context) {
        mItems = new PageItem[] {
                new PageItem( context.getString(R.string.pagelist_page_welcome),       Welcome.class),
                new PageItem( context.getString(R.string.pagelist_page_nearest_tasks), MapWithTasks.class),
                new PageItem( context.getString(R.string.pagelist_page_new_task),      NewTask.class),
                new PageItem( context.getString(R.string.pagelist_page_settings),      Settings.class),
                new PageItem( context.getString(R.string.pagelist_page_about),         About.class),
        };

    }

    /**
     * Returns array of names in the list of pages
     * @return
     */
    public static String[] getPagesNames() {
        if (mItems == null)
            throw new RuntimeException("'initialize' must be called first");

        String[] result = new String[mItems.length];
        for (int i = 0; i < mItems.length; i++) {
            result[i] = mItems[i].name;
        }
        return result;
    }

    /**
     * Creates and returns fragment for given number
     * @param number
     * @return
     */
    public static Fragment newFrame(int number){
        if (mItems == null)
            throw new RuntimeException("'initialize' must be called first");

        if (number >= mItems.length)
            throw new RuntimeException("Logical error: number of item is too big");

        return mItems[number].getInstance();
    }
}
