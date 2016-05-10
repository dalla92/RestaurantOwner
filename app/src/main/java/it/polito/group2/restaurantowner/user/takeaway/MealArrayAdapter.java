package it.polito.group2.restaurantowner.user.takeaway;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Filippo on 10/05/2016.
 */
public class MealArrayAdapter extends ArrayAdapter<String> {

    private Context context;

    public MealArrayAdapter(Context context, int textViewResourceId,
                                    List<String> stringList) {
        super(context, textViewResourceId, stringList);

        this.context = context;
    }
}
