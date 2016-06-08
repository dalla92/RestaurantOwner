package it.polito.group2.restaurantowner.owner.offer;

import android.app.AlertDialog;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.Offer;
import it.polito.group2.restaurantowner.firebasedata.User;
import it.polito.group2.restaurantowner.gallery.GalleryViewActivity;
import it.polito.group2.restaurantowner.owner.AddRestaurantActivity;
import it.polito.group2.restaurantowner.owner.MainActivity;
import it.polito.group2.restaurantowner.owner.MenuRestaurant_page;
import it.polito.group2.restaurantowner.owner.Restaurant_page;
import it.polito.group2.restaurantowner.owner.ReviewsActivity;
import it.polito.group2.restaurantowner.owner.StatisticsActivity;
import it.polito.group2.restaurantowner.owner.my_offers.MyOffersActivity;
import it.polito.group2.restaurantowner.owner.reservations.ReservationActivity;
import it.polito.group2.restaurantowner.user.restaurant_page.UserRestaurantActivity;

public class OfferActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OfferFragment.OnActionListener,
        CategoryFragment.OnActionListener {

    private Offer offer = null;
    private ArrayList<Meal> mealList = null;

    private String restaurantID = null;

    private ProgressDialog mProgressDialog;
    private int progressDialogCounter = 0;
    private final int MODIFY_INFO = 0;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_offer_activity);

        //Toolbar setting
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //User object
        DatabaseReference userRef = FirebaseUtil.getCurrentUserRef();
        if (userRef != null) {
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    showProgressDialog();
                    User user = dataSnapshot.getValue(User.class);
                    hideProgressDialog();
                    setDrawer(user);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else {
            goAway();
        }

        //MealList object
        if (getIntent().getExtras() != null && getIntent().getExtras().getString("restaurant_id") != null) {
            restaurantID = getIntent().getExtras().getString("restaurant_id");
            Query mealsRef = FirebaseUtil.getMealsByRestaurantRef(restaurantID);
            if (mealsRef == null)
                goAway();
            assert mealsRef != null;
            mealsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<Meal> meals = new ArrayList<>();
                    showProgressDialog();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        meals.add(d.getValue(Meal.class));
                    }
                    hideProgressDialog();
                    openOfferFragment(meals);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else {
            goAway();
        }

        //Offer object (for editing purpose)
        if (getIntent().getExtras() != null && getIntent().getExtras().getString("offer_id") != null) {
            String offerID = getIntent().getExtras().getString("offer_id");
            DatabaseReference offerRef = FirebaseUtil.getOfferRef(offerID);
            if (offerRef != null) {
                offerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        showProgressDialog();
                        Offer editOffer = dataSnapshot.getValue(Offer.class);
                        hideProgressDialog();
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
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.owner_offer_activity_onbackalert_title))
                    .setMessage(getResources().getString(R.string.owner_offer_activity_onbackalert_message))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            offer = null;
                            Intent intent = new Intent(getBaseContext(), Restaurant_page.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("restaurant_id", restaurantID);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
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
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        DatabaseReference offersReference = firebase.getReference("offers/");
        DatabaseReference keyReference = offersReference.push();
        this.offer.setOfferID(keyReference.getKey());
        keyReference.setValue(this.offer);
        this.offer = setNewOffer();
        Intent intent = new Intent(this, MyOffersActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSaveListClicked(Offer offer) {
        this.offer = offer;
        OfferFragment offerFragment = OfferFragment.newInstance(offer, this.mealList);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, offerFragment, "OFFER");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onCategoryListRq(Offer offer) {
        this.offer = offer;
        CategoryFragment categoryFragment = CategoryFragment.newInstance(getCategoryList(), offer);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, categoryFragment, "CATEGORY");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onMealListRq(Offer offer) {
        this.offer = offer;
        MealFragment mealFragment = MealFragment.newInstance(mealList, offer);
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
        for (Meal m : mealList) {
            if (categoryList.indexOf(m.getMeal_category()) == -1)
                categoryList.add(m.getMeal_category());
        }

        return categoryList;
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
        if (this.offer != null) {
            if (findViewById(R.id.fragment_container) != null) {
                OfferFragment offerFragment = OfferFragment.newInstance(this.offer, this.mealList);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.fragment_container, offerFragment, "OFFER");
                transaction.commit();
            }
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
                    .with(OfferActivity.this)
                    .load(R.drawable.blank_profile_nav)
                    .centerCrop()
                    .into(nav_picture);
        } else {
            Glide
                    .with(OfferActivity.this)
                    .load(photoUri)
                    .centerCrop()
                    .into(nav_picture);
        }

    }

    private synchronized void showProgressDialog() {
        progressDialogCounter++;
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private synchronized void hideProgressDialog() {
        progressDialogCounter--;
        if (progressDialogCounter <= 0) {
            progressDialogCounter = 0;
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.hide();
            }
        }
    }

    private void goAway() {
        Intent intent = new Intent(this, Restaurant_page.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("restaurant_id", restaurantID);
        startActivity(intent);
        finish();
    }
}
