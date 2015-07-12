package utils.async_stuff;

import android.content.ContentResolver;
import android.util.Log;

import utils.Commons;
import utils.MyApp;
import utils.datatypes.Result;

/**
 * Base of async operation
 * Needs to be implemented using doOperation
 */
public abstract class DatabaseOperation<ReturnType> {

    private GenericAsyncTask mAsyncTask;
    private AsyncOpCallback  mCb; //callback to be called when

    public DatabaseOperation(AsyncOpCallback cb) {
        mAsyncTask = new GenericAsyncTask<>(this);
        mCb = cb;
    }

    // this will actually execute the op
    public void run() {
        mAsyncTask.execute((Void) null);
    }

    public Result<ReturnType> doInBackground() {
        try {
            return new Result<>( doOperation(MyApp.getContext().getContentResolver()) );
        } catch (android.database.SQLException ex) {
            Log.w(Commons.TAG, "SQL exception: " + ex.getMessage());

            // ToDo: translate to human readable format
            return new Result<>(null, ex.getMessage());
        }
    }

    public void onPostExecute(Result<ReturnType> result) {
        mCb.run(result);
    }

    /**
     * Synchronously create new record in database and return ID.
     */
    public abstract ReturnType doOperation(ContentResolver cr);
}
