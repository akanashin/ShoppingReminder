package utils.datatypes;

/**
 * Created by akana_000 on 6/20/2015.
 */
public class PlaceType {
    public Integer id; // this is unique key in database
    public String  name;
    public Integer color;

    // this class is just a storage for returning usage statistics for this type
    public class Usage {
        public Integer n_places;
        public Integer n_tasks;
    }
}
