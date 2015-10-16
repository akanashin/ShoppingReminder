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

    // array of typeIds for this place
    public ArrayList<Long> typeIds;

    /*
     *  Checks whether this type is one of typeIds for current place
     */
    public Boolean isOneOfTypes(Long ptId) {
        for(Long id : typeIds)
            if (id == ptId)
                return true;

        return false;
    }

    public PlaceData(String aName, Double aLat, Double aLong) {
        id    = 0; // this constructor always creates new object which does not have database ID
        name  = aName;
        loc   = new LatLng(aLat, aLong);
        typeIds = new ArrayList<>();
    }
    public PlaceData(String aName, Double aLat, Double aLong, ArrayList<Long> aTypes) {
        id    = 0; // this constructor always creates new object which does not have database ID
        name  = aName;
        loc   = new LatLng(aLat, aLong);
        typeIds = aTypes;
    }
    public PlaceData(long aId, String aName, Double aLat, Double aLong, ArrayList<Long> aTypes) {
        id    = aId;
        name  = aName;
        loc   = new LatLng(aLat, aLong);
        typeIds = aTypes;
    }


    public static PlaceData findById(PlaceData[] collection, long id) {
        for (PlaceData placeData : collection)
            if (placeData.id == id)
                return placeData;

        return null;
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

        // check typeIds
        return typeIds.equals(other.typeIds);
    }
}
