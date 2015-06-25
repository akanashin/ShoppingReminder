package operations.PlaceOps;

import android.content.ContentResolver;

import operations.Operations;
import utils.AsyncOperationBase;
import utils.Commons;
import utils.async_stuff.AsyncOpCallback;
import utils.database.DatabaseContract;

/**
 * Created by akana_000 on 6/20/2015.
 */
public class DeletePlaceCommand
        extends AsyncOperationBase<Void> {

    private Integer mUid; // ID of item to delete

    public DeletePlaceCommand(Operations ops, Integer uid, AsyncOpCallback cb) {
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
        // 1st stage: remove place itself
        cr.delete(Commons.ContentProvider.URI_TABLE_PLACE,
                    mUid != -1 ? (DatabaseContract.Table_PlaceType.COLUMN_ID + "=" + String.format("%d", mUid)) : "",
                    null);

        // 2nd stage: clean Link table of this place
        // 1st stage: remove place itself
        cr.delete(Commons.ContentProvider.URI_TABLE_PLACE,
                mUid != -1 ? (DatabaseContract.Table_PlaceTypeLink.COLUMN_PLACE_ID + "=" + String.format("%d", mUid)) : "",
                null);

        return null;
    }
}
