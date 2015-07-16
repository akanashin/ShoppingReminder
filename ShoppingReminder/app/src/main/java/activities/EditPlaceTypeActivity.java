package activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.margaritov.preference.colorpicker.ColorPickerDialog;

import home.akanashin.shoppingreminder.R;
import operations.Operations;
import utils.Commons;
import utils.Utils;
import utils.async_stuff.AsyncOpCallback;
import utils.async_stuff.GenericActivity;
import utils.datatypes.PlaceType;
import utils.datatypes.Result;


public class EditPlaceTypeActivity extends GenericActivity<Operations> {

    // these are constants for startActivityForResult
    public static final String INTENT_ID_NAME  = "Name";
    public static final String INTENT_ID_COLOR = "Color";
    public static final String INTENT_ID_UID   = "UID";
    public static final Integer INTENT_REQUEST_ID = 1;

    // these members will be filled in onCreate and used in onOkClick
    private EditText  mNameEditor;
    private ImageView mColorPicker;

    private PlaceType mPlaceType; // here is the storage for the data we edit

    private ColorPickerDialog mColorPicketDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, Operations.class);

        setContentView(R.layout.activity_edit_place_type);

        // read what we need to do
        // if UID = 0 this is creating new PlaceType
        //  else we edit existing one
        Intent intent = getIntent();
        mPlaceType = new PlaceType("", 0);

        mPlaceType.id = intent.getLongExtra(INTENT_ID_UID, -1);
        if (mPlaceType.id == -1)
            throw new AssertionError("No UID was bundled into Intent!");

        mNameEditor = (EditText) findViewById(R.id.et_name);
        mColorPicker = (ImageView) findViewById(R.id.iv_color);
        if (mNameEditor == null || mColorPicker == null)
            throw new AssertionError("Logical error: members 'EditText' and 'ImageView' are zero");

        if (mPlaceType.id == 0) {
            // creating new place type
            getSupportActionBar().setTitle("New place type");

            // hide statistics
            View g = findViewById(R.id.l_stat_data);
            g.setVisibility(View.INVISIBLE);

            // disable 'delete' button
            findViewById(R.id.btn_remove).setEnabled(false);

            mNameEditor.setText("New place type");

            mPlaceType.color = Commons.DEFAULT_MARKER_COLOR;
        } else {
            getSupportActionBar().setTitle("Edit place type");

            // editing already existed place type
            // set name
            String name = intent.getStringExtra(INTENT_ID_NAME);
            if (name == null || name.equals(""))
                throw new AssertionError("No Name was bundled into Intent!");

            mNameEditor.setText(name);

            // Nb: all colors will have negative values (they have 0xFF in higher byte)
            //  so it's better to check them against positive value
            mPlaceType.color = intent.getIntExtra(INTENT_ID_COLOR, 1);
            if (mPlaceType.color == 1)
                throw new AssertionError("No Color was bundled into Intent!");

            // these data only needed for filling statistics data
            final TextView tv_places = (TextView) findViewById(R.id.tv_n_places);
            final TextView tv_tasks = (TextView) findViewById(R.id.tv_n_tasks);
            final View btn_delete = findViewById(R.id.btn_remove);

            // request for statistics data
            getOps().placeType().queryUsageStatistics(mPlaceType.id,
                    new AsyncOpCallback<Result<PlaceType.Usage>>() {
                        @Override
                        public void run(Result<PlaceType.Usage> param) {
                            PlaceType.Usage usage = param.result;

                            if (usage == null) {
                                Utils.toast("Error reading from database: " + param.message);
                                return;
                            }

                            tv_places.setText("" + usage.n_places);
                            tv_tasks.setText("" + usage.n_tasks);

                            if (usage.n_places > 0 || usage.n_tasks > 0)
                                btn_delete.setEnabled(false);
                        }
                    });
        }

        // set color of marker
        mColorPicker.setColorFilter(mPlaceType.color, PorterDuff.Mode.MULTIPLY);

        // some hacks to show keyboard when user wants to edit the field
        mNameEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mNameEditor, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    // handler for changing color
    public void onColorIconClick(View v) {
        mColorPicketDialog = new ColorPickerDialog(EditPlaceTypeActivity.this, mPlaceType.color);
        mColorPicketDialog.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                mPlaceType.color = color;
                mColorPicker.setColorFilter(mPlaceType.color, PorterDuff.Mode.MULTIPLY);
            }
        });
        mColorPicketDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_place_type, menu);
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
        mPlaceType.name = mNameEditor.getText().toString();
        getOps().placeType().addOrModify(
                new PlaceType[] {mPlaceType},
                new AsyncOpCallback<Result<Integer>>() {
                    @Override
                    public void run(Result<Integer> v) {
                        if (v.result == null) {
                            Utils.toast(v.message);
                            return;
                        }

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
        getOps().placeType().delete(mPlaceType.id, new AsyncOpCallback<Result<Integer>>() {
            @Override
            public void run(Result<Integer> param) {
                if (param.result == null) {
                    Utils.toast("Error reading database: " + param.message);
                    setResult(RESULT_CANCELED);
                    finish();
                    return;
                }

                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
