package home.akanashin.shoppingreminder.utils.datatypes;

import java.util.ArrayList;

import static home.akanashin.shoppingreminder.utils.Utils.compare;

/**
 *  Data structure to
 */
public class TaskData {
    public long        id;
    public String      name;
    public String      remark;     // remark for this task

    public String      expiration; // expiration data (if present)

    // Can be used only one property at a time
    // array of place types for this task
    public ArrayList<PlaceType> types;

    // array of places for this task
    public ArrayList<PlaceData> places;

    public TaskData(String aName, String aExpiration, String aRemark,
                    ArrayList<PlaceType> aTypes, ArrayList<PlaceData> aPlaces) {
        id    = 0; // this constructor always creates new object which does not have database ID
        name  = aName;
        expiration = aExpiration;
        remark  = aRemark;

        if ((aTypes != null && !aTypes.isEmpty())
                && (aPlaces != null && !aPlaces.isEmpty()))
            throw new AssertionError("Either types or places must be empty");

        types  = aTypes;
        places = aPlaces;
    }
    public TaskData(long aId,
                    String aName, String aExpiration, String aRemark,
                    ArrayList<PlaceType> aTypes, ArrayList<PlaceData> aPlaces) {
        id    = aId;
        name  = aName;
        expiration = aExpiration;
        remark = aRemark;

        if ((aTypes != null && !aTypes.isEmpty())
                && (aPlaces != null && !aPlaces.isEmpty()))
            throw new AssertionError("Either types or places must be empty");

        types  = aTypes;
        places = aPlaces;
    }

    /**
     * Comparator of two TaskData objects
     *  do not compare IDs
     * @param object
     * @return
     */
    public boolean equals(Object object) {
        if(!(object instanceof TaskData))
            throw new ClassCastException("Object is not TaskData");

        // check location
        TaskData other = (TaskData) object;
        if ( !compare(name,other.name)
                || !compare(expiration,other.expiration))
            return false;

        // check types
        if (types != null) {
            if (!types.equals(other.types))
                return false;
        } else
            if (other.types != null)
                return false;

        // check places
        if (places != null) {
            // task only contains some data for place
            for(int i = 0; i < places.size(); i++) {
                if (places.get(i).id != other.places.get(i).id
                    || !compare(places.get(i).name, other.places.get(i).name))
                    return false;
            }
        } else
        if (other.places != null)
            return false;

        return true;
    }
}
