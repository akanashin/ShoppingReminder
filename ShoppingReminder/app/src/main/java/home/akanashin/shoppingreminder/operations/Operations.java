package home.akanashin.shoppingreminder.operations;

import android.content.ContentResolver;

import org.joda.time.DateTime;

import java.util.ArrayList;

import datastore.generated.provider.places.PlacesSelection;
import datastore.generated.provider.placetypes.PlaceTypesSelection;
import datastore.generated.provider.tasks.TasksSelection;
import datastore.generated.provider.tasksv2.TasksV2Selection;
import home.akanashin.shoppingreminder.utils.async_stuff.AsyncOpCallback;
import home.akanashin.shoppingreminder.utils.async_stuff.DatabaseOperation;
import home.akanashin.shoppingreminder.utils.datatypes.PlaceData;
import home.akanashin.shoppingreminder.utils.datatypes.PlaceType;
import home.akanashin.shoppingreminder.utils.datatypes.Result;
import home.akanashin.shoppingreminder.utils.datatypes.TaskData;

/**
 * Created by akana_000 on 6/19/2015.
 */
public class Operations {

    private TaskOps      mTaskOps;
    private PlaceTypeOps mPlaceTypeOps;
    private PlaceOps     mPlaceOps;

    public Operations() {
        mPlaceTypeOps = new PlaceTypeOps();
        mPlaceOps = new PlaceOps();
        mTaskOps = new TaskOps();
    }

    /*
     * Accessors for groups of home.akanashin.shoppingreminder.operations
     */
    public PlaceTypeOps placeType() {
        return mPlaceTypeOps;
    }

    public PlaceOps place() {
        return mPlaceOps;
    }

    public TaskOps task() {
        return mTaskOps;
    }

    /**
     * Clearer of database
     */
    public static void clearDB(AsyncOpCallback cb) {
        new DatabaseOperation<Result<Integer>>(cb) {
            @Override
            public Result<Integer> doOperation(ContentResolver cr) {
                // 1st: clear place-typeIds
                new PlaceTypesSelection().delete(cr);

                // 2nd: clear places
                new PlacesSelection().delete(cr);

                // 3nd: clear tasks
                new TasksSelection().delete(cr);

                // 4th: clear tasks (v2)
                new TasksV2Selection().delete(cr);

                return null;
            }
        }.run();
    }

    /**
     * Creater if 'demo' database
     */
    public void initDB() throws OpsException {
        // clean the database
        place().deleteSync(-1);
        placeType().deleteSync(-1);
        task().deleteSync(-1);

        // initial structure
        // Nb: all these need to have fixed IDs
        final PlaceType[] mTypes = new PlaceType[]{
                new PlaceType("School",      0xFF0000FF),
                new PlaceType("Food shop",   0xFF00FF00),
                new PlaceType("Home care",   0xFFFF0000),
                new PlaceType("Library",     0xFFFFFF00),
                new PlaceType("Sport goods", 0xFF00FFFF),
                new PlaceType("Окей",        0xFF70FFFF),
        };

        // write place typeIds and re-query it to get proper IDs
        placeType().addOrModifySync(mTypes);

        PlaceType[] newTypes;
        try {
            newTypes = placeType().queryListSync();
        } catch (OpsException e) {
            throw new AssertionError(e.getMessage());
        }
        if (newTypes == null)
            throw new AssertionError("Error: cannot read place typeIds from database!");

        // fix IDs of place typeIds in array of places
        for (PlaceType pt : mTypes) {
            // set ID based on name
            long id = -1;
            for (PlaceType placeType : newTypes)
                if (placeType.name.equals(pt.name))
                    id = placeType.id;

            if (id == -1)
                throw new AssertionError("Error: cannot find placetype with name " + pt.name);

            pt.id = id;
        }

        final PlaceData[] mPlaces = new PlaceData[]{
                new PlaceData("home", 59.73057, 30.564674),

                new PlaceData("Школа", 59.722935, 30.564836,
                        new ArrayList<Long>() {{
                            add(mTypes[0].id);
                        }}),

                new PlaceData("100% вкуса", 59.730157, 30.565222,
                        new ArrayList<Long>() {{
                            add(mTypes[1].id);
                            add(mTypes[2].id);
                        }}),

                new PlaceData("Окей Трудящихся 12", 59.737183, 30.572005,
                        new ArrayList<Long>() {{
                            add(mTypes[1].id);
                            add(mTypes[2].id);
                            add(mTypes[4].id);
                            add(mTypes[5].id);
                        }}),

                new PlaceData("Окей Тверская 36", 59.740266, 30.611288,
                        new ArrayList<Long>() {{
                            add(mTypes[1].id);
                            add(mTypes[2].id);
                            add(mTypes[4].id);
                            add(mTypes[5].id);
                        }}),

                new PlaceData("Окей Октябрьская 8", 59.738983, 30.622903,
                        new ArrayList<Long>() {{
                            add(mTypes[1].id);
                            add(mTypes[2].id);
                            add(mTypes[4].id);
                            add(mTypes[5].id);
                        }}),

                new PlaceData("Офис", 59.925173, 30.386341),
        };

        // write places to database
        place().addOrModifySync(mPlaces);

        PlaceData[] newplaces = place().queryListSync();
        for (PlaceData p : mPlaces) {
            // set ID based on name
            long id = -1;
            for (PlaceData place : newplaces)
                if (place.name.equals(p.name))
                    id = place.id;

            if (id == -1)
                throw new AssertionError("Error: cannot find placetype with name " + p.name);

            p.id = id;
        }

        final TaskData[] mTasks = new TaskData[]{
                new TaskData()
                        .setDescription("Купить пива")
                        .attachPlace(mPlaces[2].id, false) // "100% вкуса"
                        .setExpirationDate(DateTime.now()) // now
                        .setEnabled(true),
                new TaskData()
                        .setDescription("Принести домой книгу")
                        .attachPlace(mPlaces[6].id, false) // "Офис"
                        .setExpirationDate(DateTime.now().plusDays(1)) // tomorrow
                        .setEnabled(true),
                new TaskData()
                        .setDescription("Купить (огромный список покупок)")
                        .attachPlaceType(mTypes[5].id, false) // группа "Окей"
                        .setExpirationDate(new DateTime(2015, 10, 19, 00, 00, 01))
                        .setEnabled(true),
                new TaskData()
                        .setDescription("Сотворить что-нибудь странное")
                        .setExpirationDate(new DateTime(2015, 10, 18, 00, 00, 01)) // New Year!
                        .setEnabled(true),
        };

        // write tasks to database
        task().addOrModifySync(mTasks);
    }
}
