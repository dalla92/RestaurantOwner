package it.polito.group2.restaurantowner.user.my_orders;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import it.polito.group2.restaurantowner.R;

/**
 * Created by Filippo on 13/05/2016.
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final ArrayList<OrderModel> modelList;
    private final Context context;

    public OrderAdapter(Context context, ArrayList<OrderModel> list) {
        this.context = context;
        this.modelList = list;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public TextView restaurantName;
        public TextView price;
        public RecyclerView mealList;

        public OrderViewHolder(View view){
            super(view);
            date = (TextView) itemView.findViewById(R.id.order_date);
            restaurantName = (TextView) itemView.findViewById(R.id.restaurant_name);
            price = (TextView) itemView.findViewById(R.id.order_price);
            mealList = (RecyclerView) itemView.findViewById(R.id.order_meal_list);
        }
    }

    @Override
    public OrderAdapter.OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.myorders_activity_order_item, parent, false);
        return new OrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OrderAdapter.OrderViewHolder holder, int position) {
        Date date = modelList.get(position).getOrder().getTimestamp().getTime();
        SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
        holder.date.setText(formatDate.format(date));
        holder.restaurantName.setText(modelList.get(position).getOrder().getRestaurantID());
        holder.price.setText(formatEuro(modelList.get(position).getOrder().getPrice()));
        holder.mealList.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
        holder.mealList.setNestedScrollingEnabled(false);
        MealAdapter adapter = new MealAdapter(context, modelList.get(position).getMealList());
        holder.mealList.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    private String formatEuro(Float number) {
        if(number == null)
            return "€ 0.00";
        return "€ "+String.format("%10.2f", number);
    }
}