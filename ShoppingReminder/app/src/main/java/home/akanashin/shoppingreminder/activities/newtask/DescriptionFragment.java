package home.akanashin.shoppingreminder.activities.newtask;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import home.akanashin.shoppingreminder.R;
import home.akanashin.shoppingreminder.utils.datatypes.TaskData;

/**
 * Created by akana_000 on 10/11/2015.
 */
// this fragment shows description
public class DescriptionFragment extends android.support.v4.app.Fragment
{
    private NewTaskActivity mParentActivity;
    private TaskData mNewTask;

    private View mView;

    private Boolean mFirstActivation = true;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            mParentActivity = (NewTaskActivity) context;
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
        mView.findViewById(R.id.text).setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus)
                            mNewTask.description = ((EditText) v).getText().toString();
                    }
                });

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mFirstActivation) {
            setFocusToEditText();
            mFirstActivation = false;
        }
    }

    public void setFocusToEditText() {
        if(mView.findViewById(R.id.text).requestFocus()) {
            mParentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
