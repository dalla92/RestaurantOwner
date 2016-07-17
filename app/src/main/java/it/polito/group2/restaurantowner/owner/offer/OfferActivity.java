package it.polito.group2.restaurantowner.owner.offer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
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
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.DrawerUtil;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.Utils.OnBackUtil;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.Offer;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
import it.polito.group2.restaurantowner.firebasedata.User;
import it.polito.group2.restaurantowner.gallery.GalleryViewActivity;
import it.polito.group2.restaurantowner.owner.AddRestaurantActivity;
import it.polito.group2.restaurantowner.owner.MainActivity;
import it.polito.group2.restaurantowner.owner.MenuRestaurant_page;
import it.polito.group2.restaurantowner.owner.Restaurant_page;
import it.polito.group2.restaurantowner.owner.reviews.ReviewsActivity;
import it.polito.group2.restaurantowner.owner.statistics.StatisticsActivity;
import it.polito.group2.restaurantowner.owner.my_offers.MyOffersActivity;
import it.polito.group2.restaurantowner.owner.reservations.ReservationActivity;
import it.polito.group2.restaurantowner.user.restaurant_list.SendNotificationAsync;
import it.polito.group2.restaurantowner.user.restaurant_page.UserRestaurantActivity;

public class OfferActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OfferFragment.OnActionListener,
        CategoryFragment.OnActionListener,
        MealFragment.OnActionListener {

    private Offer offer = null;
    private ArrayList<Meal> mealList = null;

    private String restaurantID = null;
    private Restaurant restaurant = null;

    private ProgressDialog mProgressDialog;
    private final int MODIFY_INFO = 0;
    private Toolbar toolbar;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);
    private FirebaseDatabase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_offer_activity);

        //Toolbar setting
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgressDialog = FirebaseUtil.initProgressDialog(this);
        FirebaseUtil.showProgressDialog(mProgressDialog);

        //User object
        firebase = FirebaseDatabase.getInstance();

        //User object
        DatabaseReference userRef = FirebaseUtil.getCurrentUserRef();
        if (userRef == null) {
            goAway();
        }
        setDrawer();

        if (getIntent().getExtras() != null && getIntent().getExtras().getString("restaurant_id") != null) {
            restaurantID = getIntent().getExtras().getString("restaurant_id");
            DatabaseReference restaurantRef = FirebaseUtil.getRestaurantRef(restaurantID);
            if (restaurantRef != null) {
                restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        restaurant = dataSnapshot.getValue(Restaurant.class);
                        FirebaseUtil.hideProgressDialog(mProgressDialog);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
            DatabaseReference mealsRef = FirebaseUtil.getMealsRef(restaurantID);
            if (mealsRef == null)
                goAway();
            assert mealsRef != null;
            mealsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<Meal> meals = new ArrayList<>();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        meals.add(d.getValue(Meal.class));
                    }
                    FirebaseUtil.hideProgressDialog(mProgressDialog);
                    openOfferFragment(meals);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        } else {
            goAway();
        }

        //Offer object (for editing purpose)
        if (getIntent().getExtras() != null && getIntent().getExtras().getString("offer_id") != null) {
            String offerID = getIntent().getExtras().getString("offer_id");
            DatabaseReference offerRef = FirebaseUtil.getOfferRef(restaurantID, offerID);
            if (offerRef != null) {
                offerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Offer editOffer = dataSnapshot.getValue(Offer.class);
                        FirebaseUtil.hideProgressDialog(mProgressDialog);
                        openOfferFragment(editOffer);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            } else {
                openOfferFragment(setNewOffer());
            }
        } else {
            openOfferFragment(setNewOffer());
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(getSupportFragmentManager().getBackStackEntryCount() < 1) {
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.owner_offer_activity_onbackalert_title))
                        .setMessage(getResources().getString(R.string.owner_offer_activity_onbackalert_message))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                offer = null;
                                OfferActivity.super.onBackPressed();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            } else {
                OfferActivity.super.onBackPressed();
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
// Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(id==R.id.action_my_restaurants){
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    MainActivity.class);
            startActivity(intent1);
            return true;
        } else if(id==R.id.action_show_as) {
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    UserRestaurantActivity.class);
            Bundle b = new Bundle();
            b.putString("restaurant_id", restaurantID);
            intent1.putExtras(b);
            startActivity(intent1);
            return true;
        } else if(id==R.id.action_gallery) {
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    GalleryViewActivity.class);
            Bundle b = new Bundle();
            b.putString("restaurant_id", restaurantID);
            intent1.putExtras(b);
            startActivity(intent1);
            return true;
        } else if(id==R.id.action_menu) {
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    MenuRestaurant_page.class);
            Bundle b = new Bundle();
            b.putString("restaurant_id", restaurantID);
            intent1.putExtras(b);
            startActivity(intent1);
            return true;
        } else if(id==R.id.action_offers) {
            Intent intent2 = new Intent(
                    getApplicationContext(),
                    MyOffersActivity.class);
            Bundle b2 = new Bundle();
            b2.putString("restaurant_id", restaurantID);
            intent2.putExtras(b2);
            startActivity(intent2);
            return true;
        } else if(id==R.id.action_reservations){
            Intent intent3 = new Intent(
                    getApplicationContext(),
                    ReservationActivity.class);
            Bundle b3 = new Bundle();
            b3.putString("restaurant_id", restaurantID);
            intent3.putExtras(b3);
            startActivity(intent3);
            return true;
        } else if(id==R.id.action_reviews){
            Intent intent4 = new Intent(
                    getApplicationContext(),
                    ReviewsActivity.class); //here Filippo must insert his class name
            Bundle b4 = new Bundle();
            b4.putString("restaurant_id", restaurantID);
            intent4.putExtras(b4);
            startActivity(intent4);
            return true;
        } else if(id==R.id.action_statistics){
            Intent intent5 = new Intent(
                    getApplicationContext(),
                    StatisticsActivity.class); //here Filippo must insert his class name
            Bundle b5 = new Bundle();
            b5.putString("restaurant_id", restaurantID);
            intent5.putExtras(b5);
            startActivity(intent5);
            return true;
        } else if(id==R.id.action_edit){
            Intent intent6 = new Intent(
                    getApplicationContext(),
                    AddRestaurantActivity.class);
            intent6.putExtra("Restaurant", "");
            final AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
            assert appbar != null;
            appbar.setExpanded(false);
            startActivityForResult(intent6, MODIFY_INFO);
            return true;
        }
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //FRAGMENT CALL BACKS ==========================================================================

    @Override
    public void onSaveClicked(Offer offer) {
        this.offer = offer;
        if(this.offer.getOfferID() != null && !this.offer.getOfferID().equals("")) {
            DatabaseReference offersReference = FirebaseUtil.getOfferRef(restaurantID, this.offer.getOfferID());
            offersReference.setValue(this.offer);
        } else {
            DatabaseReference offersReference = FirebaseUtil.getOffersRef(restaurantID);
            DatabaseReference keyReference = offersReference.push();
            this.offer.setOfferID(keyReference.getKey());
            keyReference.setValue(this.offer);
            if(this.offer.getOfferEnabled() && restaurant != null) {
                String title = getResources().getString(R.string.new_offer_notification);
                new SendNotificationAsync().execute(restaurant.getRestaurant_name(), restaurantID+ "offer", title);
            }
        }
        this.offer = null;
        Intent intent = new Intent(this, MyOffersActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("restaurant_id", restaurantID);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSaveListClicked(Offer offer) {
        this.offer = offer;
        OfferFragment offerFragment = OfferFragment.newInstance(this.offer, this.mealList);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, offerFragment, "OFFER");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onCategoryListRq(Offer offer) {
        this.offer = offer;
        CategoryFragment categoryFragment = CategoryFragment.newInstance(getCategoryList(),  this.offer);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, categoryFragment, "CATEGORY");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onMealListRq(Offer offer) {
        this.offer = offer;
        MealFragment mealFragment = MealFragment.newInstance(this.mealList, this.offer);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mealFragment, "MEAL");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //MODEL FUNCTIONS ==============================================================================

    private Offer setNewOffer() {
        Offer o = new Offer();
        o.setRestaurantID(restaurantID);
        o.setUserID(FirebaseUtil.getCurrentUserId());
        o.setOfferEnabled(true);
        return o;
    }

    private ArrayList<String> getCategoryList() {
        ArrayList<String> categoryList = new ArrayList<>();
        if(mealList.size() > 0) {
            for (Meal m : mealList) {
                if (categoryList.indexOf(m.getMeal_category()) == -1)
                    categoryList.add(m.getMeal_category());
            }
        }
        return categoryList;
    }

    public void showFromDatePickerDialog(final View v) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dialog =  new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                TextView date = (TextView) v.findViewById(R.id.text_from_date);
                Calendar c = Calendar.getInstance();
                if(c.get(Calendar.YEAR) == year &&  c.get(Calendar.MONTH) == monthOfYear &&  c.get(Calendar.DAY_OF_MONTH) == dayOfMonth)
                    date.setText(getString(R.string.owner_offer_fragment_offer_label_today));
                else {
                    c.set(Calendar.YEAR, year);
                    c.set(Calendar.MONTH, monthOfYear);
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    date.setText(dateFormat.format(c.getTime()));
                }
            }
        }, year, month, day);
        dialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        dialog.show();
    }

    public void showToDatePickerDialog(final View v) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dialog =  new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                TextView date = (TextView) v.findViewById(R.id.text_to_date);
                Calendar c = Calendar.getInstance();
                if(c.get(Calendar.YEAR) == year &&  c.get(Calendar.MONTH) == monthOfYear &&  c.get(Calendar.DAY_OF_MONTH) == dayOfMonth)
                    date.setText(getString(R.string.owner_offer_fragment_offer_label_today));
                else {
                    c.set(Calendar.YEAR, year);
                    c.set(Calendar.MONTH, monthOfYear);
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    date.setText(dateFormat.format(c.getTime()));
                }
            }
        }, year, month, day);

        dialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        dialog.show();
    }

    //ACTIVITY FUNCTIONS ===========================================================================

    private synchronized void openOfferFragment(Offer offer) {
        this.offer = offer;
        if (this.mealList != null) {
            if (findViewById(R.id.fragment_container) != null) {
                OfferFragment offerFragment = OfferFragment.newInstance(this.offer, this.mealList);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.fragment_container, offerFragment, "OFFER");
                transaction.commit();
            }
        }
    }

    private synchronized void openOfferFragment(ArrayList<Meal> mealList) {
        this.mealList = mealList;
        if(this.mealList == null) {
            this.mealList = new ArrayList<>();
        }
        if (this.offer != null) {
            if (findViewById(R.id.fragment_container) != null) {
                OfferFragment offerFragment = OfferFragment.newInstance(this.offer, this.mealList);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.fragment_container, offerFragment, "OFFER");
                transaction.commit();
            }
        }
    }

    private void setDrawer() {
        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        String userID = FirebaseUtil.getCurrentUserId();
        if (userID != null) {

            DatabaseReference userRef = firebase.getReference("users/" + userID);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    TextView nav_username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderUsername);
                    TextView nav_email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderEmail);
                    ImageView nav_picture = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderPicture);
                    User target = dataSnapshot.getValue(it.polito.group2.restaurantowner.firebasedata.User.class);
                    TextView nav_points = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderPoints);
                    nav_points.setText(target.getUser_fidelity_points() + " " + getString(R.string.points));

                    nav_username.setText(target.getUser_full_name());
                    nav_email.setText(target.getUser_email());

                    String photoUri = target.getUser_photo_firebase_URL();
                    if (photoUri == null || photoUri.equals("")) {
                        Glide
                                .with(getApplicationContext())
                                .load(R.drawable.blank_profile_nav)
                                .centerCrop()
                                .into(nav_picture);
                    } else {
                        Glide
                                .with(getApplicationContext())
                                .load(photoUri)
                                .centerCrop()
                                .into(nav_picture);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("prova", "cancelled");
                }
            });

        }

        Menu menu = navigationView.getMenu();
        MenuItem i = menu.findItem(R.id.action_edit);
        i.setVisible(false);
        MenuItem i2 = menu.findItem(R.id.action_show_as);
        i2.setVisible(false);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressWarnings("StatementWithEmptyBody")
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Handle navigation view item clicks here.
                return DrawerUtil.drawer_owner_not_restaurant_page(OfferActivity.this, item, restaurantID);
            }
        });
    }

    private void goAway() {
        Intent intent = new Intent(this, Restaurant_page.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("restaurant_id", restaurantID);
        startActivity(intent);
        finish();
    }
}
