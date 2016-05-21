package it.polito.group2.restaurantowner.user.order;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.polito.group2.restaurantowner.R;

/**
 * Created by Filippo on 10/05/2016.
 */
public class AdditionAdapter extends RecyclerView.Adapter<AdditionAdapter.AdditionViewHolder> {

    private final ArrayList<AdditionModel> modelList;
    private final Context context;

    public AdditionAdapter(Context context, ArrayList<AdditionModel> list) {
        this.context = context;
        this.modelList = list;
    }

    public class AdditionViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView price;
        public CheckBox checkbox;

        public AdditionViewHolder(View view){
            super(view);
            name = (TextView) itemView.findViewById(R.id.addition_name);
            price = (TextView) itemView.findViewById(R.id.addition_price);
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
        holder.name.setText(modelList.get(position).getName().toString());
        holder.price.setText(formatEuro(modelList.get(position).getAddition().getPrice()));
        holder.checkbox.setChecked(modelList.get(position).isSelected());
        holder.checkbox
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

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
        return modelList.size();
    }

    private String formatEuro(double number) {
        return "+ € "+String.format("%10.2f", number);
    }
}
