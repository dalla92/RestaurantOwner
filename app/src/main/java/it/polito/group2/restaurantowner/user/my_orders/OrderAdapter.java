package it.polito.group2.restaurantowner.user.my_orders;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.Order;

/**
 * Created by Filippo on 13/05/2016.
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final ArrayList<Order> orderList;
    private final Context context;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);

    public OrderAdapter(Context context, ArrayList<Order> list) {
        this.context = context;
        this.orderList = list;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public TextView restaurantName;
        public TextView price;
        public RecyclerView mealList;
        public LinearLayout orderItem;

        public OrderViewHolder(View view){
            super(view);
            date = (TextView) itemView.findViewById(R.id.order_date);
            restaurantName = (TextView) itemView.findViewById(R.id.restaurant_name);
            price = (TextView) itemView.findViewById(R.id.order_price);
            mealList = (RecyclerView) itemView.findViewById(R.id.order_meal_list);
            orderItem = (LinearLayout) itemView.findViewById(R.id.order_item);
        }
    }

    @Override
    public OrderAdapter.OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_myorders_activity_order_item, parent, false);
        return new OrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OrderAdapter.OrderViewHolder holder, int position) {
        Date date = orderList.get(position).orderDateToCalendar().getTime();
        SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
        holder.date.setText(formatDate.format(date));

        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        DatabaseReference resReference = firebase.getReference("restaurants/" + orderList.get(position).getRestaurant_id() + "/restaurant_name");
        resReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                holder.restaurantName.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.price.setText(formatEuro(orderList.get(position).getOrder_price()));

        holder.mealList.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
        holder.mealList.setNestedScrollingEnabled(false);
        MealAdapter adapter = new MealAdapter(context, orderList.get(position).allMeals());
        holder.mealList.setAdapter(adapter);
        holder.mealList.setVisibility(View.GONE);

        holder.orderItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mealList.getVisibility() == View.VISIBLE) {
                    holder.mealList.setVisibility(View.GONE);
                } else {
                    holder.mealList.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private String formatEuro(Double number) {
        if(number == null)
            return "€ 0.00";
        return "€ "+String.format("%4.2f", number);
    }
}