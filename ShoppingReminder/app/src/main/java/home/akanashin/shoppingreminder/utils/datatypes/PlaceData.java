package home.akanashin.shoppingreminder.utils.datatypes;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import static home.akanashin.shoppingreminder.utils.Utils.compare;

/**
 * Created by akana_000 on 6/19/2015.
 */
public class PlaceData {
    public long        id;
    public LatLng      loc;
    public String      name;

    // array of types for this place
    public ArrayList<PlaceType> types;

    /*
     *  Checks whether this type is one of types for current place
     */
    public Boolean isOneOfTypes(PlaceType placeType) {
        for(PlaceType p: types)
            if (p.id == placeType.id)
                return true;

        return false;
    }

    public PlaceData(String aName, Double aLat, Double aLong, ArrayList<PlaceType> aTypes) {
        id    = 0; // this constructor always creates new object which does not have database ID
        name  = aName;
        loc   = new LatLng(aLat, aLong);
        types = aTypes;
    }
    public PlaceData(long aId, String aName, Double aLat, Double aLong, ArrayList<PlaceType> aTypes) {
        id    = aId;
        name  = aName;
        loc   = new LatLng(aLat, aLong);
        types = aTypes;
    }

    /**
     * Comparator of two PlaceData objects
     *  do not compare IDs
     * @param object
     * @return
     */
    public boolean equals(Object object) {
        if(!(object instanceof PlaceData))
            throw new ClassCastException("Object is not PlaceData");

        PlaceData other = (PlaceData) object;
        // check location
        if ( !compare(loc,other.loc)
          || !compare(name,other.name) )
            return false;

        // check types
        return types.equals(other.types);
    }
}
