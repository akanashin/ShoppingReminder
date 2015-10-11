package home.akanashin.shoppingreminder.activities.newtask;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import home.akanashin.shoppingreminder.R;
import home.akanashin.shoppingreminder.utils.datatypes.TaskDatav2;

/**
 * Created by akana_000 on 10/11/2015.
 */
// this fragment shows description
public class DescriptionFragment
    extends android.support.v4.app.Fragment
    implements NewTaskActivity.DataListener
{
    public static final String BUNDLE_MODE = "Mode";
    public static final String BUNDLE_VALUE = "Value";

    private NewTaskActivity mParentActivity;
    private TaskDatav2 mNewTask;

    private View mView;

    private Boolean mFirstActivation = true;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            mParentActivity = (NewTaskActivity) context;
            mParentActivity.mDescriptionReceiver = this;
            mNewTask = mParentActivity.mNewTask;
        } else
            throw new RuntimeException("Something went wrong");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (getArguments() == null)
//            throw new RuntimeException("Logical error: received empty bundle!");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // do not create view again
        if (mView != null)
            return mView;

        mView = inflater.inflate(R.layout.activity_new_task_description, container, false);

        // create sub-layout
        View subLayout = mView.findViewById(R.id.container);
        switch(mNewTask.description.dType) {
            case Text:
                ((LinearLayout)subLayout).addView(
                        inflater.inflate(R.layout.activity_new_task_description_text,
                                (ViewGroup) subLayout, false));

                mView.findViewById(R.id.text).setOnFocusChangeListener(
                        new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (!hasFocus)
                                    mNewTask.description.text = ((EditText) v).getText().toString();
                            }
                        });
                break;
            case Photo:
                ((LinearLayout)subLayout).addView(
                        inflater.inflate(R.layout.activity_new_task_description_photo,
                                (ViewGroup)subLayout, false));

                // set up the camera request
                mView.findViewById(R.id.ivPhoto).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParentActivity.enterDescription(TaskDatav2.Description.Type.Photo, false);
                    }
                });
                break;
            case Voice:
                ((LinearLayout)subLayout).addView(
                        inflater.inflate(R.layout.activity_new_task_description_voice,
                                (ViewGroup)subLayout, false));

                // set up the camera request
                mView.findViewById(R.id.ivVoice).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParentActivity.enterDescription(TaskDatav2.Description.Type.Voice, false);
                    }
                });
                break;
        }

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mFirstActivation) {
            if (mNewTask.description.dType == TaskDatav2.Description.Type.Text)
                setFocusToEditText();
            else
                // this is first time after start, execute stuff
                mParentActivity.enterDescription(mNewTask.description.dType, true);

            mFirstActivation = false;
        }
    }

    public void setFocusToEditText() {
        if(mView.findViewById(R.id.text).requestFocus()) {
            mParentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    /**
     * Update UI using refreshed data in our TaskDatav2 object
     */
    @Override
    public void update() {
        switch (mNewTask.description.dType) {
            case Photo:
                ((ImageView)mView.findViewById(R.id.ivPhoto)).setImageBitmap(mNewTask.description.bitmap);
                break;
            /*case Voice:
                break;*/
            default:
                throw new RuntimeException("Cannot be here with mode " + mNewTask.description.dType);
        }
    }

}
