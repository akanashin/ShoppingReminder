package home.akanashin.shoppingreminder.operations;

import android.content.ContentResolver;

import home.akanashin.shoppingreminder.utils.MyApp;
import home.akanashin.shoppingreminder.utils.async_stuff.AsyncOpCallback;
import home.akanashin.shoppingreminder.utils.async_stuff.DatabaseOperation;

/**
 * Interface for generification of database home.akanashin.shoppingreminder.operations
 *  mainly used in unit-test
 */
public abstract class OpsInterface<DataType, DataUsageType> {

    /**
     * These are async-wrappers over home.akanashin.shoppingreminder.operations
     * @param cb
     */
    public void queryList(AsyncOpCallback cb) {
        new DatabaseOperation<DataType>(cb) {
            @Override
            public DataType doOperation(ContentResolver cr) throws OpsException {
                return doQuery(cr, -1);
            }
        }.run();
    }

    public void query(final long uid, AsyncOpCallback cb) {
        new DatabaseOperation<DataType>(cb) {
            @Override
            public DataType doOperation(ContentResolver cr) throws OpsException {
                return doQuery(cr, uid);
            }
        }.run();
    }

    public void queryUsageStatistics(final long uid, AsyncOpCallback cb) {
        new DatabaseOperation<DataUsageType>(cb) {
            @Override
            public DataUsageType doOperation(ContentResolver cr) {
                return doQueryUsageStatistics(cr, uid);
            }
        }.run();
    }

    public void addOrModify(final DataType data, AsyncOpCallback cb) {
        new DatabaseOperation<Integer>(cb) {
            @Override
            public Integer doOperation(ContentResolver cr) throws OpsException {
                return doAddOrModify(cr, data);
            }
        }.run();
    }

    public void delete(final long uid, AsyncOpCallback cb) {
        new DatabaseOperation<Integer>(cb) {
            @Override
            public Integer doOperation(ContentResolver cr) throws OpsException {
                return doDelete(cr, uid);
            }
        }.run();
    }

    /**
     * These are syncronous versions of home.akanashin.shoppingreminder.operations
     *        Use with care!
     */
    public DataType queryListSync() throws OpsException {
        return doQuery(MyApp.getContext().getContentResolver(), -1);
    }

    public Integer addOrModifySync(DataType data) {
        try {
            return doAddOrModify(MyApp.getContext().getContentResolver(), data);
        } catch (OpsException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void deleteSync(long uid) {
        doDelete(MyApp.getContext().getContentResolver(), uid);
    }


    /**
     *  These need to be implemented in childs
     */
    abstract protected DataType doQuery(ContentResolver cr, long uid) throws OpsException;
    abstract protected Integer  doAddOrModify(ContentResolver cr, DataType data) throws OpsException;
    abstract protected Integer  doDelete(ContentResolver cr, long uid);

    abstract protected DataUsageType doQueryUsageStatistics(ContentResolver cr, long uid);
}
