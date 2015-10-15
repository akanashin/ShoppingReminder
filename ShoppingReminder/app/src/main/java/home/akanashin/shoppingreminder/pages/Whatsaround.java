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
import java.util.Set;
import java.util.TreeSet;

import home.akanashin.shoppingreminder.R;
import home.akanashin.shoppingreminder.operations.Operations;
import home.akanashin.shoppingreminder.operations.OpsException;
import home.akanashin.shoppingreminder.utils.MyApp;
import home.akanashin.shoppingreminder.utils.Utils;
import home.akanashin.shoppingreminder.utils.datatypes.PlaceData;
import home.akanashin.shoppingreminder.utils.datatypes.PlaceType;
import home.akanashin.shoppingreminder.utils.datatypes.TaskDatav2;

/**
 * Created by akana_000 on 8/16/2015.
 */
public class Whatsaround extends Fragment {
    private int mViewMode = 0;
    private int iconWidth, iconHeight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.whatsaround, container, false);


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
        ExpandableListView listView1 = (ExpandableListView)(view.findViewById(R.id.hottasks));

        // get tasks from database
        Operations ops = new Operations();

        TaskDatav2[] tasks;
        PlaceData[] places;
        PlaceType[] pTypes;
        try {
            tasks = ops.taskv2().queryListSync();
            pTypes = ops.placeType().queryListSync();
            places = ops.place().queryListSync();
        } catch (OpsException e) {
            e.printStackTrace();
            Toast.makeText(view.getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            return;
        }

        TimeListAdaptor.Group[] groups;
        switch (mViewMode) {
            case 0:
                // grouping by expiration
                Arrays.sort(tasks, new Comparator<TaskDatav2>() {
                    @Override
                    public int compare(TaskDatav2 lhs, TaskDatav2 rhs) {
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
                groups = new TimeListAdaptor.Group[] {
                        new TimeListAdaptor.Group("Expired", DateTime.now().getMillis()),
                        new TimeListAdaptor.Group("Today", tomorrow.getMillis()),
                        new TimeListAdaptor.Group("Tomorrow", tomorrow.plusDays(1).getMillis()),
                        new TimeListAdaptor.Group("This week", tomorrow.plusDays(6).withDayOfWeek(1).getMillis()), // +6 days gives next week, then we cut it to first day
                        new TimeListAdaptor.Group("Later", 0),
                };

                // expires
                for (TaskDatav2 task : tasks) {
                    for(TimeListAdaptor.Group group : groups)
                        if ( group.end == 0 || task.expiration.date < group.end ) {
                            group.tasks.add(task);
                            break;
                        }
                }

                break;

            case 1:
                // grouping by distance
                Location myloc = MyApp.getInstance().getLocationRequester().getCurrentLocation();

                // traverse all the tasks and find nearest place or one of places with the given type
                class D {
                    double distance;
                    TaskDatav2 task;

                    public D(double distance, TaskDatav2 task) {
                        this.distance = distance;
                        this.task = task;
                    }
                }

                ArrayList<D> tasksWithDistance = new ArrayList<>();

                for (TaskDatav2 task : tasks) {
                    double distance = Double.MAX_VALUE;

                    if (task.placement != null) {
                        if (task.placement.places != null) {
                            // iterate over places
                            for (long id : task.placement.places) {
                                PlaceData place = null;
                                for (PlaceData p : places)
                                    if (id == p.id)
                                        place = p;

                                if (place == null)
                                    throw new RuntimeException("Internal error: non-existing ID of place!");

                                float[] dist = new float[1];
                                Location.distanceBetween(
                                        myloc.getLatitude(), myloc.getLongitude(),
                                        place.loc.latitude, place.loc.longitude,
                                        dist);

                                if (dist[0] < distance)
                                    distance = dist[0];
                            }
                        } else {
                            if (task.placement.place_types != null) {
                                // iterate over types
                            }
                        }
                    }

                    tasksWithDistance.add(new D(distance, task));
                }

                groups = new TimeListAdaptor.Group[] {
                        new TimeListAdaptor.Group("Here", 100),
                        new TimeListAdaptor.Group("Around", 500),
                        new TimeListAdaptor.Group("Far", 0),
                };

                Collections.sort(tasksWithDistance, new Comparator<D>() {
                    @Override
                    public int compare(D lhs, D rhs) {
                        return (int) (lhs.distance - rhs.distance);
                    }
                });

                // fill the groups with tasks
                for (D d : tasksWithDistance) {
                    for (TimeListAdaptor.Group group : groups)
                        if (group.end == 0 || group.end > d.distance) {
                            group.tasks.add(d.task);
                            break;
                        }
                }
                break;

            case 2:
                // simple list of tasks
                groups = new TimeListAdaptor.Group[0];
                break;
            default:
                throw new RuntimeException("Bad bad bad");
        }

        listView1.setAdapter(new TimeListAdaptor(this.getActivity(), groups));
    }

    public static class TimeListAdaptor extends BaseExpandableListAdapter {
        public static class Group {
            public String name;
            public long end;
            public ArrayList<TaskDatav2> tasks;

            public Group(String name, long millis) {
                this.name = name;
                this.end = millis;
                tasks = new ArrayList<>();
            }
        }

        private Group[] mData;
        private Context mContext;

        public TimeListAdaptor(Context context, Group[] data) {
            mContext = context;
            mData = data;
        }

        @Override
        public int getGroupCount() {
            return mData.length;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mData[groupPosition].tasks.size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mData[groupPosition];
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mData[groupPosition].tasks.get(childPosition);
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
                convertView = inflater.inflate(R.layout.main_hotlist_group, null);
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
                convertView = inflater.inflate(R.layout.main_hotlist_child, null);
            }

            ((TextView) convertView.findViewById(R.id.text)).setText(mData[groupPosition].tasks.get(childPosition).description);

            ((TextView) convertView.findViewById(R.id.date))
                    .setText( Utils.DateUtil.toLocal(new DateTime(mData[groupPosition]
                            .tasks.get(childPosition)
                            .expiration.date)).toString("d-M-x H:m:s") );
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
