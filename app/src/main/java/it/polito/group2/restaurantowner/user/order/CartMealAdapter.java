package it.polito.group2.restaurantowner.user.order;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.MealAddition;
import it.polito.group2.restaurantowner.firebasedata.Offer;
import it.polito.group2.restaurantowner.owner.Swipe_Detector;

public class CartMealAdapter extends RecyclerView.Adapter<CartMealAdapter.MealViewHolder> {

    private final ArrayList<Meal> mealList;
    private final Offer offer;
    private final Context context;
    private final CartFragment fragment;

    public CartMealAdapter(Context context, ArrayList<Meal> list, Offer offer, CartFragment fragment) {
        this.context = context;
        this.mealList = list;
        this.offer = offer;
        this.fragment = fragment;
    }

    public class MealViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView price;
        public TextView quantity;
        public RecyclerView additionList;
        public ImageView offer_active;
        public RelativeLayout item;
        public ImageView del_btn;
        public LinearLayout additionResume;
        public TextView additionResumePrice;
        public ImageView additionResumeIcon;

        public MealViewHolder(View view){
            super(view);
            name = (TextView) itemView.findViewById(R.id.meal_name);
            price = (TextView) itemView.findViewById(R.id.meal_price);
            quantity = (TextView) itemView.findViewById(R.id.meal_quantity);
            additionList = (RecyclerView) itemView.findViewById(R.id.order_meal_addition_list);
            offer_active = (ImageView) itemView.findViewById(R.id.offer_active);
            item = (RelativeLayout) itemView.findViewById(R.id.meal_item);
            del_btn = (ImageView) itemView.findViewById(R.id.meal_delete);
            additionResume = (LinearLayout) itemView.findViewById(R.id.order_meal_addition_resume);
            additionResumePrice = (TextView) itemView.findViewById(R.id.order_meal_addition_resume_price);
            additionResumeIcon = (ImageView) itemView.findViewById(R.id.order_meal_addition_resume_icon);
        }
    }

    @Override
    public CartMealAdapter.MealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_order_fragment_cart_meal, parent, false);
        return new MealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CartMealAdapter.MealViewHolder holder, final int position) {
        holder.del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.onMealDeleted(mealList.get(position));
            }
        });

        if(mealList.get(position).getMeal_additions().size() > 0) {
            holder.additionResume.setVisibility(View.VISIBLE);
            double addP = 0.0;
            for(MealAddition a : mealList.get(position).getMeal_additions().values()) {
                addP += a.getMeal_addition_price();
            }
            holder.additionResumePrice.setText(formatEuro(addP));
            holder.additionList.setVisibility(View.GONE);
            holder.additionResume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.additionList.getVisibility() == View.GONE) {
                        holder.additionResumeIcon.setImageDrawable(context.getResources().getDrawable(android.R.drawable.arrow_down_float));
                        holder.additionList.setVisibility(View.VISIBLE);
                    } else  {
                        holder.additionResumeIcon.setImageDrawable(context.getResources().getDrawable(android.R.drawable.arrow_up_float));
                        holder.additionList.setVisibility(View.GONE);
                    }
                }
            });

            holder.additionList.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
            holder.additionList.setNestedScrollingEnabled(false);
            CartAdditionAdapter adapter = new CartAdditionAdapter(context, mealList.get(position).allAdditions());
            holder.additionList.setAdapter(adapter);

        } else {
            holder.additionResume.setVisibility(View.GONE);
        }

        holder.name.setText(mealList.get(position).getMeal_name());
        holder.quantity.setText(mealList.get(position).getMeal_quantity().toString());


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
        return "€ "+String.format("%5.2f", number);
    }

    private boolean isInOffer(Meal meal) {
        if(offer != null) {
            return offer.isMealInOffer(meal);
        }
        return false;
    }
}