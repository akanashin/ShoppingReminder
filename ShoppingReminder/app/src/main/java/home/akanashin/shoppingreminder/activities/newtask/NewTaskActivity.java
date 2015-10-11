package home.akanashin.shoppingreminder.activities.newtask;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import home.akanashin.shoppingreminder.R;
import home.akanashin.shoppingreminder.pages.NewTask;
import home.akanashin.shoppingreminder.utils.datatypes.TaskDatav2;

public class NewTaskActivity extends FragmentActivity {
    private static final int CAMERA_REQUEST = 1001;

    public static final String SOURCE = "SOURCE";

    public TaskDatav2 mNewTask = new TaskDatav2();

    private ViewPager mPager;
    private static final Integer NUM_ITEMS = 3;
    private Integer mCurrentPage = 1;

    public DataListener mDescriptionReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        // here we get intent with indicator how to get description for new task
        Intent intent = getIntent();

        // creating task object
        mNewTask.description = new TaskDatav2.Description();
        mNewTask.placement   = new TaskDatav2.Placement();
        mNewTask.expiration  = new TaskDatav2.Expiration();
        mNewTask.expiration.notification = new TaskDatav2.Expiration.Notification();

        mNewTask.description.dType = (TaskDatav2.Description.Type)intent.getSerializableExtra(SOURCE);

        // set up the activity
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter( new MyAdapter(getSupportFragmentManager(),this) );

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                // hide on-screen keyboard
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                ((Button)findViewById(R.id.btnBack)).setText( (position > 0) ? "Back" : "Cancel");
                ((Button)findViewById(R.id.btnNext)).setText( (position < NUM_ITEMS-1) ? "Next" : "Ok");
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        // buttons Back/Next
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPager.getCurrentItem() > 0)
                    mPager.setCurrentItem(mPager.getCurrentItem() - 1, true);
                else {
                    // ask and exit
                }
            }
        });
        findViewById(R.id.btnNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPager.getCurrentItem() < NUM_ITEMS-1)
                    mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
                else {
                    // ask and save new task, then exit
                    Toast.makeText(NewTaskActivity.this, mNewTask.description.text, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // skip first page when photo or voice is chosen
        if (mNewTask.description.dType != TaskDatav2.Description.Type.Text)
            mPager.setCurrentItem(mCurrentPage);
        else {
            // open description page and activate text editor
            mPager.setCurrentItem(mCurrentPage = 0);
        }
    }

    public void enterDescription(TaskDatav2.Description.Type source, final boolean stopIfCancel) {
        switch (source) {
            case Voice:
                break;
            case Photo:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                startActivityForResult(intent, CAMERA_REQUEST);
                break;
            default:
                throw new RuntimeException("Incorrect value of SOURCE " + source);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case CAMERA_REQUEST:
                try {
                    if (resultCode == RESULT_OK) {
                        // funky logic of getting photo result
                        if (data.getData() == null)
                            mNewTask.description.bitmap = (Bitmap) data.getExtras().get("data");
                        else
                            mNewTask.description.bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());

                        mDescriptionReceiver.update();

                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Image capture failed, advise user
                Toast.makeText(this, "Something happened. Try again?", Toast.LENGTH_LONG).show();

                // We have no image and nothing to do
                if (mNewTask.description.bitmap == null)
                    finish();

                break;
            default:
                throw new RuntimeException("Unknown request code " + requestCode);
        }
    }

    // here comes fragments!
    public interface DataListener {
        String VALUE="value";

        void update();
    }

    // this adapter creates fragments for view
    public class MyAdapter extends FragmentPagerAdapter {
        NewTaskActivity mParentActivity;

        public MyAdapter(FragmentManager fm, NewTaskActivity activity) {
            super(fm);
            mParentActivity = activity;
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            Bundle bundle;
            switch (position) {
                case 0:
                    fragment = new DescriptionFragment();
//                    bundle = new Bundle();
//                    bundle.putSerializable(DescriptionFragment.BUNDLE_MODE, mCurrentMode);
//                    fragment.setArguments(bundle);
                    return fragment;
                case 1:
                    fragment = new PlacementFragment();
//                    bundle = new Bundle();
//                    bundle.putString(INTENT_PRODUCT_INFO, mPriceInfoAsJSON);
//                    fragment.setArguments(bundle);
                    return fragment;
                case 2:
                    fragment = new ExpirationFragment();
//                    bundle = new Bundle();
//                    bundle.putString(INTENT_PRODUCT_INFO, mPriceInfoAsJSON);
//                    fragment.setArguments(bundle);
                    return fragment;
                default:
                    throw new RuntimeException("Incorrect number of page");
            }
        }
    }

}
