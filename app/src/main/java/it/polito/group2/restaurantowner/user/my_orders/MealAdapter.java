package it.polito.group2.restaurantowner.user.my_orders;


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
 * Created by Filippo on 13/05/2016.
 */
public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private final ArrayList<Meal> mealList;
    private final Context context;

    public MealAdapter(Context context, ArrayList<Meal> list) {
        this.context = context;
        this.mealList = list;
    }

    public class MealViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView category;
        public TextView price;
        public TextView quantity;
        public RecyclerView additionList;

        public MealViewHolder(View view){
            super(view);
            name = (TextView) itemView.findViewById(R.id.meal_name);
            category = (TextView) itemView.findViewById(R.id.category_name);
            price = (TextView) itemView.findViewById(R.id.meal_price);
            quantity = (TextView) itemView.findViewById(R.id.meal_quantity);
            additionList = (RecyclerView) itemView.findViewById(R.id.order_meal_addition_list);
        }
    }

    @Override
    public MealAdapter.MealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_myorders_activity_meal_item, parent, false);
        return new MealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MealAdapter.MealViewHolder holder, int position) {
        holder.name.setText(mealList.get(position).getMeal_name());
        holder.category.setText(mealList.get(position).getMeal_category());
        holder.price.setText(formatEuro(mealList.get(position).getMeal_price()));
        holder.quantity.setText(mealList.get(position).getMeal_quantity());
        holder.additionList.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
        holder.additionList.setNestedScrollingEnabled(false);
        AdditionAdapter adapter = new AdditionAdapter(context, mealList.get(position).allAdditions());
        holder.additionList.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    private String formatEuro(double number) {
        return "€ "+String.format("%10.2f", number);
    }
}