package activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.margaritov.preference.colorpicker.ColorPickerDialog;

import home.akanashin.shoppingreminder.R;
import operations.Operations;
import utils.Commons;
import utils.async_stuff.AsyncOpCallback;
import utils.async_stuff.GenericActivity;
import utils.datatypes.PlaceType;


public class EditPlaceTypeActivity extends GenericActivity<Operations> {

    // these are constants for startActivityForResult
    public static final String INTENT_ID_NAME  = "Name";
    public static final String INTENT_ID_COLOR = "Color";
    public static final String INTENT_ID_UID   = "UID";
    public static final Integer INTENT_REQUEST_ID = 1;

    // these members will be filled in onCreate and used in onOkClick
    private EditText mNameEditor;
    private ImageView mColorPicker;

    private Integer   mUid;   // this is used to  separate adding and modifying
    private Integer   mColor; // initial color value or updated color value

    private ColorPickerDialog mColorPicketDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, Operations.class);

        setContentView(R.layout.activity_edit_place_type);

        // read what we need to do
        // if UID = 0 this is creating new PlaceType
        //  else we edit existing one
        Intent intent = getIntent();
        mUid = intent.getIntExtra(INTENT_ID_UID, -1);
        if (mUid == -1)
            throw new AssertionError("No UID was bundled into Intent!");

        mNameEditor = (EditText) findViewById(R.id.et_name);
        mColorPicker = (ImageView) findViewById(R.id.iv_color);
        if (mNameEditor == null || mColorPicker == null)
            throw new AssertionError("Logical error: members 'EditText' and 'ImageView' are zero");

        if (mUid == 0) {
            // creating new place type
            getSupportActionBar().setTitle("New place type");

            // hide statistics
            View g = findViewById(R.id.l_stat_data);
            g.setVisibility(View.INVISIBLE);

            // disable 'delete' button
            findViewById(R.id.btn_remove).setEnabled(false);

            mNameEditor.setText("New place type");

            mColor = Commons.DEFAULT_MARKER_COLOR;
        } else {
            getSupportActionBar().setTitle("Edit place type");

            // editing already existed place type
            // set name
            String name = intent.getStringExtra(INTENT_ID_NAME);
            if (name == null || name.equals(""))
                throw new AssertionError("No Name was bundled into Intent!");

            mNameEditor.setText(name);

            // set color
            mColor = intent.getIntExtra(INTENT_ID_COLOR, -1);
            if (mColor == -1)
                throw new AssertionError("No Color was bundled into Intent!");

            // these data only needed for filling statistics data
            final TextView tv_places = (TextView) findViewById(R.id.tv_n_places);
            final TextView tv_tasks = (TextView) findViewById(R.id.tv_n_tasks);
            final View btn_delete = findViewById(R.id.btn_remove);

            // request for statistics data
            getOps().placeType().queryUsageStatistics(mUid, new AsyncOpCallback<PlaceType.Usage>() {
                @Override
                public void run(PlaceType.Usage param) {
                    tv_places.setText("" + param.n_places);
                    tv_tasks.setText("" + param.n_tasks);

                    if (param.n_places > 0 || param.n_tasks > 0)
                        btn_delete.setEnabled(false);
                }
            });
        }

        // set color of marker
        mColorPicker.setColorFilter(mColor, PorterDuff.Mode.MULTIPLY);

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
        mColorPicketDialog = new ColorPickerDialog(EditPlaceTypeActivity.this, mColor);
        mColorPicketDialog.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                mColor = color;
                mColorPicker.setColorFilter(mColor, PorterDuff.Mode.MULTIPLY);
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
        getOps().placeType().addOrModify(
                new PlaceType[] {new PlaceType(mUid, mNameEditor.getText().toString(), mColor)},
                new AsyncOpCallback<Void>() {
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
        getOps().placeType().delete(mUid, new AsyncOpCallback() {
            @Override
            public void run(Object param) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
