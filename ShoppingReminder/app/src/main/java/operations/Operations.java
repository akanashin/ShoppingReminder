package operations;

import android.util.Log;

import operations.PlaceOps.AddOrUpdatePlaceCommand;
import operations.PlaceOps.DeletePlaceCommand;
import operations.PlaceOps.QueryPlacesCommand;
import operations.PlaceTypeOps.AddOrUpdatePlaceTypeCommand;
import operations.PlaceTypeOps.DeletePlaceTypeCommand;
import operations.PlaceTypeOps.QueryPlaceTypeUsageStatisticsCommand;
import operations.PlaceTypeOps.QueryPlaceTypesListCommand;
import utils.Commons;
import utils.async_stuff.AsyncOpCallback;
import utils.async_stuff.ConfigurableOps;
import utils.datatypes.PlaceData;
import utils.datatypes.PlaceType;

/**
 * Created by akana_000 on 6/19/2015.
 */
public class Operations implements ConfigurableOps {
    private PlaceTypeOps mPlaceTypeOps;
    private PlaceOps     mPlaceOps;

    /*
     * This method is called when configuration changes occur
     * activity is the new activity
     * firstTimeIn - flag indicates that this is first time (we need to initialization)
     */
    @Override
    public void onConfiguration(boolean firstTimeIn) {
        Log.d(Commons.TAG, "onConfiguration called: firstTimeIn=" + firstTimeIn);

        mPlaceTypeOps = this.new PlaceTypeOps();
        mPlaceOps     = this.new PlaceOps();
    }

    /*
     * Accessors for groups of operations
     */
    public PlaceTypeOps placeType() { return mPlaceTypeOps; }
    public PlaceOps     place()     { return mPlaceOps; }

    /*
     * Operations for Place Types
     */
    public class PlaceTypeOps {
        public void queryList(AsyncOpCallback cb) {
            Log.d(Commons.TAG, "PlaceTypeOps.queryList requested");

            new QueryPlaceTypesListCommand(Operations.this, cb).run();
        }

        // searches for usage of a given place
        public void queryUsageStatistics(int uid, AsyncOpCallback cb) {
            Log.d(Commons.TAG, "PlaceTypeOps.queryUsageStatistics requested");

            new QueryPlaceTypeUsageStatisticsCommand(Operations.this, uid, cb).run();
        }

        // add new place type
        public void addOrModify(PlaceType[] data, AsyncOpCallback cb) {
            Log.d(Commons.TAG, "PlaceTypeOps.addOrModify requested");

            new AddOrUpdatePlaceTypeCommand(Operations.this, data, cb).run();
        }

        // delete place type
        // uid == -1: delete all the place types
        public void delete(Integer uid, AsyncOpCallback cb) {
            Log.d(Commons.TAG, "PlaceTypeOps.delete requested");

            new DeletePlaceTypeCommand(Operations.this, uid, cb).run();
        }
    }


    /*
     * Operations for Places
     */
    public class PlaceOps {
        public void queryList(AsyncOpCallback cb) {
            Log.d(Commons.TAG, "PlaceOps.queryList requested");

            new QueryPlacesCommand(Operations.this, cb).run();
        }

        // delete place
        // uid == -1: delete all the places
        public void delete(Integer uid, AsyncOpCallback cb) {
            Log.d(Commons.TAG, "PlaceOps.delete requested");

            new DeletePlaceCommand(Operations.this, uid, cb).run();
        }

        // get data about existing place
        public void query(Integer uid, AsyncOpCallback cb) {
            Log.d(Commons.TAG, "PlaceOps.query requested");

            new QueryPlacesCommand(Operations.this, uid, cb).run();
        }

        // add new or modify existing place
        public void addOrModify(PlaceData[] data, AsyncOpCallback cb) {
            Log.d(Commons.TAG, "PlaceType.addOrModify requested");

            new AddOrUpdatePlaceCommand(Operations.this, data, cb).run();
        }
    }
}
