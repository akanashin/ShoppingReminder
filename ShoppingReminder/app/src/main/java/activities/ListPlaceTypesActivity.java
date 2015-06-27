package activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import home.akanashin.shoppingreminder.R;
import operations.Operations;
import utils.Commons;
import utils.ListViewAdapter;
import utils.async_stuff.AsyncOpCallback;
import utils.async_stuff.GenericActivity;
import utils.datatypes.PlaceType;


public class ListPlaceTypesActivity extends GenericActivity<Operations> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, Operations.class);

        setContentView(R.layout.activity_list_place_types);

        // reload types
        reloadPlaceTypes();
    }

    private void reloadPlaceTypes() {
        final ListView lv = (ListView) findViewById(R.id.lv_types);
        final ListPlaceTypesActivity me = this;

        getOps().placeType().queryList(new AsyncOpCallback<PlaceType[]>() {
            @Override
            public void run(final PlaceType[] data) {
                // set new data into ListView
                lv.setAdapter(new ListViewAdapter<PlaceType>(getBaseContext(), data, R.layout.list_item_place_type) {
                    @Override
                    protected void fillView(View v, PlaceType data) {
                        // set name
                        TextView textView = (TextView) v.findViewById(R.id.tv_name);
                        textView.setText(data.name);

                        // set color
                        ImageView imageView = (ImageView) v.findViewById(R.id.iv_color);
                        imageView.setColorFilter(data.color, PorterDuff.Mode.MULTIPLY);
                    }
                });

                // setup onClick handler for items
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        me.onListItemClick(data[i]);
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_place_types, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // not implemented now
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_add_new_place_type) {
            PlaceType placeType = new PlaceType(0, "", 0);
            AddOrEditPlaceType(placeType);

            return true;
        }
        if (id == R.id.action_delete_all_place_types) {
            // Ask 'are you sure?'
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            ListPlaceTypesActivity.this.getOps().placeType().delete(-1, new AsyncOpCallback() {
                                @Override
                                public void run(Object param) {
                                    Log.d(Commons.TAG, "Reloading items");
                                    reloadPlaceTypes();
                                }
                            });
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            return;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // start Edit activity for this placeType
    public void onListItemClick(PlaceType placeType) {
        AddOrEditPlaceType(placeType);
    }

    private void AddOrEditPlaceType(PlaceType placeType) {
        // Start Editing activity. When it finishes we will either reload data or just continue
        Intent intent = new Intent(this, EditPlaceTypeActivity.class);

        intent.putExtra(EditPlaceTypeActivity.INTENT_ID_NAME, placeType.name)
                .putExtra(EditPlaceTypeActivity.INTENT_ID_COLOR, placeType.color)
                .putExtra(EditPlaceTypeActivity.INTENT_ID_UID, placeType.id);
        startActivityForResult(intent, EditPlaceTypeActivity.INTENT_REQUEST_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == EditPlaceTypeActivity.INTENT_REQUEST_ID) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // There was something done to this placeType. Reload my list
                Log.d(Commons.TAG, "Reloading items");
                reloadPlaceTypes();
            }
        }
    }

}