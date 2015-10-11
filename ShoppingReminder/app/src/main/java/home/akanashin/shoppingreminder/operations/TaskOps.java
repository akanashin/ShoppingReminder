package home.akanashin.shoppingreminder.operations;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import datastore.generated.provider.taskplacelink.TaskPlaceLinkContentValues;
import datastore.generated.provider.taskplacelink.TaskPlaceLinkCursor;
import datastore.generated.provider.taskplacelink.TaskPlaceLinkSelection;
import datastore.generated.provider.taskplacetypelink.TaskPlaceTypeLinkContentValues;
import datastore.generated.provider.taskplacetypelink.TaskPlaceTypeLinkCursor;
import datastore.generated.provider.taskplacetypelink.TaskPlaceTypeLinkSelection;
import datastore.generated.provider.tasks.TasksContentValues;
import datastore.generated.provider.tasks.TasksCursor;
import datastore.generated.provider.tasks.TasksSelection;
import home.akanashin.shoppingreminder.utils.Commons;
import home.akanashin.shoppingreminder.utils.Utils;
import home.akanashin.shoppingreminder.utils.datatypes.PlaceData;
import home.akanashin.shoppingreminder.utils.datatypes.PlaceType;
import home.akanashin.shoppingreminder.utils.datatypes.TaskData;

/**
 *   Database home.akanashin.shoppingreminder.operations on tasks
 */
public class TaskOps extends OpsInterface<TaskData[], Void> {

    @Override
    protected TaskData[] doQuery(ContentResolver cr, long uid) throws OpsException {
        Log.d(Commons.TAG, "TaskOps.doQuery(" + uid + ") called");
        ArrayList<TaskData> tasks = new ArrayList<>();

        // 1st: get list of tasks
        {
            TasksSelection sel = new TasksSelection();
            if (uid != -1)
                sel.id(uid);

            TasksCursor cursor = sel.query(cr);
            while(cursor.moveToNext()) {
                tasks.add(new TaskData(
                        cursor.getId(),
                        cursor.getShortname(),
                        cursor.getExpiration(),
                        cursor.getRemark(),

                        null, null // i don't know which list will be present here
                ));
            }
        }

        // 2nd: for each task get list of types or places
        for(TaskData task : tasks)
        {
            // 2.1: check types
            {
                TaskPlaceTypeLinkSelection sel = new TaskPlaceTypeLinkSelection();
                sel.taskId(task.id);

                TaskPlaceTypeLinkCursor cursor = sel.query(cr);

                ArrayList<PlaceType> types = new ArrayList<>();
                while (cursor.moveToNext()) {
                    types.add(new PlaceType(
                            cursor.getPlaceTypeId(),
                            cursor.getPlaceTypesNote(),
                            cursor.getPlaceTypesColor()));
                }

                if ( !types.isEmpty() ) {
                    task.types = types;
                    continue;
                }
            }

            // 2.2: check places
            {
                TaskPlaceLinkSelection sel = new TaskPlaceLinkSelection();
                sel.taskId(task.id);

                TaskPlaceLinkCursor cursor = sel.query(cr);

                ArrayList<PlaceData> places = new ArrayList<>();
                while (cursor.moveToNext()) {
                    places.add(new PlaceData(
                            cursor.getPlaceId(),
                            cursor.getPlacesName(),
                            0.0, 0.0,
                            new ArrayList<PlaceType>()
                    ));
                }

                if ( !places.isEmpty() )
                    task.places = places;
            }
        }

        // sort resulting set by Name
        Collections.sort(tasks, new Comparator<TaskData>() {
            @Override
            public int compare(TaskData taskData, TaskData t1) {
                return taskData.name.compareTo(t1.name);
            }
        });

        return tasks.toArray(new TaskData[tasks.size()]);
    }

    @Override
    protected Integer doAddOrModify(ContentResolver cr, TaskData[] data) throws OpsException {
        Log.d(Commons.TAG, "TaskOps.doAddOrModify(" + data + ") called");

        // First: some checks
        for (TaskData task : data) {
            // 1st: not empty name
            if (task.name == null
                    || task.name.trim().isEmpty())
                throw new OpsException(OpsException.MSG_EMPTY_NAME);

            // 2nd: task has place and types (only can have one of it)
            if (task.types != null && task.places != null)
                throw new OpsException(OpsException.MSG_EITHER_PLACE_OR_TYPE);
        }

        // Now: processing
        for (TaskData task : data) {
            TasksContentValues task_cv = new TasksContentValues();
            task_cv.putShortname(task.name);
            task_cv.putExpiration(task.expiration);
            task_cv.putRemark(task.remark);

            Boolean needToWriteLinks = false;
            if (task.id == 0) {
                // creating new record and store its ID
                Uri uri = task_cv.insert(cr);
                task.id = Long.parseLong(uri.getLastPathSegment());

                needToWriteLinks = true;
            } else {
                // updating the record

                // 1st - get old value of record
                TaskData[] cur = doQuery(cr, task.id);
                if (cur.length != 1)
                    throw new AssertionError("Logical error: found " + cur.length + " records for ID=" + task.id);

                // 2nd: do i need to update record at all?
                if (cur[0].equals(task))
                    continue;

                // 3rd: do i need to update record data?
                if (!Utils.compare(cur[0].name, task.name)
                    || !Utils.compare(cur[0].expiration,task.expiration)
                    || !Utils.compare(cur[0].remark, task.expiration) )
                    task_cv.update(cr, new TasksSelection().id(task.id));

                // 4th: do i need to update links to places and types?
                if (!Utils.compare(cur[0].types, task.types)
                    || !Utils.compare(cur[0].places, task.places)) {
                    // delete old link records (i will recreate them later)
                    new TaskPlaceTypeLinkSelection().taskId(task.id).delete(cr);
                    new TaskPlaceLinkSelection().taskId(task.id).delete(cr);

                    needToWriteLinks = true;
                }
            }

            // now update link tables (create all the records)
            if (needToWriteLinks) {
                // i don't check anything here (hoping for the best)
                if (task.types != null)
                    for (PlaceType pt : task.types) {
                        TaskPlaceTypeLinkContentValues cv = new TaskPlaceTypeLinkContentValues();

                        cv.putTaskId(task.id);
                        cv.putPlaceTypeId(pt.id);
                        cv.insert(cr);
                    }
                if (task.places != null)
                    for (PlaceData p : task.places) {
                        TaskPlaceLinkContentValues cv = new TaskPlaceLinkContentValues();

                        cv.putTaskId(task.id);
                        cv.putPlaceId(p.id);
                        cv.insert(cr);
                    }
            }
        }

        return data.length;
    }

    @Override
    protected Integer doDelete(ContentResolver cr, long uid) {
        Log.d(Commons.TAG, "TaskOps.doDelete(" + uid + ") called");
        TasksSelection where = new TasksSelection();

        if (uid != -1)
            where = where.id(uid);

        return where.delete(cr);
    }

    @Override
    protected Void doQueryUsageStatistics(ContentResolver cr, long uid) {
        throw new UnsupportedOperationException("Not Implemented");
    }
}
