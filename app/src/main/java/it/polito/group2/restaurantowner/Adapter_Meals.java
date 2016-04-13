package it.polito.group2.restaurantowner;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

/**
 * Created by Alessio on 12/04/2016.
 */
public class Adapter_Meals extends ArrayAdapter<Meal> {
    private Activity activity;
    private ArrayList<Meal> meals;
    private static LayoutInflater inflater = null;
    public String meal_name;
    public int index;

    public Adapter_Meals (Activity activity, int textViewResourceId, ArrayList<Meal> meals) {
        super(activity, textViewResourceId, meals);
        try {
            this.activity = activity;
            this.meals = meals;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch (Exception e) {
        }
    }

    public int getCount() {
        return meals.size();
    }

    public Meal getItem(Meal position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public ImageView MealImage;
        public TextView MealName;
        public TextView MealPrice;
        public ImageView Type1;
        public ImageView Type2;
        public Switch availability;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        int i = position;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.meal_layout, null);
                holder = new ViewHolder();
                holder.MealImage = (ImageView) vi.findViewById(R.id.meal_image);
                holder.MealName = (TextView) vi.findViewById(R.id.meal_name);
                holder.MealPrice = (TextView) vi.findViewById(R.id.meal_price);
                holder.Type1 = (ImageView) vi.findViewById(R.id.meal_type1);
                holder.Type2 = (ImageView) vi.findViewById(R.id.meal_type2);
                holder.availability = (Switch) vi.findViewById(R.id.meal_availability);
                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }
            /*
            if(meals.get(i).getMeal_photo()!=null)
                MealViewHolder.MealImage.setImageURI(Uri.parse(meals.get(i).getMeal_photo()));
            */
            holder.MealName.setText(meals.get(i).getMeal_name());
            holder.MealPrice.setText(String.valueOf(meals.get(i).getMeal_price()));
            /*
            if(meals.get(i).getType1()!=null)
                MealViewHolder.Type1.setImageResource(Integer.parseInt(meals.get(i).getType1()));
            if(meals.get(i).getType2()!=null)
                MealViewHolder.Type2.setImageResource(Integer.parseInt(meals.get(i).getType2()));
            */
            holder.availability.setEnabled(meals.get(i).isAvailable());
            meal_name = holder.MealName.getText().toString();
            holder.availability.setOnClickListener(new View.OnClickListener() {
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        } catch (Exception e) {
        }
        return vi;
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
