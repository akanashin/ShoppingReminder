package home.akanashin.shoppingreminder.activities.newtask;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import home.akanashin.shoppingreminder.R;

/**
 * Created by akana_000 on 10/11/2015.
 */
// this fragment shows expiration settings of task
public class ExpirationFragment extends android.support.v4.app.Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//            if (getArguments() == null)
//                throw new RuntimeException("Logical error: received empty bundle!");

//            mPriceData = new Gson().fromJson(getArguments().getString(INTENT_PRODUCT_INFO), PriceData.class);
//            if (mPriceData == null)
//                throw new RuntimeException("Logical error: bundle does not contain price info");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_new_task_expiration, container, false);
/*
            // name and barcode of the item
            ((TextView)v.findViewById(R.id.tvName)).setText(mPriceData.product.name);

            Double[] minMedMaxPrices = getMinMedMaxPrices(mPriceData);
            ((TextView)v.findViewById(R.id.tvMinPrice)).setText("" + minMedMaxPrices[0]);
            ((TextView)v.findViewById(R.id.tvMediumPrice)).setText("" + minMedMaxPrices[1]);
            ((TextView)v.findViewById(R.id.tvMaxPrice)).setText("" + minMedMaxPrices[2]);

            // get picture of the item and show it
            if (mPriceData.product.image != null && !mPriceData.product.image.isEmpty()) {
                byte[] imageData = Base64.decode(mPriceData.product.image, Base64.DEFAULT);
                if (imageData != null && imageData.length != 0) {
                    ByteArrayInputStream is = new ByteArrayInputStream(imageData);
                    Drawable drw = Drawable.createFromStream(is, "something");
                    if (drw != null)
                        ((ImageView)v.findViewById(R.id.ivPhoto)).setImageDrawable(drw);
                }
            }
            */
        return v;
    }
}
