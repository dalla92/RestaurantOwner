package it.polito.group2.restaurantowner.user.my_orders;

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
 * Created by Filippo on 13/05/2016.
 */
public class AdditionAdapter extends RecyclerView.Adapter<AdditionAdapter.AdditionViewHolder> {

    private final ArrayList<MealAddition> additionList;
    private final Context context;

    public AdditionAdapter(Context context, ArrayList<MealAddition> list) {
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
    public AdditionAdapter.AdditionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_myorders_activity_addition_item, parent, false);
        return new AdditionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AdditionAdapter.AdditionViewHolder holder, int position) {
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
