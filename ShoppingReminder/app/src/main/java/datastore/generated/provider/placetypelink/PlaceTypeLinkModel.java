/**
 *  This class was generated by Android-contentprovider-generator software
 *       (https://github.com/BoD/android-contentprovider-generator)
 */
package datastore.generated.provider.placetypelink;

import datastore.generated.provider.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Entity joining places and types many-to-many
 */
public interface PlaceTypeLinkModel extends BaseModel {

    /**
     * Get the {@code place_id} value.
     */
    long getPlaceId();

    /**
     * Get the {@code place_type_id} value.
     */
    long getPlaceTypeId();
}