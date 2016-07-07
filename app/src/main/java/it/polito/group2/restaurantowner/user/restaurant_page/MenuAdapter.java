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
import it.polito.group2.restaurantowner.firebasedata.Meal;

/**
 * Created by TheChuck on 07/05/2016.
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder>{

    private ArrayList<String> categories;
    private ArrayList<Meal> meals;
    private Context context;


    public MenuAdapter(ArrayList<String> categories, ArrayList<Meal> meals, Context context) {
        this.categories = categories;
        this.meals = meals;
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
        String category = categories.get(position);
        ArrayList<Meal> meals = getMealsFromCategory(category);

        holder.name.setText(category);
        holder.meals.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
        holder.meals.setNestedScrollingEnabled(false);

        MealAdapter adapter = new MealAdapter(meals);
        holder.meals.setAdapter(adapter);
    }

    private ArrayList<Meal> getMealsFromCategory(String category) {
        ArrayList<Meal> targetMeals = new ArrayList<>();
        for(Meal m: meals){
            if(m.getMeal_category() != null) {
                if (m.getMeal_category().equals(category))
                    targetMeals.add(m);
            }
        }

        return targetMeals;
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
