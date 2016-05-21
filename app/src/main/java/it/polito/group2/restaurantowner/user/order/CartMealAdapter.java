package it.polito.group2.restaurantowner.user.order;

import android.app.Activity;
import android.content.Context;
import android.content.SyncStatusObserver;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.user.my_orders.*;

/**
 * Created by Filippo on 12/05/2016.
 */
public class CartMealAdapter extends RecyclerView.Adapter<CartMealAdapter.MealViewHolder> {

    private final ArrayList<MealModel> modelList;
    private final Context context;

    public CartMealAdapter(Context context, ArrayList<MealModel> list) {
        this.context = context;
        this.modelList = list;
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
    public CartMealAdapter.MealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_fragment_cart_meal, parent, false);
        return new MealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CartMealAdapter.MealViewHolder holder, int position) {
        holder.name.setText(modelList.get(position).getName().toString());
        holder.category.setText(modelList.get(position).getMeal().getCategory().toString());
        holder.price.setText(formatEuro(modelList.get(position).getMeal().getMeal_price()));
        holder.quantity.setText(modelList.get(position).getQuantity().toString());
        holder.additionList.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
        holder.additionList.setNestedScrollingEnabled(false);
        CartAdditionAdapter adapter = new CartAdditionAdapter(context, modelList.get(position).getAdditionModel());
        holder.additionList.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    private String formatEuro(double number) {
        return "€ "+String.format("%10.2f", number);
    }
}