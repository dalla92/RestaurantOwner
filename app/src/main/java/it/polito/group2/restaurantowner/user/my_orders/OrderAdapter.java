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
import it.polito.group2.restaurantowner.firebasedata.Order;

/**
 * Created by Filippo on 13/05/2016.
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final ArrayList<Order> orderList;
    private final Context context;

    public OrderAdapter(Context context, ArrayList<Order> list) {
        this.context = context;
        this.orderList = list;
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
        Date date = orderList.get(position).getOrder_date().getTime();
        SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
        holder.date.setText(formatDate.format(date));
        //TODO stampare il nome del ristorante e non il suo id
        holder.restaurantName.setText(orderList.get(position).getRestaurant_id());
        holder.price.setText(formatEuro(orderList.get(position).getOrder_price()));
        holder.mealList.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
        holder.mealList.setNestedScrollingEnabled(false);
        MealAdapter adapter = new MealAdapter(context, orderList.get(position).getOrder_meals());
        holder.mealList.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private String formatEuro(Double number) {
        if(number == null)
            return "€ 0.00";
        return "€ "+String.format("%10.2f", number);
    }
}