package home.akanashin.shoppingreminder.pages;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import home.akanashin.shoppingreminder.R;

/**
 * Created by akana_000 on 8/16/2015.
 */
public class TaskList extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_tasks, container, false);

        // set up buttons and stuff


        return view;
    }
}
