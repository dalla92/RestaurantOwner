package it.polito.group2.restaurantowner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MenuRestaurant_page extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Menu menu;
    private ListView listView;
    private Adapter_Meals adapter;
    private String restaurant_id = "0";
    private ArrayList<Meal> meals = new ArrayList<>();
    private ArrayList<Addition> meals_additions = new ArrayList<>();
    private ArrayList<Addition> meals_categories = new ArrayList<>();
    public RecyclerView rv;
    public int index_position;
    Meal meal_to_delete;
    Meal current_meal = null;
    public final int MODIFY_MEAL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_restaurant_page);

        //get the right restaurant
        Bundle b = getIntent().getExtras();
        if(b!=null)
            restaurant_id = b.getString("restaurant_id");

        //check if file already exists and is not empty, otherwise create it
        /*
        FileInputStream fis = null;
        String FILENAME = "mealList.json";
        try {
            fis = openFileInput(FILENAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File("mealList.json");
        if(!file.exists()) {
        try {
                saveJSONMeList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        */
        Log.d("aaa", "SIZE1: " + meals.size());
        String FILENAME = "mealList.json";
        File file = getFileStreamPath(FILENAME);
        if(!file.exists()){
            addMeal(restaurant_id);
            addAddition(restaurant_id);
            addCategory(restaurant_id);
            try {
                saveJSONMeList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d("aaa", "SIZE2: " + meals.size());
        try {
            readJSONMeList();
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        Log.d("aaa", "SIZE3: " + meals.size());

        //remove duplicates
        //remove_duplicates();

        //navigation drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //take the right restaurant
        Bundle b1 = getIntent().getExtras();
        restaurant_id = b1.getString("restaurant_id");

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);
        Log.d("ccc", "SIZE "+ meals.size());
        adapter = new Adapter_Meals(this, 0, meals, restaurant_id);
        listView.setAdapter(adapter);
    }

    /*
    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        try {
            saveJSONMeList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    */

    /*
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        try {
            readJSONMeList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    */

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_restaurant_page, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_logout) {
            // TODO Handle the logout action
        } else if (id == R.id.nav_manage) {
            // TODO Handle the manage action
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void myClickHandler_add(View v) {
        Log.d("myClickHandler", "You want to add a new meal");
        //adapter.insert(new Meal(), 0);
        Meal m = new Meal();
        m.setRestaurantId(restaurant_id);
        meals.add(0, m);  //insert at the top
        try {
            saveJSONMeList_modified();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
        adapter.notifyDataSetInvalidated();
    }

    public void myClickHandler_edit(View v) {
        LinearLayout vwParentRow = (LinearLayout) v.getParent();
        TextView child = (TextView) vwParentRow.getChildAt(2);
        final String meal_name = child.getText().toString();
        Intent intent1 = new Intent(
                getApplicationContext(),
                MenuRestaurant_edit.class);
        Meal meal_to_edit = null;
        index_position = -1;
        for(Meal m : meals){
            index_position++;
            if(m.getMeal_name().equals(meal_name)){
                meal_to_edit = m;
                break;
            }
        }
        if(meal_to_edit!=null){
        intent1.putExtra("meal", meal_to_edit);
        startActivityForResult(intent1, MODIFY_MEAL);
        }
    }

    public void myClickHandler_remove(View v) {
        LinearLayout vwParentRow = (LinearLayout) v.getParent();
        TextView child = (TextView) vwParentRow.getChildAt(2);
        final String meal_name = child.getText().toString();
        Log.d("myClickHandler", "You want to remove " + meal_name);
        //TextView meal_name_text_view = (TextView) vwParentRow.findViewById(R.id.meal_name);
        //String meal_name = meal_name_text_view.getText().toString();
        meal_to_delete = null;
        Log.d("ccc", meal_name);
        if(meal_name.trim().equals("")) {
            meals.remove(0);
            adapter.notifyDataSetChanged();
            adapter.notifyDataSetInvalidated();
            try {
                saveJSONMeList_modified();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }
        for(Meal m : meals){
            Log.d("ccc", "Loop: " + m.getMeal_name());
            if(m.getMeal_name().equals(meal_name)){
                Log.d("ccc", "FOUND");
                meal_to_delete = m;
                break;
            }
        }
        Log.d("ccc", meal_to_delete.getMeal_name());
        //dialog box
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        meals.remove(meal_to_delete);
                        //adapter.remove(meal_to_delete);
                        try {
                            saveJSONMeList_modified();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                        adapter.notifyDataSetInvalidated();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setMessage("Are you sure that you want to delete " + meal_name + "?").setPositiveButton("Yes, sure", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MODIFY_MEAL) {
            if (resultCode == RESULT_OK) {
                Meal m = (Meal) data.getExtras().get("meal");
                meals.set(index_position, m);
                adapter.notifyDataSetChanged();
                adapter.notifyDataSetInvalidated();
                try {
                    saveJSONMeList_modified();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void remove_duplicates(){
        for( Meal m : meals) {
            meals.add(m);
        }
        for( Addition a : meals_additions) {
            meals_additions.add(a);
        }
        for( Addition a : meals_categories) {
            meals_categories.add(a);
        }
     }

    public void readJSONMeList() throws JSONException {
        Log.d("aaa", "CALLED READ");
        //mealList.json
        meals = new ArrayList<>();
        meals_additions = new ArrayList<>();
        meals_categories = new ArrayList<>();
        String json = null;
        FileInputStream fis = null;
        String FILENAME = "mealList.json";
        try {
            fis = openFileInput(FILENAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            json = new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jobj = new JSONObject(json);
        JSONArray jsonArray = jobj.optJSONArray("Meals");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Meal me = new Meal();
            me.setRestaurantId(jsonObject.optString("RestaurantId"));
            me.setMealId(jsonObject.optString("MealId"));
            me.setMeal_photo(jsonObject.optString("MealPhoto"));
            me.setMeal_name(jsonObject.optString("MealName"));
            me.setMeal_price(jsonObject.optDouble("MealPrice"));
            me.setType1(jsonObject.optString("MealType1"));
            me.setType2(jsonObject.optString("MealType2"));
            me.setAvailable(jsonObject.getBoolean("MealAvailable"));
            me.setTake_away(jsonObject.getBoolean("MealTakeAway"));
            me.setCooking_time(jsonObject.optInt("MealCookingTime"));
            me.setDescription(jsonObject.getString("MealDescription"));
                //if(me.getRestaurantId().equals(restaurant_id))
                    meals.add(me);
        }
        //mealAdditions.json
        String json2 = null;
        FileInputStream fis2 = null;
        String FILENAME2 = "mealAddition.json";
        try {
            fis2 = openFileInput(FILENAME2);
            int size2 = fis2.available();
            byte[] buffer2 = new byte[size2];
            fis2.read(buffer2);
            fis2.close();
            json2 = new String(buffer2, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jobj2 = new JSONObject(json2);
        JSONArray jsonArray2 = jobj2.optJSONArray("MealsAdditions");
        for (int i = 0; i < jsonArray2.length(); i++) {
            JSONObject jsonObject2 = jsonArray2.getJSONObject(i);
            Addition ad = new Addition();
            if (jsonObject2.optString("RestaurantId").equals(restaurant_id)) {
                ad.setRestaurant_id(jsonObject2.optString("RestaurantId"));
                ad.setMeal_id(jsonObject2.optString("MealId"));
                ad.setName(jsonObject2.optString("AdditionName"));
                ad.setSelected(jsonObject2.getBoolean("AdditionSelected"));
                ad.setPrice(jsonObject2.optDouble("AdditionPrice"));
                meals_additions.add(ad);
            }
        }
        //mealCategories.json
        String json3 = null;
        FileInputStream fis3 = null;
        String FILENAME3 = "mealCategory.json";
        try {
            fis3 = openFileInput(FILENAME3);
            int size3 = fis3.available();
            byte[] buffer3 = new byte[size3];
            fis3.read(buffer3);
            fis3.close();
            json3 = new String(buffer3, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jobj3 = new JSONObject(json3);
        JSONArray jsonArray3 = jobj3.optJSONArray("MealsCategories");
        for (int i = 0; i < jsonArray3.length(); i++) {
            JSONObject jsonObject3 = jsonArray3.getJSONObject(i);
            Addition ad = new Addition();
            if (jsonObject3.optString("RestaurantId").equals(restaurant_id)) {
                ad.setRestaurant_id(jsonObject3.optString("RestaurantId"));
                ad.setMeal_id(jsonObject3.optString("MealId"));
                ad.setName(jsonObject3.optString("CategoryName"));
                ad.setSelected(jsonObject3.getBoolean("CategorySelected"));
                ad.setPrice(0);
                //ad.setPrice(jsonObject3.optDouble("CategoryPrice"));
                meals_categories.add(ad);
            }
        }
    }

    public void saveJSONMeList() throws JSONException {
        //meals writing
        Log.d("aaa", "CALLED SAVE_BASE");
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
            Log.d("ccc", "WRITE");
            jarray.put(jres);
        }
        JSONObject resObj = new JSONObject();
        resObj.put("Meals", jarray);
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
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
        for (Addition ad : meals_additions) {
            JSONObject jres2 = new JSONObject();
            jres2.put("RestaurantId", ad.getRestaurant_id());
            jres2.put("MealId", ad.getMeal_id());
            jres2.put("AdditionName", ad.getName());
            jres2.put("AdditionSelected", ad.isSelected());
            jres2.put("AdditionPrice", ad.getPrice());
            jarray2.put(jres2);
        }
        JSONObject resObj2 = new JSONObject();
        resObj2.put("MealsAdditions", jarray2);
        FileOutputStream fos2 = null;
        try {
            fos2 = openFileOutput(FILENAME2, Context.MODE_PRIVATE);
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
        for (Addition ad : meals_categories) {
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
            resObj3.put("MealsCategories", jarray3);
            FileOutputStream fos3 = null;
            try {
                fos3 = openFileOutput(FILENAME3, Context.MODE_PRIVATE);
                fos3.write(resObj3.toString().getBytes());
                fos3.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    public void saveJSONMeList_modified() throws JSONException {
        Log.d("aaa", "CALLED SAVE_MODIFIED");
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
            Log.d("ccc", "WRITE");
            jarray.put(jres);
        }
        JSONObject resObj = new JSONObject();
        resObj.put("Meals", jarray);
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
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
        for (Addition ad : meals_additions) {
            JSONObject jres2 = new JSONObject();
            jres2.put("RestaurantId", ad.getRestaurant_id());
            jres2.put("MealId", ad.getMeal_id());
            jres2.put("AdditionName", ad.getName());
            jres2.put("AdditionSelected", ad.isSelected());
            jres2.put("AdditionPrice", ad.getPrice());
            jarray2.put(jres2);
        }
        JSONObject resObj2 = new JSONObject();
        resObj2.put("MealsAdditions", jarray2);
        FileOutputStream fos2 = null;
        try {
            fos2 = openFileOutput(FILENAME2, Context.MODE_PRIVATE);
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
        for (Addition ad : meals_categories) {
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
        resObj3.put("MealsCategories", jarray3);
        FileOutputStream fos3 = null;
        try {
            fos3 = openFileOutput(FILENAME3, Context.MODE_PRIVATE);
            fos3.write(resObj3.toString().getBytes());
            fos3.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addMeal(String restaurant_id){
        Log.d("ccc", "CALLED ADDMEAL");
        Meal m = new Meal();
        m.setMeal_name("Pasta ca sassa");
        m.setMeal_price(5.0);
        m.setAvailable(true);
        m.setCooking_time(10);
        m.setMealId("0");
        m.setRestaurantId(restaurant_id);
        m.setDescription("Pasta salta con salsa di pomodoro");
        m.setType1("Vegetarian");
        m.setTake_away(false);
        m.setMeal_photo(getResources().getResourceName(R.mipmap.ic_launcher));
        meals.add(m);
    }

    public void addAddition(String restaurant_id){
        Addition a1 = new Addition();
        a1.setName("Basilicò");
        a1.setPrice(0.50);
        a1.setMeal_id("0");
        a1.setSelected(true);
        a1.setRestaurant_id(restaurant_id);

        Addition a2 = new Addition();
        a2.setName("Peperoncino");
        a2.setPrice(0.20);
        a2.setMeal_id("0");
        a2.setSelected(true);
        a2.setRestaurant_id(restaurant_id);

        meals_additions.add(a1);
        meals_additions.add(a2);

        Log.d("ccc", "ADDITIONS:");
        for(Addition a : meals_additions){
            Log.d("ccc", a.getName() + " " + a.getMeal_id());
        }
    }

    public void addCategory(String restaurant_id){
        Addition a1 = new Addition();
        a1.setName("Pasta");
        a1.setMeal_id("0");
        a1.setSelected(true);
        a1.setRestaurant_id(restaurant_id);

        Addition a2 = new Addition();
        a2.setName("Piccante");
        a2.setMeal_id("0");
        a2.setSelected(true);
        a2.setRestaurant_id(restaurant_id);

        meals_categories.add(a1);
        meals_categories.add(a2);

        Log.d("ccc", "CATEGORIES:");
        for(Addition a : meals_categories){
            Log.d("ccc", a.getName() + " " + a.getMeal_id());
        }
    }
}