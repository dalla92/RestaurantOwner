package it.polito.group2.restaurantowner.owner.offer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.Offer;


/**
 * Created by Filippo on 02/06/2016.
 */
public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private final ArrayList<Meal> mealList;
    private final Context context;
    private final Offer offer;

    public MealAdapter(Context context, ArrayList<Meal> list, Offer offer) {
        this.context = context;
        this.mealList = list;
        this.offer = offer;
    }

    public class MealViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public CheckBox checkbox;

        public MealViewHolder(View view) {
            super(view);
            name = (TextView) itemView.findViewById(R.id.meal_name);
            checkbox = (CheckBox) itemView.findViewById(R.id.check);
        }
    }

    @Override
    public MealAdapter.MealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.owner_offer_fragment_meal_item, parent, false);
        return new MealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MealAdapter.MealViewHolder holder, int position) {
        holder.name.setText(mealList.get(position).getMeal_name());
        holder.checkbox.setChecked(offer.isInMealList(mealList.get(position)));
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                Meal element = (Meal) holder.checkbox.getTag();
                if(buttonView.isChecked()) {
                    offer.addMealInOffer(element);
                } else {
                    offer.delMealInOffer(element);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

}