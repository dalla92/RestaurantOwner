package it.polito.group2.restaurantowner.owner;

import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;

/**
 * Created by TheChuck on 14/04/2016.
 */
public class MealListDialog extends DialogFragment {

    private ListView list_view;

    public static MealListDialog newInstance(ArrayList<OrderedMeal> ordered_meals) {
        MealListDialog dialog = new MealListDialog();
        Bundle args = new Bundle();
        args.putSerializable("data", ordered_meals);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.takeaway_reservation_popup, null, false);
        list_view = (ListView) view.findViewById(R.id.meal_list_view);

        getDialog().setTitle(R.string.ordered_meals);
        TextView title = (TextView) getDialog().findViewById(android.R.id.title);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setTextColor(Color.WHITE);
        title.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        ArrayList<OrderedMeal> ordered_meals = (ArrayList<OrderedMeal>) getArguments().getSerializable("data");
        MealListAdapter adapter = new MealListAdapter(getActivity(), ordered_meals);

        list_view.setAdapter(adapter);

        return view;
    }

}
