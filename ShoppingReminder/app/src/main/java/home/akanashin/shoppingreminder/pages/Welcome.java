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
public class Welcome extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_about, container, false);
    }
}
