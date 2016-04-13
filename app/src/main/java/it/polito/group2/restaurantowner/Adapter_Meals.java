package it.polito.group2.restaurantowner;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alessio on 12/04/2016.
 */
public class Adapter_Meals extends RecyclerView.Adapter<Adapter_Meals.MealViewHolder> {
        ArrayList<Meal> meals;
        Context context;
        public String meal_name;
        public int index;
        Adapter_Meals(ArrayList<Meal> meals, Context context) {
        this.meals = meals;
        this.context = context;
        }
public static class MealViewHolder extends RecyclerView.ViewHolder {
    CardView cv;
    ImageView MealImage;
    TextView MealName;
    TextView MealPrice;
    ImageView Type1;
    ImageView Type2;
    Switch availability;

    MealViewHolder(View itemView) {
        super(itemView);
        cv = (CardView) itemView.findViewById(R.id.cv_meal);
        MealImage = (ImageView) itemView.findViewById(R.id.meal_image);
        MealName = (TextView) itemView.findViewById(R.id.meal_name);
        MealPrice = (TextView) itemView.findViewById(R.id.meal_price);
        Type1 = (ImageView) itemView.findViewById(R.id.meal_type1);
        Type2 = (ImageView) itemView.findViewById(R.id.meal_type2);
        availability = (Switch) itemView.findViewById(R.id.meal_availability);
    }
}

    @Override
    public int getItemCount() {
        return meals.size();
    }

    @Override
    public MealViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.meal_card_view, viewGroup, false);
        MealViewHolder pvh = new MealViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(MealViewHolder MealViewHolder, int i) {
        MealViewHolder.MealImage.setImageURI(Uri.parse(meals.get(i).getMeal_photo()));
        MealViewHolder.MealName.setText(meals.get(i).getMeal_name());
        MealViewHolder.MealPrice.setText(String.valueOf(meals.get(i).getMeal_price()));
        MealViewHolder.Type1.setImageURI(Uri.parse(meals.get(i).getType1()));
        MealViewHolder.Type2.setImageURI(Uri.parse(meals.get(i).getType2()));
        MealViewHolder.availability.setEnabled(meals.get(i).isAvailable());
        meal_name = MealViewHolder.MealName.getText().toString();
        MealViewHolder.availability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 0;
                int i = 0;
                for (; i < meals.size(); i++) {
                    if (meals.get(i).getMeal_name().equals(meal_name)) {
                        meals.get(i).setAvailable(v.findViewById(R.id.meal_availability).isEnabled());
                        break;
                    }
                }
                try {
                    saveJSONMeList(meals);
                }
                catch(JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
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
            fos = this.context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
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
                jres2.put("MealId", ad.getMeal_id());
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
            fos2 = this.context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
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
                jres3.put("MealId", ad.getMeal_id());
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
                fos3 = this.context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
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