package utils;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import utils.async_stuff.AsyncOpCallback;

/**
 * Helper class for synchronous setter or getter
 * Usage: give proper type of returning value (if this will be getter) or Void
 *        override method exec
 *        call whatever you want and set 'this' as callback
 * @param <ResultType>
 */
public abstract class CommandSyncer<ResultType>
        implements AsyncOpCallback<ResultType> {
    private CountDownLatch mSignal = new CountDownLatch(1);
    private ArrayList<ResultType> mResult =  new ArrayList<>(1);


    public ResultType doStuff() {
        exec();

        try {
            mSignal.await();// wait for callback
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return mResult.get(0);
    }

    /*
     * This method will be called as callback from AsyncOperation
     */
    @Override
    public void run(ResultType param) {
        mResult.add(param);
        mSignal.countDown();
    }

    /*
     * This method needs to be overridden and used for any AsyncCommand
     */
    protected abstract void exec();
}