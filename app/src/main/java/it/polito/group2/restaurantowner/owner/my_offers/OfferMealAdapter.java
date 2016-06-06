package it.polito.group2.restaurantowner.owner.my_offers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;

/**
 * Created by Filippo on 04/06/2016.
 */
public class OfferMealAdapter extends RecyclerView.Adapter<OfferMealAdapter.OfferMealViewHolder> {

    private final ArrayList<String> mealList;

    public OfferMealAdapter(ArrayList<String> list) {
        this.mealList = list;
    }

    public class OfferMealViewHolder extends RecyclerView.ViewHolder {
        public TextView name;

        public OfferMealViewHolder(View view) {
            super(view);
            name = (TextView) itemView.findViewById(R.id.meal_name);
        }
    }

    @Override
    public OfferMealAdapter.OfferMealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.owner_myoffers_offer_meal_item, parent, false);
        return new OfferMealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OfferMealAdapter.OfferMealViewHolder holder, int position) {
        holder.name.setText(mealList.get(position));
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

}