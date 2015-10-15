package home.akanashin.shoppingreminder;

import android.app.FragmentManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import home.akanashin.shoppingreminder.navdrawer.NavigationDrawerFragment;
import home.akanashin.shoppingreminder.navdrawer.PagesManager;
import home.akanashin.shoppingreminder.utils.MyApp;
import home.akanashin.shoppingreminder.utils.async_stuff.AsyncOpCallback;

public class FrontActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PagesManager.initialize(this);

        setContentView(R.layout.activity_front);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // Init location manager
        MyApp.getInstance().getLocationRequester().connectToGoogleAPI(
                // once it is
                new AsyncOpCallback<Boolean>() {
                    @Override
                    public void doStuff(Boolean param) {
                        Location loc = null;
                        if (param)
                            loc = MyApp.getInstance().getLocationRequester().getCurrentLocation();

                        // it CAN happen
                        if (loc != null)
                            Log.i(MyApp.TAG, "Location manager is ready");
                        else
                            Log.w(MyApp.TAG, "Location is unavailable");
                    }
                },
                FrontActivity.this);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PagesManager.newFrame(position))
                .commit();

        mTitle = PagesManager.getPagesNames()[position];
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.front, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
}
