package activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import home.akanashin.shoppingreminder.R;
import operations.Operations;
import utils.async_stuff.AsyncOpCallback;
import utils.async_stuff.GenericActivity;
import utils.database.DatabaseHelper;
import utils.datatypes.PlaceData;
import utils.datatypes.PlaceType;


public class MainActivity extends GenericActivity<Operations> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, Operations.class);

        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    public void onBtnEditShopsClick(View v) {
        Intent intent = new Intent(this, ShowPlacesActivity.class);
        startActivity(intent);
    }

    public void onBtnEditPlaceTypesClick(View v) {
        Intent intent = new Intent(this, ListPlaceTypesActivity.class);
        startActivity(intent);
    }

    public void onBtnInitDBClick(View v) {
        //checkPlaceTypes();
    }


    private void checkPlaces(final PlaceType[] pTypes) {
        // here we fill places

        PlaceData[] places = new PlaceData[] {
                new PlaceData("P Green", 1.0, 1.0, new ArrayList<PlaceType>() {{ add(pTypes[0]);}}),
                new PlaceData("P Blue", 1.1, 1.3, new ArrayList<PlaceType>() {{ add(pTypes[2]);}}),
                new PlaceData("P Green + Red", 2.0, 1.0, new ArrayList<PlaceType>() {{ add(pTypes[0]);add(pTypes[1]);}})
        };

        getOps().place().addOrModify(places, new AsyncOpCallback<Void>() {
            @Override
            public void run(Void param) {
                // check result here

                getOps().place().query(0, new AsyncOpCallback<PlaceData[]>() {
                    @Override
                    public void run(PlaceData[] param) {
                        // validate
                        Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

    }
}

