package home.akanashin.shoppingreminder.pages;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import home.akanashin.shoppingreminder.R;
import home.akanashin.shoppingreminder.activities.newtask.NewTaskActivity;
import home.akanashin.shoppingreminder.utils.datatypes.TaskDatav2;

/**
 * Launcher for NewTaskActivity
 */
public class NewTask extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.page_new_task, container, false);

        // set proper handlers for the buttons
        v.findViewById(R.id.btnPhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callNextActivity(TaskDatav2.Description.Type.Photo);
            }
        });
        v.findViewById(R.id.btnText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callNextActivity(TaskDatav2.Description.Type.Text);
            }
        });
        v.findViewById(R.id.btnVoice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //callNextActivity(NewTaskActivity.Source.Voice);
                Toast.makeText(getActivity(), "not implemented", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    private void callNextActivity(TaskDatav2.Description.Type mode) {
        Intent intent = new Intent(this.getActivity(), NewTaskActivity.class);
        intent.putExtra(NewTaskActivity.SOURCE, mode);

        startActivity(intent);
    }
}