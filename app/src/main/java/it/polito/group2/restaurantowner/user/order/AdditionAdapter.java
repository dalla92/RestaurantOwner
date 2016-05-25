package it.polito.group2.restaurantowner.user.order;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.MealAddition;

/**
 * Created by Filippo on 10/05/2016.
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
        public CheckBox checkbox;
        public RelativeLayout box;

        public AdditionViewHolder(View view){
            super(view);
            name = (TextView) itemView.findViewById(R.id.addition_name);
            price = (TextView) itemView.findViewById(R.id.addition_price);
            box = (RelativeLayout) itemView.findViewById(R.id.addition_box);
            checkbox = (CheckBox) itemView.findViewById(R.id.check);
        }
    }

    @Override
    public AdditionAdapter.AdditionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_fragment_addition_item, parent, false);
        return new AdditionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AdditionAdapter.AdditionViewHolder holder, int position) {
        holder.name.setText(additionList.get(position).getMeal_addition_name());
        holder.price.setText(formatEuro(additionList.get(position).getMeal_addition_price()));

        holder.box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.checkbox.setChecked(holder.checkbox.isSelected() ? false : true);
            }
        });

        holder.checkbox.setChecked(additionList.get(position).getAdditionSelected());
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                AdditionModel element = (AdditionModel) holder.checkbox.getTag();
                element.setSelected(buttonView.isChecked());
            }
                });
    }

    @Override
    public int getItemCount() {
        return additionList.size();
    }

    private String formatEuro(double number) {
        return "+ € "+String.format("%10.2f", number);
    }
}
