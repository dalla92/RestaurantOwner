package it.polito.group2.restaurantowner.user.takeaway;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import it.polito.group2.restaurantowner.R;

public class TakeawayActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MenuCategoryFragment.OnMenuCategorySelectedListener,
        MealFragment.OnMealSelectedListener {

    //private String restaurantID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takeaway);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            //restaurantID = getIntent().getExtras().getString("restaurant_id");
            MenuCategoryFragment firstFragment = new MenuCategoryFragment();
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment).commit();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMenuCategorySelected(String restaurantID, String menuCategoryID) {
        MealFragment mealFragment = new MealFragment();
        Bundle args = new Bundle();
        args.putString(MealFragment.MENUCATEGORY_ID, menuCategoryID);
        args.putString(MealFragment.RESTAURANT_ID, restaurantID);
        mealFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mealFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onMealSelected(String restaurantID, String menuCategoryID, String mealID) {
        AdditionFragment additionFragment = new AdditionFragment();
        Bundle args = new Bundle();
        args.putString(AdditionFragment.MENUCATEGORY_ID, menuCategoryID);
        args.putString(AdditionFragment.RESTAURANT_ID, restaurantID);
        args.putString(AdditionFragment.MEAL_ID, mealID);
        additionFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, additionFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
