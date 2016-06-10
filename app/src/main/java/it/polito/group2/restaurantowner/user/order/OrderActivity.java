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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import it.polito.group2.restaurantowner.HaveBreak;
import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.MealAddition;
import it.polito.group2.restaurantowner.firebasedata.Offer;
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

    private Toolbar toolbar;
    private ProgressDialog mProgressDialog;
    private boolean orderStarted = false;

    private String userID;
    private User user;

    private String restaurantID;
    private Restaurant restaurant;

    private ArrayList<Meal> mealList = null;
    private ArrayList<Offer> offerList = null;

    private Order order = null;
    private Meal meal = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_order_activity);

        //Toolbar setting
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgressDialog = FirebaseUtil.initProgressDialog(this);
        FirebaseUtil.showProgressDialog(mProgressDialog);

        //user reference
        DatabaseReference userRef = FirebaseUtil.getCurrentUserRef();
        userID = FirebaseUtil.getCurrentUserId();
        if(userRef == null || userID == null)
            abortActivity();

        //restaurant reference
        if (getIntent().getExtras() == null || getIntent().getExtras().getString("restaurant_id") == null)
            abortActivity();
        restaurantID = getIntent().getExtras().getString("restaurant_id");
        DatabaseReference restaurantRef = FirebaseUtil.getRestaurantRef(restaurantID);
        if(restaurantRef == null)
            abortActivity();

        //meals reference
        DatabaseReference mealsRef = FirebaseUtil.getMealsRef(restaurantID);
        if (mealsRef == null)
            abortActivity();

        //offers refercence
        DatabaseReference offersRef = FirebaseUtil.getOffersRef(restaurantID);
        if (offersRef == null)
            abortActivity();

        //user firebase data getting
        assert userRef != null;
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user == null)
                    abortActivity();
                setDrawer(user);
                //TODO gestire i suoi punti
                startOrder(user);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        //restaurant firebase data getting
        assert restaurantRef != null;
        restaurantRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Restaurant rest = dataSnapshot.getValue(Restaurant.class);
                if(rest == null)
                    abortActivity();
                if(!rest.getTakeAwayAllowed())
                    abortActivity();

                startOrder(rest);

                //orders reference
                Query ordersRef = FirebaseUtil.getOrdersByRestaurantRef(restaurantID);
                if(ordersRef != null) {
                    ordersRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<Order> orders = new ArrayList<>();
                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                orders.add(d.getValue(Order.class));
                            }
                            if(restaurantOrderLimit(orders)) {
                                //TODO lanciare un alert perché il massimo numero di prenotazioni in un'ora è stato raggiunto
                                abortActivity();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        //meals firebase data getting
        assert mealsRef != null;
        mealsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Meal> meals = new ArrayList<>();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    meals.add(d.getValue(Meal.class));
                }
                startOrder(meals);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        //offers firebase data getting
        assert offersRef != null;
        offersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Offer> offers = new ArrayList<>();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    offers.add(d.getValue(Offer.class));
                }
                startOrder(restaurantID, offers);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });



















    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(getSupportFragmentManager().findFragmentByTag("CART") != null){
                CategoryFragment categoryFragment = CategoryFragment.newInstance(getCategoryList(), getActiveOffer());
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, categoryFragment, "CATEGORY");
                transaction.addToBackStack(null);
                transaction.commit();
            } else if(getSupportFragmentManager().findFragmentByTag("CATEGORY") != null) {
                //TODO lanciare un alert che se si fa indietro qui si perde l'ordine
                this.order = null;
                Intent intent = new Intent(this, UserRestaurantActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("restaurant_id", restaurantID);
                startActivity(intent);
                finish();
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

        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private void updatePrice(Calendar now) {
        Double price = 0.0;
        Double mealprice = 0.0;

        for(Meal m : this.order.allMeals()) {
            mealprice = getRightMealPrice(m, now);
            for(MealAddition a : m.allAdditions()) {
                mealprice += a.getMeal_addition_price();
            }
            mealprice *= m.getMeal_quantity();
            price += mealprice;
        }
        order.setOrder_price(price);
    }

    private void savePricesInOffer(Calendar now) {
        for(int i=0; i < this.order.allMeals().size(); i++) {
            this.order.allMeals().get(i).setMeal_price(getRightMealPrice(this.order.allMeals().get(i), now));
        }
    }

    private double getRightMealPrice(Meal meal, Calendar time) {
        Offer offer = getActiveOffer();
        if(offer != null) {
            return offer.getNewMealPrice(meal, time);
        }
        return meal.getMeal_price();
    }

    private Order setNewOrder() {
        Order o = new Order();
        o.setUser_id(userID);
        o.setUser_full_name(user != null ? user.getUser_full_name() : "");
        o.setRestaurant_id(restaurantID);
        o.setOrder_price(0.0);
        return o;
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


    //FRAGMENT CALL BACKS ==========================================================================

    @Override
    public void onCategorySelected(String categoryName) {
        this.meal = new Meal();
        this.meal.setRestaurant_id(restaurantID);
        this.meal.setMeal_category(categoryName);
        ArrayList<MealAddition> additionList = new ArrayList<MealAddition>();
        this.meal.addManyAdditions(additionList);

        MealFragment mealFragment = MealFragment.newInstance(getMealListByCategory(categoryName), getActiveOffer());
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

        AdditionFragment additionFragment = AdditionFragment.newInstance(meal.allAdditions());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, additionFragment, "ADDITION");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onAdditionsSelected(ArrayList<MealAddition> selectedAdditions) {
        for(MealAddition a : selectedAdditions) {
            this.meal.addAddition(a);
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
        Calendar now = Calendar.getInstance();
        updatePrice(now);

        CartFragment cartFragment = CartFragment.newInstance(this.order, getActiveOffer());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, cartFragment, "CART");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onCartClicked() {
        CartFragment cartFragment = CartFragment.newInstance(this.order, getActiveOffer());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, cartFragment, "CART");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onConfirmOrderClicked(Order order){
        this.order = order;
        Calendar now = Calendar.getInstance();
        updatePrice(now);
        savePricesInOffer(now);
        this.order.calendarToOrderDate(now);

        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        DatabaseReference ordersReference = firebase.getReference("orders/");
        DatabaseReference keyReference= ordersReference.push();
        this.order.setOrder_id(keyReference.getKey());
        keyReference.setValue(this.order);
        this.order = setNewOrder();

        Intent intent = new Intent(this, MyOrdersActivity.class);
        startActivity(intent);

        //TODO vedere come attivare il fedelity program
    }

    @Override
    public void onContinueOrderClicked(Order order){
        this.order = order;
        loadCategoryFragment(false);
    }

    @Override
    public void onMealDeleted(Order order, Meal meal) {
        this.order = order;
        this.order.delMeal(meal);
        Calendar now = Calendar.getInstance();
        updatePrice(now);

        CartFragment cartFragment = CartFragment.newInstance(this.order, getActiveOffer());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, cartFragment, "CART");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onCancelOrderClicked() {
        this.order = setNewOrder();
        loadCategoryFragment(false);
        comeBackToRestaurantActivity();
    }

    //MODEL FUNCTIONS ==============================================================================

    private boolean restaurantOrderLimit(ArrayList<Order> orders) {
        Calendar now = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        Calendar stop = Calendar.getInstance();
        start.set(now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH),
                now.get(Calendar.HOUR_OF_DAY), 0, 0);
        stop.setTimeInMillis(start.getTimeInMillis()+65*60*1000);
        int orderCounter = 0;
        for(Order o : orders) {
            if(o.orderDateToCalendar().after(start.getTime()) && o.orderDateToCalendar().before(stop.getTime())) {
                orderCounter++;
            }
        }
        if(restaurant.getRestaurant_orders_per_hour()<=orderCounter)
            return true;
        return false;
    }

    private Offer getActiveOffer() {
        Calendar c = Calendar.getInstance();
        for(Offer o : offerList) {
            if(o.isNowInOffer(c))
                return o;
        }
        return null;
    }

    private ArrayList<String> getCategoryList() {
        ArrayList<String> categoryList = new ArrayList<>();
        for(Meal m : mealList) {
            if(categoryList.indexOf(m.getMeal_category()) == -1)
                categoryList.add(m.getMeal_category());
        }

        return categoryList;
    }

    private ArrayList<Meal> filterAvailableMeals(ArrayList<Meal> meals) {
        ArrayList<Meal> list = new ArrayList<>();
        for(Meal m : meals) {
            if(m.getMealAvailable() && m.getMealTakeAway()) {
                list.add(m);
            }
        }
        return list;
    }

    //ACTIVITY FUNCTIONS ===========================================================================

    private synchronized void startOrder(ArrayList<Meal> meals) {
        if(meals.size() <=0) {
            tempCreateFakeMeals(restaurantID);
        } else {
            mealList = filterAvailableMeals(meals);
            checkForStart();
        }
    }

    private synchronized void startOrder(Restaurant r) {
        restaurant = r;
        checkForStart();
    }

    private synchronized void startOrder(User u) {
        user = u;
        checkForStart();
    }

    private synchronized void startOrder(String restID, ArrayList<Offer> offers) {
        offerList = offers;
        checkForStart();
    }

    private void checkForStart() {
        if(mealList != null &&
                user != null &&
                restaurant != null &&
                offerList != null &&
                !orderStarted) {
            order = setNewOrder();
            FirebaseUtil.hideProgressDialog(mProgressDialog);
            loadCategoryFragment(true);
        }
    }

    private void loadCategoryFragment(boolean start) {
        if (findViewById(R.id.fragment_container) != null) {
            CategoryFragment categoryFragment = CategoryFragment.newInstance(getCategoryList(), getActiveOffer());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if(start) {
                transaction.add(R.id.fragment_container, categoryFragment, "CATEGORY");
            } else {
                transaction.replace(R.id.fragment_container, categoryFragment, "CATEGORY");
                transaction.addToBackStack(null);
            }
            transaction.commit();
        }
    }

    private void setDrawer(User user) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        final MenuItem ownerItem = menu.findItem(R.id.nav_owner);
        MenuItem loginItem = menu.findItem(R.id.nav_login);
        MenuItem logoutItem = menu.findItem(R.id.nav_logout);
        MenuItem myProfileItem = menu.findItem(R.id.nav_my_profile);
        MenuItem myOrdersItem = menu.findItem(R.id.nav_my_orders);
        MenuItem mrResItem = menu.findItem(R.id.nav_my_reservations);
        MenuItem myReviewsItem = menu.findItem(R.id.nav_my_reviews);
        MenuItem myFavItem = menu.findItem(R.id.nav_my_favourites);

        ownerItem.setVisible(false);
        loginItem.setVisible(false);
        logoutItem.setVisible(true);
        myProfileItem.setVisible(true);
        myOrdersItem.setVisible(true);
        mrResItem.setVisible(true);
        myReviewsItem.setVisible(true);
        myFavItem.setVisible(true);

        TextView nav_username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderUsername);
        TextView nav_email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderEmail);
        ImageView nav_picture = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderPicture);

        if (user.getOwnerUser())
            ownerItem.setVisible(true);

        nav_username.setText(user.getUser_full_name());
        nav_email.setText(user.getUser_email());
        String photoUri = user.getUser_photo_firebase_URL();

        if (photoUri == null || photoUri.equals("")) {
            Glide
                    .with(OrderActivity.this)
                    .load(R.drawable.blank_profile_nav)
                    .centerCrop()
                    .into(nav_picture);
        } else {
            Glide
                    .with(OrderActivity.this)
                    .load(photoUri)
                    .centerCrop()
                    .into(nav_picture);
        }

    }

    private void abortActivity() {
        Intent intent = new Intent(this, UserRestaurantList.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void comeBackToRestaurantActivity() {
        Intent intent = new Intent(this, UserRestaurantActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("restaurant_id", restaurantID);
        startActivity(intent);
        finish();
    }

    //TODO quando il sistema è a regime eliminare questo metodo
    private void tempCreateFakeMeals(String restID) {
        String[] cats = {"Primi", "Secondi", "Contorni", "Frutta", "Bevande", "Dolci", "Antipasti", "Specialità"};
        DatabaseReference mealsReference = FirebaseUtil.getMealsRef(restID);
        for(int i=0; i<100; i++) {
            Meal meal = new Meal();
            meal.setMeal_price(5.0);
            meal.setMeal_category(cats[i%8]);
            meal.setMeal_cooking_time(5);
            meal.setMeal_description("Meal " + i + " description.");
            meal.setMeal_name("Meal " + i + " name");
            meal.setMealAvailable(true);
            meal.setMealTakeAway(true);
            meal.setRestaurant_id(restID);
            ArrayList<MealAddition> additions = new ArrayList<>();
            for(int j=0; j<10; j++) {
                MealAddition add = new MealAddition();
                add.setMeal_addition_name("Addition "+j+" name");
                add.setMeal_addition_price(1.0);
            }
            meal.addManyAdditions(additions);
            DatabaseReference keyReference = mealsReference.push();
            meal.setMeal_id(keyReference.getKey());
            keyReference.setValue(meal);
        }
    }
}
