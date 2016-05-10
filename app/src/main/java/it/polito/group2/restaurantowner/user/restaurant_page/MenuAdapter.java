package it.polito.group2.restaurantowner.user.restaurant_page;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.Meal;
import it.polito.group2.restaurantowner.data.MenuCategory;

/**
 * Created by TheChuck on 07/05/2016.
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder>{

    private ArrayList<MenuCategory> categories;
    private Context context;

    public MenuAdapter(ArrayList<MenuCategory> categories, Context context) {
        this.categories = categories;
        this.context = context;
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public RecyclerView meals;

        public MenuViewHolder(View view){
            super(view);
            name = (TextView) itemView.findViewById(R.id.user_restaurant_menu_category_name);
            meals = (RecyclerView) itemView.findViewById(R.id.user_restaurant_menu_meals);
        }

    }

    @Override
    public MenuAdapter.MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_restaurant_menu_category, parent, false);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuAdapter.MenuViewHolder holder, int position) {
        holder.name.setText(categories.get(position).getName());
        ArrayList<Meal> meals = getMealsFromCategory();
        holder.meals.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
        holder.meals.setNestedScrollingEnabled(false);

        MealAdapter adapter = new MealAdapter(meals);
        holder.meals.setAdapter(adapter);
    }

    private ArrayList<Meal> getMealsFromCategory() {
        ArrayList<Meal> meals = new ArrayList<>();
        Meal m1 = new Meal();
        m1.setMeal_name("Pasta alla carbonara");
        m1.setMeal_price(4.5);
        Meal m2 = new Meal();
        m2.setMeal_name("Tonno alla piastra");
        m2.setMeal_price(4.5);
        Meal m3 = new Meal();
        m3.setMeal_name("Tiramisù");
        m3.setMeal_price(4.5);
        meals.add(m1);
        meals.add(m2);
        meals.add(m3);

        return meals;
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
