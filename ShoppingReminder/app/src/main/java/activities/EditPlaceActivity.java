package activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import home.akanashin.shoppingreminder.R;
import operations.Operations;
import utils.ListViewAdapter;
import utils.async_stuff.AsyncOpCallback;
import utils.async_stuff.GenericActivity;
import utils.datatypes.PlaceData;
import utils.datatypes.PlaceType;

/*
 * Activity for Editing place (for new one or modified one)
 */
public class EditPlaceActivity
        extends GenericActivity<Operations>
{
    // these are constants for startActivityForResult
    public static final String INTENT_ID_UID   = "UID";
    public static final String INTENT_ID_LAT   = "Latitude";
    public static final String INTENT_ID_LONG  = "Longitude";
    public static final Integer INTENT_REQUEST_ID = 2;

    private Integer   mUid;
    private PlaceData mPlace; // will be filled from database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, Operations.class);

        setContentView(R.layout.activity_edit_place);

        // read what we need to do
        // if UID = 0 this is creating new PlaceType
        //  else we edit existing one
        Intent intent = getIntent();
        mUid = intent.getIntExtra(INTENT_ID_UID, -1);
        if (mUid == -1)
            throw new AssertionError("No UID was bundled into Intent!");

        if (mUid == 0) {
            // creating new place
            getSupportActionBar().setTitle("New place");

            // disable 'delete' button
            findViewById(R.id.btnDelete).setEnabled(false);

            mPlace = new PlaceData();
            mPlace.id = 0;
            mPlace.loc = new LatLng(intent.getDoubleExtra(INTENT_ID_LAT, 0),
                    intent.getDoubleExtra(INTENT_ID_LONG, 0));
            mPlace.types = new ArrayList<>();

            setupTypesListView();
        } else {
            // request place data from database
            getOps().place().query(mUid, new AsyncOpCallback<PlaceData>() {
                @Override
                public void run(PlaceData param) {
                    mPlace = param;

                    // put name into editor
                    EditText et = (EditText)EditPlaceActivity.this.findViewById(R.id.et_name);
                    et.setText(param.name);

                    // now - get list of types
                    setupTypesListView();
                }
            });

        }
    }

    private void setupTypesListView() {
        final ListView lv = (ListView) findViewById(R.id.lv_types);
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
                        //  if this type is in list then use its color
                        //   else - use grey
                        ImageView imageView = (ImageView) v.findViewById(R.id.iv_color);
                        if (mPlace.isOneOfTypes(data))
                            imageView.setColorFilter(data.color, PorterDuff.Mode.MULTIPLY);
                        else
                            imageView.setColorFilter(0x0FF808080, PorterDuff.Mode.MULTIPLY);
                    }
                });

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        EditPlaceActivity.this.switchTypeInPlace(data[i]);

                        // recreate item of the list
                        //View view = list.getChildAt(i-start);
                        lv.getAdapter().getView(i, view, lv);
                    }
                });

            }
        });
    }

    /*
     *  Check if this data is in type
     *   if not - add it
     *   if it is already - remove
     */
    public void switchTypeInPlace(PlaceType data) {
        for(int i = 0; i < mPlace.types.size(); i++)
            if (mPlace.types.get(i).id == data.id) {
                // type is in place, remove it and exit
                mPlace.types.remove(i);
                return;
            }

        // type was not found,add it
        mPlace.types.add(data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_place, menu);
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

    /*
     * Handler for button 'Ok'
     * Store newly created or edited item
     */
    public void onOkButtonClick(View v) {
        // store newly created data into database and notify caller activity

        // fill PlaceData with name
        mPlace.name = ((EditText)findViewById(R.id.et_name)).getText().toString();

        // place types are already in place

        getOps().place().addOrModify(mPlace, new AsyncOpCallback<Void>() {
            @Override
            public void run(Void v) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    /*
     * Handler for button 'Cancel'
     * Just close the activity
     */
    public void onCancelButtonClick(View v) {
        setResult(RESULT_CANCELED);
        finish();
    }

    /*
     * Handler for button 'Delete'
     * Send command to delete the item
     */
    public void onDeleteButtonClick(View v) {
        getOps().place().delete(mUid, new AsyncOpCallback() {
            @Override
            public void run(Object param) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

}
