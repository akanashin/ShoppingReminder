package home.akanashin.shoppingreminder.utils.async_stuff;

import android.os.AsyncTask;

import home.akanashin.shoppingreminder.utils.datatypes.Result;

/**
 * Defines a generic framework for running an AsyncTask that delegates
 * its home.akanashin.shoppingreminder.operations to the @a Ops parameter.
 */
public class GenericAsyncTask<Params,
                              Progress,
                              ResultType,
                              Ops extends DatabaseOperation<ResultType>>
      extends AsyncTask<Params, Progress, ResultType> {

    /**
     * Reference to the enclosing Ops object.
     */
    protected Ops mOps;

    /**
     * Result of operation
     */
    private Result<ResultType> mResult = null;

    /**
     * Constructor initializes the field.
     */
    public GenericAsyncTask(Ops ops) {
	mOps = ops;
    }

    /**
     * Run in a background thread to avoid blocking the UI thread.
     */
    @SuppressWarnings("unchecked")
    protected ResultType doInBackground(Params... params) {
        mResult = mOps.doInBackground(); //store result to return it in onPostExecute
        return mResult.result;
    }

    /**
     * Process results in the UI Thread.
     */
    protected void onPostExecute(ResultType result) {
        mOps.onPostExecute(mResult);
    }
}
