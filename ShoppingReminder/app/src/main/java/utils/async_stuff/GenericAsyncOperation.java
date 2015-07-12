package utils.async_stuff;

import android.content.ContentResolver;

import utils.MyApp;

/**
 * Base of async operation
 * Needs to be implemented using doOperation
 */
public abstract class GenericAsyncOperation<ReturnType> {

    private GenericAsyncTask mAsyncTask;
    private AsyncOpCallback mCb; //callback to be called when

    public GenericAsyncOperation(AsyncOpCallback cb) {
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
