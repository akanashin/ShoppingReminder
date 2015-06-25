package utils.datatypes;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by akana_000 on 6/19/2015.
 */
public class PlaceData {
    public Integer     id;
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

}
