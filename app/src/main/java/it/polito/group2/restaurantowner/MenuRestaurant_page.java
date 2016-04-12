package it.polito.group2.restaurantowner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MenuRestaurant_page extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Menu menu;
    private ListView listView;
    private Adapter_Meals adapter;
    private int restaurant_id = 0;
    private ArrayList<Meal> meals;
    private ArrayList<Addition> meals_additions;
    private ArrayList<Addition> meals_categories;
    public RecyclerView rv;
    public int index_position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_restaurant_page);

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
        Bundle b = getIntent().getExtras();
        restaurant_id = b.getInt("restaurant_id");
        //retrieve data
        try {
            meals = readJSONMeList();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //meals card view implementation
        rv = (RecyclerView) findViewById(R.id.rv_meals);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        Adapter_Meals adapter = new Adapter_Meals(meals, this.getApplicationContext());
        rv.setAdapter(adapter);
        //or
        /*
        String[] values = new String[]{"Pasta ca sassa",
                "Tuma",
                "Vino",
                "Maccu",
                "Sasizza",
                "Canni cavaddu",
                "Stummu",
                "Pasta con la carbonara",
                "Pisci stoccu e baccalaru di to soru",
                "Nenti c'è"
        };
        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);
        // Defined Array values to show in ListView
        ArrayList<String> lst = new ArrayList<String>(Arrays.asList(values)); //or values
        // Define a new Adapter: Context, Layout for the row, ID of the TextView to which the data is written, Array of data
        adapter = new ArrayAdapter<String>(this,
                R.layout.meal_card_view, R.id.meal_name, lst);
        */
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        try {
            saveJSONMeList(meals);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        try {
            readJSONMeList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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
        } else if (id == R.id.one_restaurant) {
            Intent intent = new Intent(
                    getApplicationContext(),
                    Restaurant_page.class);
            Bundle b = new Bundle();
            b.putInt("restaurant_id", restaurant_id);
            intent.putExtras(b);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void myClickHandler_add(View v) {
        Log.d("myClickHandler", "You want to add a new meal");
        //adapter.insert("New empty meal", 0);
        meals.add(0, new Meal());  //insert at the top
        try {
            saveJSONMeList(meals);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
    }

    public void myClickHandler_edit(View v) {
        LinearLayout vwParentRow = (LinearLayout) v.getParent();
        TextView child = (TextView) vwParentRow.getChildAt(2);
        String meal_name = child.getText().toString();
        Log.d("myClickHandler", "You want to modify " + meal_name);
        int id = 0;
        int i = 0;
        for (; i < meals.size(); i++) {
            if (meals.get(i).getMeal_name().equals(meal_name)) {
                id = i;
                break;
            }
        }
        Intent intent = new Intent(
                getApplicationContext(),
                MenuRestaurant_edit.class);
        Bundle b = new Bundle();
        b.putInt("meal_id", id);
        b.putInt("restaurant_id", restaurant_id);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void myClickHandler_remove(View v) {
        LinearLayout vwParentRow = (LinearLayout) v.getParent();
        TextView child = (TextView) vwParentRow.getChildAt(2);
        final String meal_name = child.getText().toString();
        Log.d("myClickHandler", "You want to remove " + meal_name);
        int i = 0;
        for (; i < meals.size(); i++) {
            if (meals.get(i).getMeal_name().equals(meal_name))
                break;
        }
        index_position = i;
        //dialog box
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //adapter.remove(meal_name);
                        meals.remove(index_position);
                        try {
                            saveJSONMeList(meals);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
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

    public ArrayList<Meal> readJSONMeList() throws JSONException {
        //mealList.json
        String json = null;
        ArrayList<Meal> meals = new ArrayList<>();
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
            return meals;
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jobj = new JSONObject(json);
        JSONArray jsonArray = jobj.optJSONArray("Meals");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Meal me = new Meal();
            if (jsonObject.optInt("RestaurantId") == restaurant_id) {
                me.setRestaurantId(jsonObject.optInt("RestaurantId"));
                me.setMealId(jsonObject.optInt("MealId"));
                me.setMeal_photo(jsonObject.optString("MealPhoto"));
                me.setMeal_name(jsonObject.optString("MealName"));
                me.setMeal_price(jsonObject.optDouble("MealPrice"));
                me.setType1(jsonObject.optString("MealType1"));
                me.setType2(jsonObject.optString("MealType2"));
                me.setAvailable(jsonObject.getBoolean("MealAvailable"));
                me.setTake_away(jsonObject.getBoolean("MealTakeAway"));
                me.setCooking_time(jsonObject.optInt("MealCookingTime"));
                me.setDescription(jsonObject.getString("MealDescription"));
                meals.add(me);
            }
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
            return meals;
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jobj2 = new JSONObject(json2);
        JSONArray jsonArray2 = jobj2.optJSONArray("MealsAdditions");
        for (int i = 0; i < jsonArray2.length(); i++) {
            JSONObject jsonObject2 = jsonArray2.getJSONObject(i);
            Addition ad = new Addition();
            if (jsonObject2.optInt("RestaurantId") == restaurant_id) {
                ad.setRestaurant_id(jsonObject2.optInt("RestaurantId"));
                ad.setMeal_id(jsonObject2.optInt("MealId"));
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
            return meals;
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jobj3 = new JSONObject(json3);
        JSONArray jsonArray3 = jobj3.optJSONArray("MealsCategories");
        for (int i = 0; i < jsonArray3.length(); i++) {
            JSONObject jsonObject3 = jsonArray3.getJSONObject(i);
            Addition ad = new Addition();
            if (jsonObject3.optInt("RestaurantId") == restaurant_id) {
                ad.setRestaurant_id(jsonObject3.optInt("RestaurantId"));
                ad.setMeal_id(jsonObject3.optInt("MealId"));
                ad.setName(jsonObject3.optString("CategoryName"));
                ad.setSelected(jsonObject3.getBoolean("CategorySelected"));
                ad.setPrice(0);
                //ad.setPrice(jsonObject3.optDouble("CategoryPrice"));
                meals_categories.add(ad);
            }
        }
        //fill additions and categories of my restaurant
        meals.get(restaurant_id).setMeal_additions(meals_additions); //no further checks because I read if(jsonObject3.optInt("RestaurantId")==restaurant_id) {
        meals.get(restaurant_id).setMeal_categories(meals_categories);
        return meals;
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
            fos2 = openFileOutput(FILENAME, Context.MODE_PRIVATE);
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
                fos3 = openFileOutput(FILENAME, Context.MODE_PRIVATE);
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