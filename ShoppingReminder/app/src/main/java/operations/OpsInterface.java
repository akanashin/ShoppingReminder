package operations;

import utils.async_stuff.AsyncOpCallback;

/**
 * Interface for generification of database operations
 *  mainly used in unit-test
 */
public interface OpsInterface<DataType> {

    void queryList(AsyncOpCallback cb);

    void query(final long uid, AsyncOpCallback cb);

    void addOrModify(final DataType data, AsyncOpCallback cb);

    void delete(final long uid, AsyncOpCallback cb);

}
