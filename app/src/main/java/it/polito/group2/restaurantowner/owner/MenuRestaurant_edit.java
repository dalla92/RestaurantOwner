package it.polito.group2.restaurantowner.owner;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;

/**
 * Created by Alessio on 16/04/2016.
 */
public class MenuRestaurant_edit extends AppCompatActivity implements FragmentMainInfo.onMainInfoPass, FragmentOtherInfo.onOtherInfoPass{
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Meal current_meal = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);

        //get current_meal
        Bundle b = getIntent().getExtras();
        if (getIntent().hasExtra("meal"))
            current_meal = (Meal) b.get("meal");
        else {
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    Restaurant_page.class);
            startActivity(intent1);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_restaurant, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            mSectionsPagerAdapter.saveDataFromFragments();
            if(current_meal.getMeal_name().equals(""))
                Toast.makeText(this,"Please insert meal name to continue", Toast.LENGTH_SHORT).show();
            else {
                Intent intent = new Intent();
                intent.putExtra("meal", current_meal);
                setResult(RESULT_OK, intent);
                finish();//finishing activity
                return true;
            }
        }
        if (id == android.R.id.home) {

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMainInfoPass(String meal_name, double meal_price, String  photouri,  String type1, String type2, boolean take_away) {
        current_meal.setMeal_name(meal_name);
        current_meal.setMeal_price(meal_price);
        current_meal.setMeal_photo(photouri);
        current_meal.setType1(type1);
        current_meal.setType2(type2);
        current_meal.setTake_away(take_away);
    }

    @Override
    public void onOtherInfoPass(String meal_description, int cooking_time, ArrayList<Addition> additions, ArrayList<Addition> categories) {
        current_meal.setDescription(meal_description);
        current_meal.setCooking_time(cooking_time);
        current_meal.setMeal_additions(additions);
        current_meal.setMeal_categories(categories);
        try {
            saveJSONMeList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private FragmentMainInfo fm;
        private FragmentOtherInfo fo;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0) {
                fm = FragmentMainInfo.newInstance(current_meal);
                return fm;
            }
            else {
                fo = FragmentOtherInfo.newInstance(current_meal, MenuRestaurant_edit.this); //,AddRestaurantActivity.this
                return fo;
            }
        }

        // Here we can finally safely save a reference to the created
        // Fragment
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            // save the appropriate reference depending on position
            switch (position) {
                case 0:
                    fm = (FragmentMainInfo) createdFragment;
                    break;
                case 1:
                    fo = (FragmentOtherInfo) createdFragment;
                    break;
            }
            return createdFragment;
        }

        public void saveDataFromFragments() {
            // do work on the referenced Fragments, but first check if they even exist yet, otherwise you'll get an NPE.
            if (fm != null) {
                fm.passData();
            }
            if (fo != null) {
                fo.passData();
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.addres_section_main_info);
                case 1:
                    return getString(R.string.addres_section_other_info);
            }
            return null;
        }
    }

    public void saveJSONMeList() throws JSONException {
        Log.d("aaa", "CALLED SAVE_MODIFIED");
        /*
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
        */
        //additions writing
        String FILENAME2 = "mealAddition.json";
        JSONArray jarray2 = new JSONArray();
        for (Addition ad : current_meal.getMeal_additions()) {
            JSONObject jres2 = new JSONObject();
            jres2.put("RestaurantId", ad.getRestaurant_id());
            jres2.put("MealId", ad.getmeal_id());
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
        for (Addition ad : current_meal.getMeal_categories()) {
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
}