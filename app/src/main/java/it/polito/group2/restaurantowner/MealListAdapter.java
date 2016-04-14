package it.polito.group2.restaurantowner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by TheChuck on 14/04/2016.
 */
public class MealListAdapter extends ArrayAdapter<OrderedMeal> {

    public MealListAdapter(Context context, ArrayList<OrderedMeal> ordered_meals) {
        super(context, 0, ordered_meals);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OrderedMeal meal = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.meal_item, parent, false);
        }

        TextView meal_name = (TextView) convertView.findViewById(R.id.meal_name);
        TextView meal_quantity = (TextView) convertView.findViewById(R.id.meal_quantity);

        meal_name.setText(meal.getMeal_name());
        meal_quantity.setText(String.format("%d", meal.getQuantity()));
        return convertView;
    }

}
