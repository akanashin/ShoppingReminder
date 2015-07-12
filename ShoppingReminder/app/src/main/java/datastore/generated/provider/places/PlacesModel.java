/**
 *  This class was generated by Android-contentprovider-generator software
 *       (https://github.com/BoD/android-contentprovider-generator)
 */
package datastore.generated.provider.places;

import android.support.annotation.NonNull;

import datastore.generated.provider.base.BaseModel;

/**
 * Places
 */
public interface PlacesModel extends BaseModel {

    /**
     * Name of place
     * Cannot be {@code null}.
     */
    @NonNull
    String getName();

    /**
     * Location of the place (latitude)
     */
    double getLatitude();

    /**
     * Location of the place (longitude)
     */
    double getLongitude();
}
