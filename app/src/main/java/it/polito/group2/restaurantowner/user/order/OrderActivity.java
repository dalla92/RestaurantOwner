package it.polito.group2.restaurantowner.user.order;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.Meal;
import it.polito.group2.restaurantowner.data.MealAddition;
import it.polito.group2.restaurantowner.data.MenuCategory;
import it.polito.group2.restaurantowner.data.Order;
import it.polito.group2.restaurantowner.data.OrderMeal;
import it.polito.group2.restaurantowner.data.OrderMealAddition;

public class OrderActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CategoryFragment.OnCategorySelectedListener,
        MealFragment.OnMealSelectedListener,
        AdditionFragment.OnNextClickedListener,
        InfoFragment.OnAddClickedListener {

    private Order order;
    private OrderMeal meal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        //Toolbar setting
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Navigation drawer setting
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Fragment loader
        if(findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            //TODO: verificare che il parametro viene passato con questo nome
            String restaurantID = getIntent().getExtras().getString("restaurant_id");
            String userID = getIntent().getExtras().getString("user_id");
            order = new Order(restaurantID, userID);
            CategoryFragment categoryFragment = CategoryFragment.
                    newInstance(restaurantID);
            getSupportFragmentManager().beginTransaction().
                    add(R.id.fragment_container, categoryFragment).commit();
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
    public void onCategorySelected(MenuCategory category) {
        this.meal = new OrderMeal();
        this.meal.setCategory(category);
        MealFragment mealFragment = MealFragment.newInstance(category.getCategoryID());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mealFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onMealSelected(Meal meal) {
        this.meal.setMeal(meal);
        AdditionFragment additionFragment = AdditionFragment.newInstance(meal.getMealId());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, additionFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onNextClicked(ArrayList<MealAddition> additionList) {
        for(MealAddition ma : additionList) {
            OrderMealAddition addition = new OrderMealAddition();
            addition.setAddition(ma);
            this.meal.getAdditionList().add(addition);
        }

        InfoFragment infoFragment = new InfoFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, infoFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onAddClicked() {
        //Toast.makeText(OrderActivity.this, "Ciao", Toast.LENGTH_SHORT).show();
    }
}
