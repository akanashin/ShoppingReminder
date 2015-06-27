package utils;

import android.content.ContentResolver;

import operations.Operations;
import utils.async_stuff.AsyncOpCallback;
import utils.async_stuff.GenericAsyncTask;
import utils.async_stuff.GenericAsyncTaskOps;

/**
 * Base of async operation
 * Needs to be implemented using doOperation
 */
public abstract class AsyncOperationBase<ReturnType>
        implements GenericAsyncTaskOps<Void, Void, ReturnType> {

    protected Operations mOps;
    private GenericAsyncTask mAsyncTask;
    private AsyncOpCallback mCb; //callback to be called when

    public AsyncOperationBase(Operations ops, AsyncOpCallback cb) {
        mOps = ops;
        mAsyncTask = new GenericAsyncTask<>(this);
        mCb = cb;
    }

    // this will actually execute the op
    public void run() {
        mAsyncTask.execute((Void) null);
    }

    @Override
    public ReturnType doInBackground(Void param) {
        return doOperation
                (MyApp.getContext().getContentResolver());
    }

    @Override
    public void onPostExecute(ReturnType uid, Void param) {
        mCb.run(uid);
    }

    /**
     * Synchronously create new record in database and return ID.
     */
    public abstract ReturnType doOperation(ContentResolver cr);
}
