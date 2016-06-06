package it.polito.group2.restaurantowner.user.order;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;

import it.polito.group2.restaurantowner.HaveBreak;
import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.MealAddition;
import it.polito.group2.restaurantowner.firebasedata.Order;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
import it.polito.group2.restaurantowner.firebasedata.User;
import it.polito.group2.restaurantowner.owner.MainActivity;
import it.polito.group2.restaurantowner.user.my_orders.MyOrdersActivity;
import it.polito.group2.restaurantowner.user.my_reviews.MyReviewsActivity;
import it.polito.group2.restaurantowner.user.restaurant_page.UserMyFavourites;
import it.polito.group2.restaurantowner.user.restaurant_page.UserMyReservations;
import it.polito.group2.restaurantowner.user.restaurant_page.UserProfile;
import it.polito.group2.restaurantowner.user.restaurant_page.UserRestaurantActivity;
import it.polito.group2.restaurantowner.user.restaurant_list.UserRestaurantList;

public class OrderActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CategoryFragment.OnActionListener,
        MealFragment.OnActionListener,
        AdditionFragment.OnActionListener,
        QuantityFragment.OnActionListener,
        CartFragment.OnActionListener {

    private Order order;
    private Meal meal;

    private String userID;
    private String restaurantID;

    private Restaurant restaurant;
    private User user;
    private ProgressDialog mProgressDialog;
    private ArrayList<Meal> mealList;
    private ArrayList<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_order_activity);

        userID = FirebaseUtil.getCurrentUserId();
        if(getIntent().getExtras()!=null && getIntent().getExtras().getString("restaurant_id")!=null) {
            restaurantID = getIntent().getExtras().getString("restaurant_id");
        }

        if(userID == null || restaurantID == null) {
            Log.d("FILIPPO", "utente non loggato o restaurantID non ricevuto");
            Intent intent = new Intent(this, HaveBreak.class);
            finish();
            startActivity(intent);
        }

        showProgressDialog();
        user = FirebaseUtil.getCurrentUser();
        restaurant = FirebaseUtil.getRestaurant(restaurantID);
        mealList = FirebaseUtil.getMealsByRestaurant(restaurantID);
        orderList = FirebaseUtil.getOrdersByRestaurant(restaurantID);
        hideProgressDialog();

        if(!restaurant.getTakeAwayAllowed()) {
            Log.d("FILIPPO", "l'tente non sarebbe dovuto arrivare a questa pagina");
            Intent intent = new Intent(this, HaveBreak.class);
            finish();
            startActivity(intent);
        }

        if(restaurantOrderLimit()) {
            //TODO lanciare un alert perché il massimo numero di prenotazioni in un'ora è stato raggiunto
        }

        order = setNewOrder();

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

        //Navigation drawer user info
        TextView nav_username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderUsername);
        TextView nav_email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderEmail);
        ImageView nav_photo = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView);
        if(user != null) {
            if (user.getUser_full_name() != null)
                nav_username.setText(user.getUser_full_name());
            if (user.getUser_email() != null)
                nav_email.setText(user.getUser_email());
        }
        SharedPreferences userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
        Uri photouri = null;
        if(userDetails.getString("photouri", null) != null) {
            photouri = Uri.parse(userDetails.getString("photouri", null));
            File f = new File(getRealPathFromURI(photouri));
            Drawable d = Drawable.createFromPath(f.getAbsolutePath());
            navigationView.getHeaderView(0).setBackground(d);
        } else {
            nav_photo.setImageResource(R.drawable.blank_profile);
        }

        //Fragment loader
        if(findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            CategoryFragment categoryFragment = CategoryFragment.newInstance(getCategoryList());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, categoryFragment, "CATEGORY");
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(getSupportFragmentManager().findFragmentByTag("CART") != null){
                CategoryFragment categoryFragment = CategoryFragment.newInstance(getCategoryList());
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, categoryFragment, "CATEGORY");
                transaction.addToBackStack(null);
                transaction.commit();
            } else if(getSupportFragmentManager().findFragmentByTag("CATEGORY") != null) {
                //TODO controllare quando vieni da un'altra activity
                //TODO controllare quando vieni dal carrello
                //TODO controllare quando vieni dal mealList
                Intent intent = new Intent(this, UserRestaurantActivity.class);
                intent.putExtra("restaurant_id", restaurantID);
                startActivity(intent);
            } else {
                super.onBackPressed();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(id==R.id.nav_owner){
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    MainActivity.class);
            startActivity(intent1);
            return true;
        } else if(id==R.id.nav_home){
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    UserRestaurantList.class);
            startActivity(intent1);
            return true;
        } else if(id==R.id.nav_login){
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    UserRestaurantList.class);
            startActivity(intent1);
            return true;
        } else if(id==R.id.nav_my_profile) {
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    UserProfile.class);
            startActivity(intent1);
            return true;
        } else if(id==R.id.nav_my_orders) {
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    UserRestaurantList.class);
            startActivity(intent1);
            return true;
        } else if(id==R.id.nav_my_reservations){
            Intent intent3 = new Intent(
                    getApplicationContext(),
                    UserMyReservations.class);
            startActivity(intent3);
            return true;
        } else if(id==R.id.nav_my_reviews){
            Intent intent3 = new Intent(
                    getApplicationContext(),
                    MyReviewsActivity.class);
            startActivity(intent3);
            return true;
        } else if(id==R.id.nav_my_favourites){
            Intent intent3 = new Intent(
                    getApplicationContext(),
                    UserMyFavourites.class);
            startActivity(intent3);
            return true;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onCategorySelected(String categoryName) {
        this.meal = new Meal();
        this.meal.setRestaurant_id(restaurantID);
        this.meal.setMeal_category(categoryName);
        ArrayList<MealAddition> additionList = new ArrayList<MealAddition>();
        this.meal.setMeal_additions(additionList);

        MealFragment mealFragment = MealFragment.newInstance(getMealListByCategory(categoryName));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mealFragment, "MEAL");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onMealSelected(Meal meal) {
        this.meal.setMeal_name(meal.getMeal_name());
        this.meal.setMeal_cooking_time(meal.getMeal_cooking_time());
        this.meal.setMeal_description(meal.getMeal_description());
        this.meal.setMeal_id(meal.getMeal_id());
        this.meal.setMeal_photo_firebase_URL(meal.getMeal_photo_firebase_URL());
        this.meal.setMeal_price(meal.getMeal_price());
        this.meal.setMeal_tags(meal.getMeal_tags());
        this.meal.setMeal_thumbnail(meal.getMeal_thumbnail());
        this.meal.setMealGlutenFree(meal.getMealGlutenFree());
        this.meal.setMealVegan(meal.getMealVegan());
        this.meal.setMealVegetarian(meal.getMealVegetarian());

        AdditionFragment additionFragment = AdditionFragment.newInstance(meal.getMeal_additions());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, additionFragment, "ADDITION");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onAdditionsSelected(ArrayList<MealAddition> selectedAdditions) {
        for(MealAddition a : selectedAdditions) {
            this.meal.getMeal_additions().add(a);
        }

        QuantityFragment quantityFragment = new QuantityFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, quantityFragment, "QUANTITY");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onQuantitySelected(Integer quantity) {
        this.meal.setMeal_quantity(quantity);
        this.order.addMeal(this.meal);
        this.meal = null;
        updatePrice();

        CartFragment cartFragment = CartFragment.newInstance(this.order);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, cartFragment, "CART");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onCartClicked() {
        CartFragment cartFragment = CartFragment.newInstance(this.order);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, cartFragment, "CART");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onConfirmOrderClicked(Order order){
        this.order = order;
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        DatabaseReference ordersReference = firebase.getReference("orders/");
        DatabaseReference keyReference= ordersReference.push();
        this.order.setOrder_id(keyReference.getKey());
        keyReference.setValue(this.order);
        this.order = setNewOrder();

        Intent intent = new Intent(this, MyOrdersActivity.class);
        startActivity(intent);
    }

    @Override
    public void onContinueOrderClicked(Order order){
        this.order = order;
        CategoryFragment categoryFragment = CategoryFragment.newInstance(getCategoryList());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, categoryFragment, "CATEGORY");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onMealDeleted(Order order, Meal meal) {
        this.order = order;
        this.order.delMeal(meal);
        updatePrice();

        CartFragment cartFragment = CartFragment.newInstance(this.order);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, cartFragment, "CART");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onCancelOrderClicked() {
        this.order = setNewOrder();
        CategoryFragment categoryFragment = CategoryFragment.newInstance(getCategoryList());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, categoryFragment, "CATEGORY");
        transaction.addToBackStack(null);
        transaction.commit();
        Intent intent = new Intent(this, UserRestaurantActivity.class);
        intent.putExtra("restaurant_id", restaurantID);
        startActivity(intent);
    }

    private void updatePrice() {
        Double price = 0.0;
        Double mealprice = 0.0;
        for(Meal m : this.order.getMealList()) {
            mealprice = m.getMeal_price();
            for(MealAddition a : m.getMeal_additions()) {
                mealprice += a.getMeal_addition_price();
            }
            mealprice *= m.getMeal_quantity();
            price += mealprice;
        }
    }

    private Order setNewOrder() {
        Order o = new Order();
        o.setUser_id(userID);
        o.setUser_full_name(user != null ? user.getUser_full_name() : "");
        o.setRestaurant_id(restaurantID);
        o.setOrder_price(0.0);
        return o;
    }

    private ArrayList<String> getCategoryList() {
        ArrayList<String> categoryList = new ArrayList<String>();
        for(Meal m : mealList) {
            if(categoryList.indexOf(m.getMeal_category()) == -1)
                categoryList.add(m.getMeal_category());
        }

        return categoryList;
    }

    private ArrayList<Meal> getMealListByCategory(String categoryName) {
        ArrayList<Meal> mealCategoryList = new ArrayList<Meal>();
        for(Meal m : mealList) {
            if(m.getMeal_category().equals(categoryName)) {
                if(m.getMealAvailable() && m.getMealTakeAway())
                    mealCategoryList.add(m);
            }
        }
        return mealCategoryList;
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    private boolean restaurantOrderLimit() {
        //TODO implementare
        return false;
    }
}
