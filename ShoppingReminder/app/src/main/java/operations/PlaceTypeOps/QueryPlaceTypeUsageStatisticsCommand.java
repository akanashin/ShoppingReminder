package operations.PlaceTypeOps;

import android.content.ContentResolver;

import operations.Operations;
import utils.AsyncOperationBase;
import utils.async_stuff.AsyncOpCallback;
import utils.datatypes.PlaceType;

/**
 * Created by akana_000 on 6/20/2015.
 */
public class QueryPlaceTypeUsageStatisticsCommand
        extends AsyncOperationBase<PlaceType.Usage>
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
        //ToDo: IMPLEMENT!!!
        PlaceType data = new PlaceType();
        PlaceType.Usage udata = data.new Usage();
        udata.n_places = 0;
        udata.n_tasks = 0;

        return udata;
    }
}
