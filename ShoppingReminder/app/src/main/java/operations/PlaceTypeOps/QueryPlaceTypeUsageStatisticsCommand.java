package operations.PlaceTypeOps;

import android.content.ContentResolver;
import android.database.Cursor;

import operations.Operations;
import utils.Commons;
import utils.async_stuff.GenericAsyncOperation;
import utils.async_stuff.AsyncOpCallback;
import utils.database.DatabaseContract;
import utils.datatypes.PlaceType;

/**
 * Created by akana_000 on 6/20/2015.
 */
public class QueryPlaceTypeUsageStatisticsCommand
        extends GenericAsyncOperation<PlaceType.Usage>
{
    private Integer          mUid;  // Uid of PlaceType we are doing querying

    public QueryPlaceTypeUsageStatisticsCommand(Operations ops, Integer uid, AsyncOpCallback cb) {
        super(ops, cb);
        mUid = uid;
    }

    /**
     * Synchronously query for usage statistics of a given place type in the Provider.
     */
    @Override
    public PlaceType.Usage doOperation(ContentResolver cr) {
        PlaceType data = new PlaceType("", 0);
        PlaceType.Usage udata = data.new Usage();

        String[] projection = {
                DatabaseContract.Table_PlaceTypeLink.COLUMN_ID,
        };

        Cursor cursor = cr.query(Commons.ContentProvider.URI_TABLE_PLACE_TYPE_LINK,
                projection,                                                       // what
                DatabaseContract.Table_PlaceTypeLink.COLUMN_TYPE_ID + "=" + mUid, // where
                null,                                                             // selection args
                DatabaseContract.Table_PlaceTypeLink.COLUMN_ID                    // order
        );

        udata.n_places = cursor.getCount();

        // ToDo: IMPLEMENT
        udata.n_tasks = 0;

        return udata;
    }
}
