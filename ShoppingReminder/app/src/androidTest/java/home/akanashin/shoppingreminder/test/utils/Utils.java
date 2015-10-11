package home.akanashin.shoppingreminder.test.utils;

import home.akanashin.shoppingreminder.operations.Operations;
import home.akanashin.shoppingreminder.operations.OpsInterface;
import home.akanashin.shoppingreminder.utils.CommandSyncer;
import home.akanashin.shoppingreminder.utils.datatypes.Result;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Generic database routines and checkers for unit-test
 */
public class Utils <
        DataType,
        DataUsageType,
        OperationsType extends OpsInterface<DataType, DataUsageType> > {

    private OperationsType mOps;

    public Utils(OperationsType ops) {
        mOps = ops;
    }

    public static <ResultType> void verifyResult(ResultType result, String message, Result<ResultType> checkedData) {
        assertEquals(result, checkedData.result);
        assertEquals(message, checkedData.message);
    }

    public void checkedInsertOrModify(Integer result, String message, DataType data) {
        verifyResult(result, message, insertOrModifyToDB(data));
    }

    /**
     * Procedure to query database and verify that result is OK
     *
     * @return readed data
     */
    public DataType checkedQuery() {
        Result<DataType> result = queryFromDB();

        // checking place types
        assertFalse(result.result == null);
        assertTrue("".equals(result.message));

        return result.result;
    }


    /**
     * Synchronous wrapper over adding to database
     *
     * @param data
     */
    public Result<Integer> insertOrModifyToDB(final DataType data) {
        return new CommandSyncer<Result<Integer>>() {
            @Override
            public void exec() {
                mOps.addOrModify(data, this);
            }
        }.doStuff();
    }

    /**
     * Synchronous wrapper over querying the database
     */
    private Result<DataType> queryFromDB() {
        return new CommandSyncer<Result<DataType>>() {
            @Override
            public void exec() {
                mOps.queryList(this);
            }
        }.doStuff();
    }

    /**
     * Synchronous wrapper over deleting from database
     */
    public Result<Integer> deleteFromDB(final long id) {
        return new CommandSyncer<Result<Integer>>() {
            @Override
            public void exec() {
                mOps.delete(id, this);
            }
        }.doStuff();
    }

    /**
     * Synchronous wrapper over clearing database
     */
    public void clearDB() {
        new CommandSyncer<Void>() {
            @Override
            public void exec() {
                Operations.clearDB(this);
            }
        }.doStuff();
    }

}
