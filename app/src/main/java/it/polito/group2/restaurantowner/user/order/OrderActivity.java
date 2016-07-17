package it.polito.group2.restaurantowner.user.order;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.DrawerUtil;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.Utils.OnBackUtil;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.MealAddition;
import it.polito.group2.restaurantowner.firebasedata.Offer;
import it.polito.group2.restaurantowner.firebasedata.Order;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
import it.polito.group2.restaurantowner.firebasedata.User;
import it.polito.group2.restaurantowner.user.restaurant_list.SendNotificationAsync;

public class OrderActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CategoryFragment.OnActionListener,
        MealFragment.OnActionListener,
        AdditionFragment.OnActionListener,
        QuantityFragment.OnActionListener,
        CartFragment.OnActionListener {

    private static final int FIDELITY_MIN_POINT_TO_DISCOUNT = 100;
    private static final int FIDELITY_APPLIED_DISCOUNT = 20;
    private static final int FIDELITY_POINT_RECHARGE = 1;
    private static final int FIDELITY_MIN_ORDER_TO_CHARGE = 10;

    private Toolbar toolbar;
    private ProgressDialog mProgressDialog;

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
        if (userRef == null || userID == null)
            OnBackUtil.clean_stack_and_go_to_restaurant_page(this, restaurantID);

        //restaurant reference
        if (getIntent().getExtras() == null || getIntent().getExtras().getString("restaurant_id") == null)
            OnBackUtil.clean_stack_and_go_to_restaurant_page(this, restaurantID);
        restaurantID = getIntent().getExtras().getString("restaurant_id");
        DatabaseReference restaurantRef = FirebaseUtil.getRestaurantRef(restaurantID);
        if (restaurantRef == null)
            OnBackUtil.clean_stack_and_go_to_restaurant_page(this, restaurantID);

        //meals reference
        DatabaseReference mealsRef = FirebaseUtil.getMealsRef(restaurantID);
        if (mealsRef == null)
            OnBackUtil.clean_stack_and_go_to_user_restaurant_list(this);

        //offers refercence
        DatabaseReference offersRef = FirebaseUtil.getOffersRef(restaurantID);

        //user firebase data getting
        assert userRef != null;
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null)
                    OnBackUtil.clean_stack_and_go_to_restaurant_page(getApplicationContext(), restaurantID);
                setDrawer(user);
                startOrder(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //restaurant firebase data getting
        assert restaurantRef != null;
        restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Restaurant rest = dataSnapshot.getValue(Restaurant.class);
                if (rest == null)
                    OnBackUtil.clean_stack_and_go_to_restaurant_page(getApplicationContext(), restaurantID);
                assert rest != null;
                if (!rest.getTakeAwayAllowed())
                    OnBackUtil.clean_stack_and_go_to_user_restaurant_page(getApplicationContext(), restaurantID);

                startOrder(rest);

                //orders reference
                Query ordersRef = FirebaseUtil.getOrdersByRestaurantRef(restaurantID);
                if (ordersRef != null) {
                    ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<Order> orders = new ArrayList<>();
                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                orders.add(d.getValue(Order.class));
                            }
                            if (restaurantOrderLimit(orders)) {
                                new AlertDialog.Builder(getApplicationContext())
                                        .setTitle(getString(R.string.user_order_alert_limit_title))
                                        .setMessage(getString(R.string.user_order_alert_limit_message))
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setNegativeButton(android.R.string.ok, null).show();
                                OnBackUtil.clean_stack_and_go_to_user_restaurant_page(getApplicationContext(), restaurantID);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //meals firebase data getting
        assert mealsRef != null;
        mealsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Meal> meals = new ArrayList<>();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    meals.add(d.getValue(Meal.class));
                }
                startOrder(meals);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //offers firebase data getting
        if (offersRef != null) {
            offersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<Offer> offers = new ArrayList<>();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        offers.add(d.getValue(Offer.class));
                    }
                    startOrder(restaurantID, offers);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else {
            startOrder(restaurantID, new ArrayList<Offer>());
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(getSupportFragmentManager().getBackStackEntryCount() <= 1) {
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.user_order_alert_back_title))
                        .setMessage(getResources().getString(R.string.user_order_alert_back_message))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                order = null;
                                OnBackUtil.clean_stack_and_go_to_user_restaurant_page(getApplicationContext(), restaurantID);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }

            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            loadCategoryFragment(false);

        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return DrawerUtil.drawer_user_not_restaurant_page(this, item);
    }

    //FRAGMENT CALL BACKS ==========================================================================

    @Override
    public void onCategorySelected(String categoryName) {
        this.meal = new Meal();
        this.meal.setRestaurant_id(restaurantID);
        this.meal.setMeal_category(categoryName);
        ArrayList<MealAddition> additionList = new ArrayList<>();
        this.meal.addManyAdditions(additionList);

        MealFragment mealFragment = MealFragment.newInstance(getMealListByCategory(categoryName), order.getOffer_applied());
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
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(meal.allAdditions().size() > 0) {
            AdditionFragment additionFragment = AdditionFragment.newInstance(meal.allAdditions());
            transaction.replace(R.id.fragment_container, additionFragment, "ADDITION");
        } else {
            QuantityFragment quantityFragment = new QuantityFragment();
            transaction.replace(R.id.fragment_container, quantityFragment, "QUANTITY");
        }
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onAdditionsSelected(ArrayList<MealAddition> selectedAdditions) {
        for (MealAddition a : selectedAdditions) {
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
        loadCartFragment();
    }

    @Override
    public void onCartClicked() {
        loadCartFragment();
    }

    @Override
    public void onOrderConfirm(Order order) {
        this.order = order;
        Calendar now = Calendar.getInstance();
        updatePrice(now);
        savePricesInOffer(now);
        this.order.calendarToOrderDate(now);

        DatabaseReference ordersReference = FirebaseUtil.getOrdersRef();
        DatabaseReference keyReference = ordersReference.push();
        if(keyReference == null)
            OnBackUtil.clean_stack_and_go_to_user_restaurant_page(this, restaurantID);
        assert keyReference != null;
        this.order.setOrder_id(keyReference.getKey());
        final Restaurant temp = this.restaurant;
        keyReference.setValue(this.order).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                String title = getResources().getString(R.string.new_order_notification);
                new SendNotificationAsync().execute(temp.getRestaurant_name(), restaurantID + "order", title);
            }
        });
        if(this.order.getFidelity_applied() || this.restaurant.getFidelityProgramAccepted()) {
            DatabaseReference userRef = FirebaseUtil.getCurrentUserRef();
            if(userRef != null) {
                DatabaseReference pointRef = userRef.child("user_fidelity_points");
                pointRef.setValue(this.user.getUser_fidelity_points());
            }
        }

        this.order = setNewOrder();

        OnBackUtil.clean_stack_and_go_to_user_restaurant_page(this, restaurantID);
    }

    @Override
    public void onOrderContinue(Order order) {
        this.order = order;
        loadCategoryFragment(false);
    }

    @Override
    public void onOrderChange(Order order) {
        this.order = order;
        Calendar now = Calendar.getInstance();
        updatePrice(now);
        loadCartFragment();
    }

    @Override
    public void onOrderDelete() {
        this.order = setNewOrder();
        loadCategoryFragment(false);
        OnBackUtil.clean_stack_and_go_to_user_restaurant_page(getApplicationContext(), restaurantID);
    }

    //MODEL FUNCTIONS ==============================================================================

    private void updatePrice(Calendar now) {
        Double price = 0.0;
        Double mealprice;
        for (Meal m : this.order.allMeals()) {
            mealprice = getRightMealPrice(m, now);
            for (MealAddition a : m.allAdditions()) {
                mealprice += a.getMeal_addition_price();
            }
            mealprice *= m.getMeal_quantity();
            price += mealprice;
        }
        if(order.getFidelity_applied())
            price -= price*(FIDELITY_APPLIED_DISCOUNT/100);
        order.setOrder_price(price);
    }

    private void savePricesInOffer(Calendar now) {
        for (int i = 0; i < this.order.allMeals().size(); i++) {
            this.order.allMeals().get(i).setMeal_price(getRightMealPrice(this.order.allMeals().get(i), now));
        }

        if(order.getFidelity_applied()) {
            user.setUser_fidelity_points(user.getUser_fidelity_points() - FIDELITY_MIN_POINT_TO_DISCOUNT);
        } else {
            if(restaurant.getFidelityProgramAccepted()) {
                user.setUser_fidelity_points(user.getUser_fidelity_points() +
                        ((int) (this.order.getOrder_price() / FIDELITY_MIN_ORDER_TO_CHARGE)) * FIDELITY_POINT_RECHARGE);
            }
        }
    }

    private double getRightMealPrice(Meal meal, Calendar time) {
        if(order.getOffer_applied() != null) {
            return order.getOffer_applied().getNewMealPrice(meal, time);
        }
        return meal.getMeal_price();
    }

    private Order setNewOrder() {
        Order o = new Order();
        o.setUser_id(userID);
        o.setUser_full_name(user != null ? user.getUser_full_name() : "");
        o.setRestaurant_id(restaurantID);
        o.setOrder_price(0.0);
        if(restaurant.getFidelityProgramAccepted() && user.getUser_fidelity_points() >= FIDELITY_MIN_POINT_TO_DISCOUNT)
            o.setFidelity_applied(true);
        else
            o.setFidelity_applied(false);
        o.setOffer_applied(getActiveOffer());
        return o;
    }

    private ArrayList<Meal> getMealListByCategory(String categoryName) {
        ArrayList<Meal> mealCategoryList = new ArrayList<>();
        for (Meal m : mealList) {
            if (m.getMeal_category().equals(categoryName)) {
                if (m.getMealAvailable() && m.getMealTakeAway())
                    mealCategoryList.add(m);
            }
        }
        return mealCategoryList;
    }

    private boolean restaurantOrderLimit(ArrayList<Order> orders) {
        Calendar now = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        Calendar stop = Calendar.getInstance();
        start.set(now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH),
                now.get(Calendar.HOUR_OF_DAY), 0, 0);
        stop.setTimeInMillis(start.getTimeInMillis() + 65 * 60 * 1000);
        int orderCounter = 0;
        for (Order o : orders) {
            if (o.orderDateToCalendar().after(start.getTime()) && o.orderDateToCalendar().before(stop.getTime())) {
                orderCounter++;
                if (orderCounter > restaurant.getRestaurant_orders_per_hour())
                    return true;
            }
        }
        return false;
    }

    private Offer getActiveOffer() {
        Boolean bo;
        Calendar c = Calendar.getInstance();
        if (offerList.size() > 0) {
            for (Offer o : offerList) {
                bo = o.isNowInOffer(c);
                if (bo) {
                    return o;
                }
            }
        }
        return null;
    }

    private ArrayList<String> getCategoryList() {
        ArrayList<String> categoryList = new ArrayList<>();
        for (Meal m : mealList) {

            if (categoryList.indexOf(m.getMeal_category()) == -1)
                categoryList.add(m.getMeal_category());
        }

        return categoryList;
    }

    private ArrayList<Meal> filterAvailableMeals(ArrayList<Meal> meals) {
        ArrayList<Meal> list = new ArrayList<>();
        for (Meal m : meals) {
            if (m.getMealAvailable() && m.getMealTakeAway()) {
                list.add(m);
            }
        }
        return list;
    }

    //ACTIVITY FUNCTIONS ===========================================================================

    private synchronized void startOrder(ArrayList<Meal> meals) {
        mealList = filterAvailableMeals(meals);
        checkForStart();
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
        assert restID != null;
        offerList = offers;
        checkForStart();
    }

    private void checkForStart() {
        if (mealList != null &&
                user != null &&
                restaurant != null &&
                offerList != null) {
            order = setNewOrder();

            FirebaseUtil.hideProgressDialog(mProgressDialog);
            loadCategoryFragment(true);
        }
    }

    private void loadCategoryFragment(boolean start) {
        if (findViewById(R.id.fragment_container) != null) {
            CategoryFragment categoryFragment = CategoryFragment.newInstance(getCategoryList(), order.getOffer_applied());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (start) {
                transaction.add(R.id.fragment_container, categoryFragment, "CATEGORY");
            } else {
                transaction.replace(R.id.fragment_container, categoryFragment, "CATEGORY");
                transaction.addToBackStack(null);
            }
            transaction.commit();
        }
    }

    private void loadCartFragment() {
        if (findViewById(R.id.fragment_container) != null) {
            CartFragment cartFragment = CartFragment.newInstance(this.order);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, cartFragment, "CART");
            transaction.addToBackStack(null);
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
        TextView nav_points = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderPoints);
        ImageView nav_picture = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderPicture);

        nav_username.setText(user.getUser_full_name());
        nav_email.setText(user.getUser_email());
        nav_points.setText(String.valueOf(user.getUser_fidelity_points()));

        String photoUri = user.getUser_photo_firebase_URL();
        if(photoUri == null || photoUri.equals("")) {
            Glide
                    .with(OrderActivity.this)
                    .load(R.drawable.blank_profile_nav)
                    .centerCrop()
                    .into(nav_picture);
        }
        else{
            Glide
                    .with(OrderActivity.this)
                    .load(photoUri)
                    .centerCrop()
                    .into(nav_picture);
        }


    }

    /*
    private void tempCreateFakeMeals(String restID) {
        String[] cats = {"Primi", "Secondi", "Contorni", "Frutta", "Bevande", "Dolci", "Antipasti", "Specialità"};
        DatabaseReference mealsReference = FirebaseUtil.getMealsRef(restID);
        for (int i = 0; i < 20; i++) {
            Meal meal = new Meal();
            meal.setMeal_price(5.0);
            meal.setMeal_category(cats[i % 8]);
            meal.setMeal_cooking_time(5);
            meal.setMeal_description("Meal " + i + " description.");
            meal.setMeal_name("Meal " + i + " name");
            meal.setMealAvailable(true);
            meal.setMealTakeAway(true);
            meal.setRestaurant_id(restID);
            ArrayList<MealAddition> additions = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                MealAddition add = new MealAddition();
                add.setMeal_addition_name("Addition " + j + " name");
                add.setMeal_addition_price(1.0);
                additions.add(add);
            }
            meal.addManyAdditions(additions);
            DatabaseReference keyReference = mealsReference.push();
            meal.setMeal_id(keyReference.getKey());
            keyReference.setValue(meal);
        }
    }
    */

}
