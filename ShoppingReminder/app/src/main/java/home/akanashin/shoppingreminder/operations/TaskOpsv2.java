package home.akanashin.shoppingreminder.operations;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

import datastore.generated.provider.tasksv2.TasksV2ContentValues;
import datastore.generated.provider.tasksv2.TasksV2Cursor;
import datastore.generated.provider.tasksv2.TasksV2Selection;
import home.akanashin.shoppingreminder.utils.Commons;
import home.akanashin.shoppingreminder.utils.datatypes.TaskDatav2;


/**
 *   Database home.akanashin.shoppingreminder.operations on tasks (v2)
 */
public class TaskOpsv2 extends OpsInterface<TaskDatav2[], Void> {

    @Override
    protected TaskDatav2[] doQuery(ContentResolver cr, long uid) throws OpsException {
        Log.d(Commons.TAG, "TaskOpsv2.doQuery(" + uid + ") called");
        ArrayList<TaskDatav2> tasks = new ArrayList<>();

        TasksV2Selection sel = new TasksV2Selection();
        if (uid != -1)
            sel.id(uid);

        TasksV2Cursor cursor = sel.query(cr);
        while (cursor.moveToNext()) {
            TaskDatav2 task = new Gson().fromJson(cursor.getGson(), TaskDatav2.class);
            task.id = cursor.getId(); // fix Id of task (it is empty when task is first time added)

            tasks.add(task);
        }
        cursor.close();

        return tasks.toArray(new TaskDatav2[tasks.size()]);
    }

    @Override
    protected Integer doAddOrModify(ContentResolver cr, TaskDatav2[] data) throws OpsException {
        Log.d(Commons.TAG, "TaskOpsv2.doAddOrModify(" + data + ") called");

        // First: some checks
        for (TaskDatav2 task : data)
            if (!task.validate())
                throw new OpsException(OpsException.MSG_INVALID);

        // Now: processing
        for (TaskDatav2 task : data) {
            TasksV2ContentValues task_cv = new TasksV2ContentValues();
            task_cv.putGson(new Gson().toJson(task));

            if (task.id == 0) {
                // creating new record and store its ID
                Uri uri = task_cv.insert(cr);
                task.id = Long.parseLong(uri.getLastPathSegment());
            } else {
                // updating the record
                task_cv.update(cr, new TasksV2Selection().id(task.id));
            }
        }

        return data.length;
    }

    @Override
    protected Integer doDelete(ContentResolver cr, long uid) {
        Log.d(Commons.TAG, "TaskOpsv2.doDelete(" + uid + ") called");
        TasksV2Selection where = new TasksV2Selection();

        if (uid != -1)
            where = where.id(uid);

        return where.delete(cr);
    }

    @Override
    protected Void doQueryUsageStatistics(ContentResolver cr, long uid) {
        throw new UnsupportedOperationException("Not Implemented");
    }
}
