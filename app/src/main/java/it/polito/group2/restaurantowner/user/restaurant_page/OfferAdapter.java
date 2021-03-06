package it.polito.group2.restaurantowner.user.restaurant_page;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.Offer;
import it.polito.group2.restaurantowner.owner.my_offers.OfferCategoryAdapter;
import it.polito.group2.restaurantowner.owner.my_offers.OfferMealAdapter;
import it.polito.group2.restaurantowner.owner.offer.OfferActivity;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder> {

    private ArrayList<Offer> offerList;
    private final ArrayList<Meal> mealRestaurantList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);
    private Context context;

    public OfferAdapter(ArrayList<Offer> offers, Context context, ArrayList<Meal> meals) {
        this.offerList = offers;
        this.context = context;
        this.mealRestaurantList = meals;
    }

    public class OfferViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView description;
        public TextView target;
        public TextView from;
        public TextView to;
        public TextView weekdays;
        public TextView discount;
        public TextView applied;
        public RecyclerView categoriesList;
        public RecyclerView mealsList;
        public LinearLayout details;

        public OfferViewHolder(View view){
            super(view);

            name = (TextView) itemView.findViewById(R.id.offer_name);
            description = (TextView) itemView.findViewById(R.id.offer_desc);
            target = (TextView) itemView.findViewById(R.id.offer_target);
            from = (TextView) itemView.findViewById(R.id.offer_from);
            to = (TextView) itemView.findViewById(R.id.offer_to);
            weekdays = (TextView) itemView.findViewById(R.id.offer_weekdays);
            discount = (TextView) itemView.findViewById(R.id.offer_discount);
            applied = (TextView) itemView.findViewById(R.id.offer_on_what);
            categoriesList = (RecyclerView) itemView.findViewById(R.id.category_list);
            mealsList = (RecyclerView) itemView.findViewById(R.id.meal_list);
            details = (LinearLayout) itemView.findViewById(R.id.offer_details);
        }

    }

    @Override
    public OfferAdapter.OfferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_restaurant_offer, parent, false);
        return new OfferViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OfferViewHolder holder, int position) {

        holder.name.setText(offerList.get(position).getOfferName());
        /*holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.details.getVisibility() == View.GONE)
                    holder.details.setVisibility(View.VISIBLE);
                else
                    holder.details.setVisibility(View.GONE);
            }
        });
        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.details.setVisibility(View.GONE);
            }
        });*/

        holder.description.setText(offerList.get(position).getOfferDescription());

        if(offerList.get(position).getOfferAtLunch() && offerList.get(position).getOfferAtDinner())
            holder.target.setText(context.getString(R.string.owner_myoffers_lable_wholeday));
        else if(offerList.get(position).getOfferAtLunch())
            holder.target.setText(context.getString(R.string.owner_myoffers_lable_targetlunch));
        else if(offerList.get(position).getOfferAtLunch())
            holder.target.setText(context.getString(R.string.owner_myoffers_lable_targetdinner));
        else
            holder.target.setText(context.getString(R.string.owner_myoffers_lable_never));

        holder.from.setText(dateFormat.format(offerList.get(position).startToCalendar().getTime()));
        holder.to.setText(dateFormat.format(offerList.get(position).stopToCalendar().getTime()));

        String[] weekdays = context.getResources().getStringArray(R.array.owner_myoffers_lable_weekdaysnames);
        ArrayList<String> weekvalues = new ArrayList<>();
        for(int i=0;i<7;i++) {
            if(offerList.get(position).isWeekDayInOffer(i)){
                weekvalues.add(weekdays[i]);
            }
        }
        holder.weekdays.setText(TextUtils.join(", ", weekvalues));

        holder.discount.setText(Integer.toString(offerList.get(position).getOfferPercentage()));

        if(offerList.get(position).getOfferForTotal()) {
            holder.applied.setText(context.getString(R.string.owner_myoffers_lable_everything));
            holder.categoriesList.setVisibility(View.GONE);
            holder.mealsList.setVisibility(View.GONE);
        } else if(offerList.get(position).getOfferForCategory()) {
            holder.applied.setText(context.getString(R.string.owner_myoffers_lable_categories));
            holder.categoriesList.setVisibility(View.VISIBLE);
            holder.mealsList.setVisibility(View.GONE);
            holder.categoriesList.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
            holder.categoriesList.setNestedScrollingEnabled(false);
            OfferCategoryAdapter adapter = new OfferCategoryAdapter(offerList.get(position).getCategoryList());
            holder.categoriesList.setAdapter(adapter);
        } else if(offerList.get(position).getOfferForMeal()) {
            holder.applied.setText(context.getString(R.string.owner_myoffers_lable_meals));
            holder.mealsList.setVisibility(View.VISIBLE);
            holder.categoriesList.setVisibility(View.GONE);
            holder.mealsList.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
            holder.mealsList.setNestedScrollingEnabled(false);
            OfferMealAdapter adapter = new OfferMealAdapter(getMealNameList(position));
            holder.mealsList.setAdapter(adapter);
        }

    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    private ArrayList<String> getMealNameList(int position) {
        ArrayList<String> list = new ArrayList<>();
        for(Meal m : mealRestaurantList) {
            if(offerList.get(position).isMealInOffer(m))
                list.add(m.getMeal_name());
        }
        return list;
    }

}
