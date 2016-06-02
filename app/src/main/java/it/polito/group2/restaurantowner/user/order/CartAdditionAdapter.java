package it.polito.group2.restaurantowner.user.order;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.MealAddition;

/**
 * Created by Filippo on 12/05/2016.
 */
public class CartAdditionAdapter extends RecyclerView.Adapter<CartAdditionAdapter.AdditionViewHolder> {

    private final ArrayList<MealAddition> additionList;
    private final Context context;

    public CartAdditionAdapter(Context context, ArrayList<MealAddition> list) {
        this.context = context;
        this.additionList = list;
    }

    public class AdditionViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView price;

        public AdditionViewHolder(View view){
            super(view);
            name = (TextView) itemView.findViewById(R.id.addition_name);
            price = (TextView) itemView.findViewById(R.id.addition_price);
        }
    }

    @Override
    public CartAdditionAdapter.AdditionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_order_fragment_cart_meal_addition, parent, false);
        return new AdditionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CartAdditionAdapter.AdditionViewHolder holder, int position) {
        holder.name.setText(additionList.get(position).getMeal_addition_name());
        holder.price.setText(formatEuro(additionList.get(position).getMeal_addition_price()));
    }

    @Override
    public int getItemCount() {
        return additionList.size();
    }

    private String formatEuro(double number) {
        return "€ "+String.format("%10.2f", number);
    }
}