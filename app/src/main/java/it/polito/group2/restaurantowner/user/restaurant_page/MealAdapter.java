package it.polito.group2.restaurantowner.user.restaurant_page;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.Meal;

/**
 * Created by TheChuck on 07/05/2016.
 */
public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private ArrayList<Meal> meals;

    public MealAdapter(ArrayList<Meal> meals) {
        this.meals = meals;
    }

    public class MealViewHolder extends RecyclerView.ViewHolder {
        public TextView name, price;

        public MealViewHolder(View view){
            super(view);
            name = (TextView) itemView.findViewById(R.id.user_restaurant_menu_meal_name);
            price = (TextView) itemView.findViewById(R.id.user_restaurant_menu_meal_price);
        }

    }

    @Override
    public MealAdapter.MealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_restaurant_menu_item, parent, false);
        return new MealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MealAdapter.MealViewHolder holder, int position) {
        holder.name.setText(meals.get(position).getMeal_name());
        holder.price.setText(meals.get(position).getMeal_price() + "\u20ac");
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }
}
