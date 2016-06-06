package it.polito.group2.restaurantowner.user.order;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.Offer;

/**
 * Created by Filippo on 10/05/2016.
 */
public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private final ArrayList<Meal> mealList;
    private final Offer offer;
    private final Context context;

    public MealAdapter(Context context, ArrayList<Meal> list, Offer offer) {
        this.context = context;
        this.mealList = list;
        this.offer = offer;
    }

    public class MealViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView category;
        public TextView price;
        public ImageView offer_active;

        public MealViewHolder(View view){
            super(view);
            name = (TextView) itemView.findViewById(R.id.meal_name);
            category = (TextView) itemView.findViewById(R.id.category_name);
            price = (TextView) itemView.findViewById(R.id.meal_price);
            offer_active = (ImageView) itemView.findViewById(R.id.offer_active);
        }
    }

    @Override
    public MealAdapter.MealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_order_fragment_meal_item, parent, false);
        return new MealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MealAdapter.MealViewHolder holder, int position) {
        holder.name.setText(mealList.get(position).getMeal_name());
        holder.category.setText(mealList.get(position).getMeal_category());
        if(isInOffer(mealList.get(position))) {
            Calendar c = Calendar.getInstance();
            holder.price.setText(formatEuro(offer.getNewMealPrice(mealList.get(position),c)));
            holder.offer_active.setVisibility(View.VISIBLE);
        } else {
            holder.price.setText(formatEuro(mealList.get(position).getMeal_price()));
            holder.offer_active.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    private String formatEuro(double number) {
        return "€ "+String.format("%10.2f", number);
    }

    private boolean isInOffer(Meal meal) {
        if(offer != null) {
            return offer.isMealInOffer(meal);
        }
        return false;
    }
}