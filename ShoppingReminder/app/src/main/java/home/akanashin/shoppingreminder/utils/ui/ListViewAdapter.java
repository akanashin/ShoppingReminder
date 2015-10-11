package home.akanashin.shoppingreminder.utils.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * This class is a template for using with ListViews.
 * Main idea: adapter does its stuff without caring about actual View content.
 * Implementation:
 *  Adapter is abstract class with method fillView which should be implemented in subclass
 *
 *  Upon creating Adapter takes layout ID
 *  Main logic about filling views is placed in fillView method which is used when system asks for View for particular item
 */
public abstract class ListViewAdapter<Params> extends ArrayAdapter<Params> {
    private int          mLayoutId;
    private Params[]     mParams;
    private Context      mContext;


    public ListViewAdapter(Context context, Params[] params, int layoutId) {
        super(context, -1, params);

        mLayoutId = layoutId;
        mParams   = params;
        mContext  = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // do not create new View if we already have one
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mLayoutId, parent, false);
        }

        // use subclass's logic to fill this view
        fillView(convertView, mParams[position]);

        return convertView;
    }

    // this method should be implemented in subclass
    protected abstract void fillView(View v, Params data);
}
