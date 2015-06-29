package utils.async_stuff;

import android.content.ContentResolver;

import operations.Operations;
import utils.MyApp;

/**
 * Base of async operation
 * Needs to be implemented using doOperation
 */
public abstract class GenericAsyncOperation<ReturnType> {

    protected Operations mOps;
    private GenericAsyncTask mAsyncTask;
    private AsyncOpCallback mCb; //callback to be called when

    public GenericAsyncOperation(Operations ops, AsyncOpCallback cb) {
        mOps = ops;
        mAsyncTask = new GenericAsyncTask<>(this);
        mCb = cb;
    }

    // this will actually execute the op
    public void run() {
        mAsyncTask.execute((Void) null);
    }

    public ReturnType doInBackground() {
        return doOperation
                (MyApp.getContext().getContentResolver());
    }

    public void onPostExecute(ReturnType uid) {
        mCb.run(uid);
    }

    /**
     * Synchronously create new record in database and return ID.
     */
    public abstract ReturnType doOperation(ContentResolver cr);
}
