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
import it.polito.group2.restaurantowner.user.order.MealModel;

/**
 * Created by Filippo on 13/05/2016.
 */
public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private final ArrayList<MealModel> modelList;
    private final Context context;

    public MealAdapter(Context context, ArrayList<MealModel> list) {
        this.context = context;
        this.modelList = list;
    }

    public class MealViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView category;
        public TextView price;
        public RecyclerView additionList;

        public MealViewHolder(View view){
            super(view);
            name = (TextView) itemView.findViewById(R.id.meal_name);
            category = (TextView) itemView.findViewById(R.id.category_name);
            price = (TextView) itemView.findViewById(R.id.meal_price);
            additionList = (RecyclerView) itemView.findViewById(R.id.order_meal_addition_list);
        }
    }

    @Override
    public MealAdapter.MealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.myorders_activity_meal_item, parent, false);
        return new MealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MealAdapter.MealViewHolder holder, int position) {
        holder.name.setText(modelList.get(position).getName().toString());
        holder.category.setText(modelList.get(position).getMeal().getCategory().toString());
        holder.price.setText(formatEuro(modelList.get(position).getMeal().getMeal_price()));
        holder.additionList.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
        holder.additionList.setNestedScrollingEnabled(false);
        AdditionAdapter adapter = new AdditionAdapter(context, modelList.get(position).getAdditionModel());
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