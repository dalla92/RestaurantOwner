package it.polito.group2.restaurantowner.user.my_orders;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.MealAddition;

/**
 * Created by Filippo on 13/05/2016.
 */
public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private final ArrayList<Meal> mealList;
    private final Context context;

    public MealAdapter(Context context, ArrayList<Meal> list) {
        this.context = context;
        this.mealList = list;
    }

    public class MealViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView category;
        public TextView price;
        public TextView quantity;
        public LinearLayout additionResume;
        public TextView additionResumePrice;

        public MealViewHolder(View view){
            super(view);
            name = (TextView) itemView.findViewById(R.id.meal_name);
            price = (TextView) itemView.findViewById(R.id.meal_price);
            quantity = (TextView) itemView.findViewById(R.id.meal_quantity);
            additionResume = (LinearLayout) itemView.findViewById(R.id.order_meal_addition_resume);
            additionResumePrice = (TextView) itemView.findViewById(R.id.order_meal_addition_resume_price);
        }
    }

    @Override
    public MealAdapter.MealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_myorders_activity_meal_item, parent, false);
        return new MealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MealAdapter.MealViewHolder holder, int position) {
        holder.name.setText(mealList.get(position).getMeal_name());
        holder.price.setText(formatEuro(mealList.get(position).getMeal_price()));
        holder.quantity.setText(mealList.get(position).getMeal_quantity().toString());
        if(mealList.get(position).getMeal_additions().size() > 0) {
            holder.additionResume.setVisibility(View.VISIBLE);
            double totalAdd = 0;
            for(MealAddition a : mealList.get(position).getMeal_additions().values())
                totalAdd += a.getMeal_addition_price();
            holder.additionResumePrice.setText(formatEuro(totalAdd));
        } else {
            holder.additionResume.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    private String formatEuro(double number) {
        return "€ "+String.format("%2.2f", number);
    }
}