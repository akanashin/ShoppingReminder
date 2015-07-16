package utils.datatypes;

import static utils.Utils.compare;

/**
 * Created by akana_000 on 6/20/2015.
 */
public class PlaceType {
    public long id; // this is unique key in database
    public String name;
    public int color;

    // this class is just a storage for returning usage statistics for this type
    public static class Usage {
        public int n_places;
        public int n_tasks;
    }


    public PlaceType(String aName, int aColor) {
        id = 0; // this constructor always creates new object which does not have database ID
        name = aName;
        color = aColor;
    }

    public PlaceType(long aId, String aName, int aColor) {
        id = aId;
        name = aName;
        color = aColor;
    }

    /**
     * Comparator of two placeType objects
     * (i don't compare IDs here)
     *
     * @return true if objects are equal
     * @params two objects
     */
    public boolean equals(Object pt2) {
        if(!(pt2 instanceof PlaceType))
            throw new ClassCastException("Object is not PlaceType");

        // auto false (this object is NOT null)
        if (pt2 == null)
            return false;

        // are this object and pt2 the same object?
        if (this == pt2)
            return true;

        // now we know that pt2 is not null
        //  compare this and pt2 field by field
        //  (ignoring ID)
        PlaceType other = (PlaceType)pt2;
        return compare(name, other.name)
                && compare(color, other.color);
    }

}
