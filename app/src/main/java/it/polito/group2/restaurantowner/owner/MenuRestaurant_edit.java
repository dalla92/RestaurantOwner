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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.MealAddition;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.MealCategory;

/**
 * Created by Alessio on 16/04/2016.
 */
public class MenuRestaurant_edit extends AppCompatActivity implements FragmentMainInfo.onMainInfoPass, FragmentOtherInfo.onOtherInfoPass{
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Meal current_meal;

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
    public void onMainInfoPass(String meal_name, double meal_price, String  photouri,  boolean is_vegan, boolean is_vegetarian, boolean is_celiac, String category, boolean take_away) {
        current_meal.setMeal_name(meal_name);
        current_meal.setMeal_price(meal_price);
        //TODO upload both thumbnail and full size pictures
        //current_meal.setMeal_photo_firebase_URL(photouri);
        current_meal.setIs_meal_celiac(is_celiac);
        current_meal.setIs_meal_vegan(is_vegan);
        current_meal.setIs_meal_vegetarian(is_vegetarian);
        current_meal.setMeal_category(category);
        current_meal.setIs_meal_take_away(take_away);
    }

    @Override
    public void onOtherInfoPass(String meal_description, int cooking_time, ArrayList<MealAddition> mealAdditions, ArrayList<MealCategory> tags) {
        current_meal.setMeal_description(meal_description);
        current_meal.setMeal_cooking_time(cooking_time);
        current_meal.setMeal_additions(mealAdditions);
        current_meal.setMeal_tags(tags);

        String meal_key = current_meal.getMeal_id();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://have-break-9713d.firebaseio.com/meals/" + meal_key);
        ref.setValue(current_meal);
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
}