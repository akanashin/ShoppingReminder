package home.akanashin.shoppingreminder.pages;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import home.akanashin.shoppingreminder.R;
import home.akanashin.shoppingreminder.operations.Operations;
import home.akanashin.shoppingreminder.operations.OpsException;
import home.akanashin.shoppingreminder.utils.async_stuff.AsyncOpCallback;

/**
 * Created by akana_000 on 8/16/2015.
 */
public class About extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.page_about, container, false);

        v.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Operations ops = new Operations();
                try {
                    ops.initDB();
                    Toast.makeText(About.this.getActivity(), "Database ready", Toast.LENGTH_LONG).show();
                } catch (OpsException e) {
                    e.printStackTrace();
                    Toast.makeText(About.this.getActivity(), "Something bad happened", Toast.LENGTH_LONG).show();
                }
            }
        });
        return v;
    }
}
