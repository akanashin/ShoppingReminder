package home.akanashin.shoppingreminder.pages;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import home.akanashin.shoppingreminder.R;
import home.akanashin.shoppingreminder.operations.Operations;
import home.akanashin.shoppingreminder.operations.OpsException;
import home.akanashin.shoppingreminder.utils.MyApp;
import home.akanashin.shoppingreminder.utils.Utils;
import home.akanashin.shoppingreminder.utils.datatypes.PlaceData;
import home.akanashin.shoppingreminder.utils.datatypes.PlaceType;
import home.akanashin.shoppingreminder.utils.datatypes.TaskData;

/**
 * Created by akana_000 on 8/16/2015.
 */
public class Whatsaround extends Fragment {
    private int mViewMode = 0;
    private int iconWidth, iconHeight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.act_whatsaround, container, false);


        setupListView(view);

        setViewModeButtons();

        view.findViewById(R.id.calendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewMode = 0;
                setViewModeButtons();
                setupListView(view);
            }
        });
        view.findViewById(R.id.placement).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewMode = 1;
                setViewModeButtons();
                setupListView(view);
            }
        });
        view.findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewMode = 2;
                setViewModeButtons();
                setupListView(view);
            }
        });

        //iconWidth = findViewById(R.id.calendar).getWidth();
        //iconHeight = findViewById(R.id.calendar).getHeight();

        return view;
    }

    private void setViewModeButtons() {
        /*
        int ids[] = {R.id.calendar, R.id.placement, R.id.text};
        for (int i = 0; i < 3; i++) {
            View view = findViewById(ids[i]);
            if (i == mViewMode) {
                // make it bigger
                view.setMinimumWidth(iconWidth * 3 / 2);
                view.setMinimumHeight(iconHeight * 3 / 2);
            } else {
                // return its size
                view.setMinimumWidth(iconWidth);
                view.setMinimumHeight(iconHeight);
            }
        }
        */
    }

    private void setupListView(View view) {
        try {
            ExpandableListView listView1 = (ExpandableListView) (view.findViewById(R.id.hottasks));

            // get tasks from database
            Operations ops = new Operations();

            TaskData[] tasks;
            PlaceData[] places;
            try {
                tasks = ops.task().queryListSync();
                places = ops.place().queryListSync();
            } catch (OpsException e) {
                e.printStackTrace();
                Toast.makeText(view.getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                return;
            }

            ListAdaptor.Group[] groups;
            switch (mViewMode) {
                case 0:
                    // grouping by expiration
                    Arrays.sort(tasks, new Comparator<TaskData>() {
                        @Override
                        public int compare(TaskData lhs, TaskData rhs) {
                            if (lhs.expiration.date < rhs.expiration.date)
                                return -1;
                            else
                                return 0;
                        }
                    });

                    DateTime tomorrow = DateTime.now()
                            .plusDays(1)
                            .withHourOfDay(0)
                            .withMinuteOfHour(0)
                            .withSecondOfMinute(0)
                            .withMillisOfDay(0);
                    groups = new ListAdaptor.Group[]{
                            new ListAdaptor.Group("Expired", DateTime.now().getMillis()),
                            new ListAdaptor.Group("Today", tomorrow.getMillis()),
                            new ListAdaptor.Group("Tomorrow", tomorrow.plusDays(1).getMillis()),
                            new ListAdaptor.Group("This week", tomorrow.plusDays(6).withDayOfWeek(1).getMillis()), // +6 days gives next week, then we cut it to first day
                            new ListAdaptor.Group("Later", 0),
                    };

                    // expires
                    for (TaskData task : tasks) {
                        for (ListAdaptor.Group group : groups)
                            if (group.end == 0 || task.expiration.date < group.end) {
                                group.items.add(new ListAdaptor.Group.Item(
                                        task,
                                        Utils.DateUtil.toString(task.expiration.date),
                                        ""
                                ));
                                break;
                            }
                    }


                    break;

                case 1:
                    // grouping by distance
                    Location myloc = MyApp.getLocationRequester().getCurrentLocation();

                    // traverse all the tasks and find nearest place or one of places with the given type
                    class D {
                        double distance;
                        String name;
                        TaskData task;

                        public D(double distance, String name, TaskData task) {
                            this.distance = distance;
                            this.name = name;
                            this.task = task;
                        }
                    }

                    ArrayList<D> tasksWithDistance = new ArrayList<>();

                    for (TaskData task : tasks) {
                        double distance = Double.MAX_VALUE;
                        String objectName = "";

                        if (task.placement != null) {
                            List<PlaceData> arrplaces = new ArrayList<>();

                            if (task.placement.places != null) {
                                for (long id : task.placement.places)
                                    arrplaces.add(PlaceData.findById(places, id));
                            } else {
                                // collect all places for given array of typeIds
                                for (long ptId : task.placement.place_types) {
                                    for (PlaceData placeData : places)
                                        if (placeData.isOneOfTypes(ptId))
                                            arrplaces.add(placeData);
                                }
                            }

                            // iterate over places
                            for (PlaceData place : arrplaces) {
                                float[] dist = new float[1];
                                Location.distanceBetween(
                                        myloc.getLatitude(), myloc.getLongitude(),
                                        place.loc.latitude, place.loc.longitude,
                                        dist);

                                if (dist[0] < distance) {
                                    distance = dist[0];
                                    objectName = place.name;
                                }
                            }
                        }

                        tasksWithDistance.add(new D(distance, objectName, task));
                    }

                    groups = new ListAdaptor.Group[]{
                            new ListAdaptor.Group("Here", 100),
                            new ListAdaptor.Group("Around", 500),
                            new ListAdaptor.Group("Far", 0),
                    };

                    Collections.sort(tasksWithDistance, new Comparator<D>() {
                        @Override
                        public int compare(D lhs, D rhs) {
                            return (int) (lhs.distance - rhs.distance);
                        }
                    });

                    // fill the groups with tasks
                    for (D d : tasksWithDistance) {
                        for (ListAdaptor.Group group : groups)
                            if (group.end == 0 || group.end > d.distance) {
                                group.items.add(new ListAdaptor.Group.Item(
                                        d.task,
                                        (d.distance == Double.MAX_VALUE) ? "" : "" + (int)(d.distance) + "m",
                                        d.name));
                                break;
                            }
                    }
                    break;

                case 2:
                    // simple list of tasks
                    groups = new ListAdaptor.Group[]{
                            new ListAdaptor.Group("All the tasks", 0)
                    };

                    PlaceType[] pTypes = ops.placeType().queryListSync();

                    for (int i = 0; i < tasks.length; i++) {
                        TaskData task = tasks[i];
                        String field2 = "";
                        if (task.placement != null) {
                            if (task.placement.places != null) {
                                for (long id : task.placement.places)
                                    field2 += PlaceData.findById(places, id).name + ",";
                            } else {
                                if (task.placement.place_types != null) {
                                    for (long id : task.placement.place_types)
                                        field2 += PlaceType.findById(pTypes, id).name + ",";
                                }
                            }
                        }

                        if (field2.length() > 1)
                            field2 = field2.substring(0, field2.length() - 1);

                        if (field2.length() > 20)
                            field2 = field2.substring(0, 20) + "...";

                        groups[0].items.add(
                                new ListAdaptor.Group.Item(task,
                                        Utils.DateUtil.toString(task.expiration.date),
                                        field2));
                    }

                    break;
                default:
                    throw new RuntimeException("Bad bad bad");
            }

            listView1.setAdapter(new ListAdaptor(this.getActivity(), groups));
        } catch (OpsException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error:" + e, Toast.LENGTH_LONG).show();
        }
    }

    public static class ListAdaptor extends BaseExpandableListAdapter {
        public static class Group {
            public String name;
            public long end;
            public ArrayList<Item> items;

            public Group(String name, long millis) {
                this.name = name;
                this.end = millis;
                items = new ArrayList<>();
            }

            public static class Item {
                public String field1;
                public String field2;
                public TaskData task;

                public Item(TaskData task, String field1, String field2) {
                    this.field1 = field1;
                    this.field2 = field2;
                    this.task = task;
                }
            }
        }

        private Group[] mData;
        private Context mContext;

        public ListAdaptor(Context context, Group[] data) {
            mContext = context;
            mData = data;
        }

        @Override
        public int getGroupCount() {
            return mData.length;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mData[groupPosition].items.size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mData[groupPosition];
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mData[groupPosition].items.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.act_whatsaround_main_hotlist_group, null);
            }

            // show right arrow if group is closed and it contains children
            if (isExpanded || getChildrenCount(groupPosition) == 0)
                convertView.findViewById(R.id.arrow).setVisibility(View.INVISIBLE);
            else
                convertView.findViewById(R.id.arrow).setVisibility(View.VISIBLE);

            // Name of a group is always first in the array
            TextView textGroup = (TextView) convertView.findViewById(R.id.text);
            textGroup.setText(mData[groupPosition].name);

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.act_whatsaround_main_hotlist_child, null);
            }

            Group.Item item = mData[groupPosition].items.get(childPosition);

            ((TextView) convertView.findViewById(R.id.text)).setText(item.task.description);

            if (item.field1 != null)
                ((TextView) convertView.findViewById(R.id.field1)).setText( item.field1);
            if (item.field2 != null)
                ((TextView) convertView.findViewById(R.id.field2)).setText(item.field2);

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
