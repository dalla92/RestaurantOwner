package it.polito.group2.restaurantowner.owner.my_offers;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.Offer;

/**
 * Created by Filippo on 05/06/2016.
 */
public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder> {

    private final ArrayList<Offer> offerList;
    private final ArrayList<Meal> mealRestaurantList;
    private final Context context;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);

    public OfferAdapter(Context context, ArrayList<Offer> list, ArrayList<Meal> meals) {
        this.context = context;
        this.offerList = list;
        this.mealRestaurantList = meals;
    }

    public class OfferViewHolder extends RecyclerView.ViewHolder {
        public TextView id;
        public TextView name;
        public TextView enabled;
        public TextView description;
        public TextView target;
        public TextView from;
        public TextView to;
        public TextView weekdays;
        public TextView discount;
        public TextView applied;
        public RecyclerView categoriesList;
        public RecyclerView mealsList;

        public OfferViewHolder(View view) {
            super(view);
            id = (TextView) itemView.findViewById(R.id.offer_id);
            name = (TextView) itemView.findViewById(R.id.offer_name);
            enabled = (TextView) itemView.findViewById(R.id.offer_enabled);
            description = (TextView) itemView.findViewById(R.id.offer_description);
            target = (TextView) itemView.findViewById(R.id.offer_target);
            from = (TextView) itemView.findViewById(R.id.offer_from);
            to = (TextView) itemView.findViewById(R.id.offer_to);
            weekdays = (TextView) itemView.findViewById(R.id.offer_weekdays);
            discount = (TextView) itemView.findViewById(R.id.offer_discount);
            applied = (TextView) itemView.findViewById(R.id.offer_on_what);
            categoriesList = (RecyclerView) itemView.findViewById(R.id.category_list);
            mealsList = (RecyclerView) itemView.findViewById(R.id.meal_list);
        }
    }

    @Override
    public OfferAdapter.OfferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.owner_myoffers_activity_offer_item, parent, false);
        return new OfferViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OfferAdapter.OfferViewHolder holder, int position) {
        holder.id.setText(offerList.get(position).getOfferID());
        holder.name.setText(offerList.get(position).getOfferName());
        if(offerList.get(position).getOfferEnabled())
            holder.enabled.setVisibility(View.VISIBLE);
        else
            holder.enabled.setVisibility(View.GONE);
        holder.description.setText(offerList.get(position).getOfferDescription());

        if(offerList.get(position).getOfferAtLunch() && offerList.get(position).getOfferAtDinner())
            holder.target.setText(context.getString(R.string.owner_myoffers_lable_wholeday));
        else if(offerList.get(position).getOfferAtLunch())
            holder.target.setText(context.getString(R.string.owner_myoffers_lable_targetlunch));
        else if(offerList.get(position).getOfferAtLunch())
            holder.target.setText(context.getString(R.string.owner_myoffers_lable_targetdinner));
        else
            holder.target.setText(context.getString(R.string.owner_myoffers_lable_never));

        holder.from.setText(dateFormat.format(offerList.get(position).getOfferStartDate().getTime()));
        holder.to.setText(dateFormat.format(offerList.get(position).getOfferStopDate().getTime()));

        String[] weekdays = context.getResources().getStringArray(R.array.owner_myoffers_lable_weekdaysnames);
        ArrayList<String> weekvalues = new ArrayList<String>();
        for(Integer wDay : offerList.get(position).getOfferOnWeekDays().keySet()) {
            if(offerList.get(position).getOfferOnWeekDays().get(wDay)) {
                weekvalues.add(weekdays[wDay]);
            }
        }
        holder.weekdays.setText(TextUtils.join(", ", weekvalues));

        holder.discount.setText(offerList.get(position).getOfferPercentage().toString());

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
        ArrayList<String> list = new ArrayList<String>();
        for(Meal m : mealRestaurantList) {
            if(offerList.get(position).getOfferOnMeals().get(m.getMeal_id()))
                list.add(m.getMeal_name());
        }
        return list;
    }

}