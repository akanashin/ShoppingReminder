package home.akanashin.shoppingreminder.activities.newtask;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import home.akanashin.shoppingreminder.R;
import home.akanashin.shoppingreminder.operations.Operations;
import home.akanashin.shoppingreminder.operations.OpsException;
import home.akanashin.shoppingreminder.utils.MyApp;
import home.akanashin.shoppingreminder.utils.datatypes.PlaceData;
import home.akanashin.shoppingreminder.utils.datatypes.PlaceType;
import home.akanashin.shoppingreminder.utils.datatypes.TaskData;

/**
 * Created by akana_000 on 10/11/2015.
 */
// this fragment shows placement settings
public class PlacementFragment extends android.support.v4.app.Fragment {
    private NewTaskActivity mParentActivity;
    private TaskData mNewTask;

    // data from database
    private PlaceData[] mPlaces;
    private PlaceType[] mPlaceTypes;

    // array of checked places or typeIds of places for this task
    //  only one type is allowed
    private boolean[] mLocationsMask;

    private View mView;
    private Spinner mPlacementSpinner;
    private ListView mList;

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

        // read data from database
        Operations ops = new Operations();
        try {
            mPlaces = ops.place().queryListSync();
            mPlaceTypes = ops.placeType().queryListSync();

            Log.i(MyApp.TAG, "loaded " + mPlaces.length + " places");
            Log.i(MyApp.TAG, "loaded " + mPlaceTypes.length + " place typeIds");

        } catch (OpsException e) {
            e.printStackTrace();

            throw new RuntimeException("Error reading data from database");
        }

//        if (getArguments() == null)
//            throw new RuntimeException("Logical error: received empty bundle!");

//        mPlaces = new Gson().fromJson(getArguments().getString(INTENT_PRODUCT_INFO), PriceData.class);
//            if (mPriceData == null)
//                throw new RuntimeException("Logical error: bundle does not contain price info");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView != null)
            return mView;

        mView = inflater.inflate(R.layout.activity_new_task_placement, container, false);

        mList = (ListView) mView.findViewById(R.id.lvPlacement);
        mList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        // switch locations mask
                        mLocationsMask[i] ^= true;

                        // redraw element
                        ((ArrayAdapter)mList.getAdapter()).notifyDataSetChanged();
                    }
                }
        );

        mPlacementSpinner = ((Spinner)mView.findViewById(R.id.spPlacement));
        mPlacementSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setupListContent(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // ???
            }
        });
        mPlacementSpinner.setSelection(0);

        return mView;
    }

    @Override
    public void onPause() {
        super.onPause();

        updateGlobalData(mPlacementSpinner.getSelectedItemPosition());
    }

    private void setupListContent(int locType) {
        List<String> names = new ArrayList<>();
        if (locType == 0)
            for (PlaceData p: mPlaces)
                names.add(p.name);
        else
            for (PlaceType p: mPlaceTypes)
                names.add(p.name);

        mLocationsMask = new boolean[names.size()];

        mList.setAdapter(new Adapter(mList.getContext(), R.layout.list_item_check_place, names));
    }

    private void updateGlobalData(int index) {
        List<Long> ids = new ArrayList<>();
        for(int i = 0; i < mLocationsMask.length; i++)
            if (mLocationsMask[i])
                ids.add( (index == 0) ? mPlaces[i].id : mPlaceTypes[i].id );

        switch (index) {
            case 0: //place
                mNewTask.placement.place_types = null;
                mNewTask.placement.places = ids;
            break;
            case 1: //place typeIds
                mNewTask.placement.places = null;
                mNewTask.placement.place_types = ids;
            break;
            default:
                throw new RuntimeException("Internal error: invalid index " + index);
        }
    }

    /**
     * Class data adapter for ListView
     */
    private class Adapter extends ArrayAdapter<String> {
        private int       mLayoutResourceId;
        private Context mContext;
        private List<String> mValues;

        public Adapter(Context context, int layoutId, List<String> values) {
            super(context, layoutId);

            mLayoutResourceId = layoutId;
            mContext = context;
            mValues  = values;
        }

        @Override
        public View getView (final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();

                convertView = inflater.inflate(mLayoutResourceId, parent, false);
            }

            ((TextView) convertView.findViewById(R.id.text))
                    .setText(mValues.get(position));

            // set mark if this element is checked
            if (position >= mLocationsMask.length)
                throw new RuntimeException("Something wrong here!");

            if (mLocationsMask[position])
                convertView.findViewById(R.id.iv).setBackgroundResource(R.mipmap.ic_check_mark);
            else
                convertView.findViewById(R.id.iv).setBackgroundResource(R.mipmap.ic_check_mark_none);

            return convertView;
        }

        @Override
        public int getCount() {
            return mValues.size();
        }
    }

}
