package it.polito.group2.restaurantowner.owner;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.MealAddition;
import it.polito.group2.restaurantowner.firebasedata.MealCategory;

/**
 * Created by Alessio on 12/04/2016.
 */
public class Adapter_Meals extends RecyclerView.Adapter<Adapter_Meals.MealViewHolder> implements ItemTouchHelperAdapter {
    private Activity activity;
    private ArrayList<Meal> meals;
    private static LayoutInflater inflater = null;
    public String meal_name;
    public int index;
    public String restaurant_id;
    public Meal meal_to_delete;

    public static class MealViewHolder extends RecyclerView.ViewHolder {
        ImageView MealImage;
        TextView MealName;
        TextView MealPrice;
        ImageView Type1;
        ImageView Type2;
        Switch availability;
        ProgressBar progressBar;

        MealViewHolder(View itemView) {
            super(itemView);
            MealImage = (ImageView) itemView.findViewById(R.id.meal_image);
            MealName = (TextView) itemView.findViewById(R.id.meal_name);
            MealPrice = (TextView) itemView.findViewById(R.id.meal_price);
            Type1 = (ImageView) itemView.findViewById(R.id.meal_type1);
            Type2 = (ImageView) itemView.findViewById(R.id.meal_type2);
            availability = (Switch) itemView.findViewById(R.id.meal_availability);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
        }
    }

    public Adapter_Meals(Activity activity, int textViewResourceId, ArrayList<Meal> meals, String restaurant_id) {
        this.activity = activity;
        this.meals = meals;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.restaurant_id = restaurant_id;
    }

    @Override
    public MealViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.meal_layout, viewGroup, false);
        MealViewHolder pvh = new MealViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final MealViewHolder MealViewHolder, int i) {
        index = i;

        String url = meals.get(i).getMeal_photo_firebase_URL();
        MealViewHolder.progressBar.setVisibility(View.VISIBLE);
        if (url != null && !url.equals(""))
            Glide
                    .with(activity)
                    .load(url)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            MealViewHolder.progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            MealViewHolder.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .error(R.mipmap.ic_launcher)
                    .into(MealViewHolder.MealImage);

        MealViewHolder.MealName.setText(meals.get(i).getMeal_name());
        MealViewHolder.MealPrice.setText(String.valueOf(meals.get(i).getMeal_price()));
        /*
        if(meals.get(i).getType1()!=null)
            MealViewHolder.Type1.setImageResource(Integer.parseInt(meals.get(i).getType1()));
        if(meals.get(i).getType2()!=null)
            MealViewHolder.Type2.setImageResource(Integer.parseInt(meals.get(i).getType2()));
        */
        /*MealViewHolder.availability.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });*/
        MealViewHolder.availability.setOnCheckedChangeListener(null);
        MealViewHolder.availability.setChecked(meals.get(i).getMealAvailable());
        MealViewHolder.availability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((Switch)v).isChecked();
                String meal_key = meals.get(index).getMeal_id();
                FirebaseDatabase ref = FirebaseDatabase.getInstance();
                DatabaseReference ref2 = ref.getReferenceFromUrl("https://have-break-9713d.firebaseio.com/meals/" + restaurant_id + "/" + meal_key + "/mealAvailable");
                ref2.setValue(checked);
                ((Switch)v).setChecked(checked);
            }
        });
        /*
        MealViewHolder.availability.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String meal_key = meals.get(index).getMeal_id();
                DatabaseReference ref = new DatabaseReference("https://have-break-9713d.firebaseio.com/meals/" + meal_key);
                ref.getReference("_meal_available").setValue(buttonView.isPressed());
                /*
                String meal_key = meals.get(index).getMeal_id();
                DatabaseReference ref = new DatabaseReference("https://have-break-9713d.firebaseio.com/meals/" + meal_key);
                if (buttonView.isPressed() && meals.get(index).getMealAvailable() == true)
                    ref.getReference("_meal_available").setValue(false);
                if (buttonView.isPressed() && meals.get(index).getMealAvailable()==false)
                    ref.getReference("_meal_available").setValue(true);
                if(!buttonView.isPressed() && meals.get(index).getMealAvailable()==true)
                    ref.getReference("_meal_available").setValue(true);
                if(!buttonView.isPressed() && meals.get(index).getMealAvailable()==false)
                    ref.getReference("_meal_available").setValue(false);

            }
        });
        //MealViewHolder.availability.setEnabled();
        */
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public void addItem(Meal m) {
        meals.add(m);
        notifyDataSetChanged();
    }

    public void replaceItem(Meal m){
        meals.set(findMeal(m), m);
        notifyDataSetChanged();
    }

    public void removeItem(Meal m) {
        meals.remove(findMeal(m));
    }

    public int findMeal(Meal m){
        for(int i = 0;i<meals.size();i++){
            Meal meal = meals.get(i);
            if(m.getMeal_id().equals(meal.getMeal_id()))
                return i;
        }
        return -1;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Meal prev = meals.remove(fromPosition);
        meals.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle(activity.getResources().getString(R.string.action_confirm));
        alert.setMessage(activity.getResources().getString(R.string.sure_delete_restaurant));
        alert.setPositiveButton(activity.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Meal m = meals.get(position);
                FirebaseDatabase firebase = FirebaseDatabase.getInstance();
                DatabaseReference mealReference = firebase.getReference("meals/" + m.getRestaurant_id() + "/" + m.getMeal_id());

                removeItem(meals.get(position));
                notifyDataSetChanged();
                mealReference.setValue(null);
                dialog.dismiss();

            }
        });
        alert.setNegativeButton(activity.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


                dialog.dismiss();
            }
        });

        alert.show();

    }
}
