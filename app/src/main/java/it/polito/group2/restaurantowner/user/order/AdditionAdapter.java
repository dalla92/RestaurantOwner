package it.polito.group2.restaurantowner.user.order;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.MealAddition;

public class AdditionAdapter extends RecyclerView.Adapter<AdditionAdapter.AdditionViewHolder> {

    private final ArrayList<MealAddition> additionList;
    private final AdditionFragment fragment;

    public AdditionAdapter(ArrayList<MealAddition> list, AdditionFragment fragment) {
        this.additionList = list;
        this.fragment = fragment;
    }

    public class AdditionViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView price;
        public LinearLayout item;

        public AdditionViewHolder(View view) {
            super(view);
            name = (TextView) itemView.findViewById(R.id.addition_name);
            price = (TextView) itemView.findViewById(R.id.addition_price);
            item = (LinearLayout) itemView.findViewById(R.id.addition_item);
        }
    }

    @Override
    public AdditionAdapter.AdditionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_order_fragment_addition_item, parent, false);
        return new AdditionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AdditionAdapter.AdditionViewHolder holder, final int position) {
        holder.name.setText(additionList.get(position).getMeal_addition_name());
        holder.price.setText(formatEuro(additionList.get(position).getMeal_addition_price()));

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment.onAdditionSelected(additionList.get(position))) {
                    holder.name.setTextColor(v.getResources().getColor(R.color.colorAccent));
                    holder.price.setTextColor(v.getResources().getColor(R.color.colorAccent));
                } else {
                    holder.name.setTextColor(v.getResources().getColor(R.color.gray));
                    holder.price.setTextColor(v.getResources().getColor(R.color.gray));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return additionList.size();
    }

    private String formatEuro(double number) {
        return "+ € " + String.format("%2.2f", number);
    }
}
