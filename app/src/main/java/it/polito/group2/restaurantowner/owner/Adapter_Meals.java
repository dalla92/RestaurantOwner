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
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.Addition;
import it.polito.group2.restaurantowner.data.Meal;

/**
 * Created by Alessio on 12/04/2016.
 */
public class Adapter_Meals extends RecyclerView.Adapter<Adapter_Meals.MealViewHolder> implements ItemTouchHelperAdapter{
    private Activity activity;
    private static ArrayList<Meal> meals;
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

        MealViewHolder(View itemView) {
            super(itemView);
            MealImage = (ImageView) itemView.findViewById(R.id.meal_image);
            MealName = (TextView) itemView.findViewById(R.id.meal_name);
            MealPrice = (TextView) itemView.findViewById(R.id.meal_price);
            Type1 = (ImageView) itemView.findViewById(R.id.meal_type1);
            Type2 = (ImageView) itemView.findViewById(R.id.meal_type2);
            availability = (Switch) itemView.findViewById(R.id.meal_availability);
        }
    }

    public Adapter_Meals (Activity activity, int textViewResourceId, ArrayList<Meal> meals, String restaurant_id) {
        try {
            this.activity = activity;
            this.meals = meals;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.restaurant_id = restaurant_id;
        } catch (Exception e) {
        }
        Log.d("ccc", "CALLED WITH SUCCESS1");
    }

    @Override
    public MealViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.meal_layout, viewGroup, false);
        MealViewHolder pvh = new MealViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(MealViewHolder MealViewHolder, int i) {
        if(meals.get(i).getMeal_photo()!=null && !meals.get(i).getMeal_photo().equals(""))
            MealViewHolder.MealImage.setImageURI(Uri.parse(meals.get(i).getMeal_photo()));
        MealViewHolder.MealName.setText(meals.get(i).getMeal_name());
        MealViewHolder.MealPrice.setText(String.valueOf(meals.get(i).getMeal_price()));
        /*
        if(meals.get(i).getType1()!=null)
            MealViewHolder.Type1.setImageResource(Integer.parseInt(meals.get(i).getType1()));
        if(meals.get(i).getType2()!=null)
            MealViewHolder.Type2.setImageResource(Integer.parseInt(meals.get(i).getType2()));
        */
        MealViewHolder.availability.setChecked(meals.get(i).isAvailable());
        //MealViewHolder.availability.setEnabled();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }
    public void addItem(int position, Meal m){
        meals.add(position,m);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, meals.size());
    }
    public void removeItem(int position){
        meals.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,meals.size());
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
        alert.setTitle("Confirmation!");
        alert.setMessage("Are you sure you want to delete the restaurant?\nThe operation cannot be undone!");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeItem(position);
                dialog.dismiss();

            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        alert.show();

    }

    public void saveJSONMeList(ArrayList<Meal> meals) throws JSONException {
        //meals writing
        String FILENAME = "mealList.json";
        JSONArray jarray = new JSONArray();
        for (Meal me : meals) {
            JSONObject jres = new JSONObject();
            jres.put("RestaurantId", me.getRestaurantId());
            jres.put("MealId", me.getMealId());
            jres.put("MealPhoto", me.getMeal_photo());
            jres.put("MealName", me.getMeal_name());
            jres.put("MealPrice", me.getMeal_price());
            jres.put("MealType1", me.getType1());
            jres.put("MealType2", me.getType2());
            jres.put("Category", me.getCategory());
            jres.put("MealAvailable", me.isAvailable());
            jres.put("MealTakeAway", me.isTake_away());
            jres.put("MealCookingTime", me.getCooking_time());
            jres.put("MealDescription", me.getDescription());
            jarray.put(jres);
        }
        JSONObject resObj = new JSONObject();
        resObj.put("Meals", jarray);
        FileOutputStream fos = null;
        try {
            fos = activity.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(resObj.toString().getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //additions writing
        String FILENAME2 = "mealAddition.json";
        JSONArray jarray2 = new JSONArray();
        for (Meal me : meals) {
            for (Addition ad : me.getMeal_additions()) {
                JSONObject jres2 = new JSONObject();
                jres2.put("RestaurantId", ad.getRestaurant_id());
                jres2.put("MealId", ad.getmeal_id());
                jres2.put("AdditionName", ad.getName());
                jres2.put("AdditionSelected", ad.isSelected());
                jres2.put("AdditionPrice", ad.getPrice());
                jarray2.put(jres2);
            }
        }
        JSONObject resObj2 = new JSONObject();
        resObj2.put("MealsAdditions", jarray2);
        FileOutputStream fos2 = null;
        try {
            fos2 = activity.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos2.write(resObj2.toString().getBytes());
            fos2.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //categories writing
        String FILENAME3 = "mealCategory.json";
        JSONArray jarray3 = new JSONArray();
        for (Meal me : meals) {
            for (Addition ad : me.getMeal_categories()) {
                JSONObject jres3 = new JSONObject();
                jres3.put("RestaurantId", ad.getRestaurant_id());
                jres3.put("MealId", ad.getmeal_id());
                jres3.put("CategoryName", ad.getName());
                jres3.put("CategorySelected", ad.isSelected());
                jres3.put("CategoryPrice", 0);
                //jres3.put("Price", ad.getPrice());
                jarray3.put(jres3);
            }
            JSONObject resObj3 = new JSONObject();
            resObj.put("MealsCategories", jarray);
            FileOutputStream fos3 = null;
            try {
                fos3 = activity.openFileOutput(FILENAME, Context.MODE_PRIVATE);
                fos3.write(resObj3.toString().getBytes());
                fos3.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
