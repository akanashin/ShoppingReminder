package operations.PlaceTypeOps;

import android.content.ContentResolver;

import operations.Operations;
import utils.AsyncOperationBase;
import utils.Commons;
import utils.async_stuff.AsyncOpCallback;
import utils.database.DatabaseContract;

/**
 * Created by akana_000 on 6/20/2015.
 */
public class DeletePlaceTypeCommand
        extends AsyncOperationBase<Void> {

    private Integer mUid; // ID of item to delete

    public DeletePlaceTypeCommand(Operations ops, Integer uid, AsyncOpCallback cb) {
        super(ops, cb);

        mUid = uid;
        if (mUid == 0)
            throw new AssertionError("Logical error: command to delete Item without ID");
    }

    /*
     * Synchronous operation on provider
     */
    @Override
    public Void doOperation(ContentResolver cr) {
        cr.delete(Commons.ContentProvider.URI_TABLE_PLACE_TYPE,
                    mUid != -1 ? (DatabaseContract.Table_PlaceType.COLUMN_ID + "=" + String.format("%d", mUid)) : "",
                    null);

        return null;
    }
}
