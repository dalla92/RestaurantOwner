package it.polito.group2.restaurantowner.owner.my_offers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.Offer;
import it.polito.group2.restaurantowner.firebasedata.User;
import it.polito.group2.restaurantowner.gallery.GalleryViewActivity;
import it.polito.group2.restaurantowner.owner.MainActivity;
import it.polito.group2.restaurantowner.owner.MenuRestaurant_page;
import it.polito.group2.restaurantowner.owner.Restaurant_page;
import it.polito.group2.restaurantowner.owner.statistics.StatisticsActivity;
import it.polito.group2.restaurantowner.owner.offer.OfferActivity;
import it.polito.group2.restaurantowner.owner.reservations.ReservationActivity;
import it.polito.group2.restaurantowner.owner.reviews.ReviewsActivity;

public class MyOffersActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private Toolbar toolbar;
    private String restaurantID;
    private ProgressDialog mProgressDialog;
    private ArrayList<Offer> offerList = null;
    private ArrayList<Meal> mealList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_myoffers_activity);

        //Toolbar setting
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgressDialog = FirebaseUtil.initProgressDialog(this);
        FirebaseUtil.showProgressDialog(mProgressDialog);

        //User object
        DatabaseReference userRef = FirebaseUtil.getCurrentUserRef();
        if (userRef != null) {
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    FirebaseUtil.hideProgressDialog(mProgressDialog);
                    setDrawer(user);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        } else {
            goAway();
        }

        if (getIntent().getExtras() != null && getIntent().getExtras().getString("restaurant_id") != null) {
            restaurantID = getIntent().getExtras().getString("restaurant_id");
            final Query offersRef = FirebaseUtil.getOffersRef(restaurantID);
            if (offersRef == null)
                goAway();
            assert offersRef != null;
            offersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<Offer> offers = new ArrayList<>();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        offers.add(d.getValue(Offer.class));
                    }
                    offerList = offers;
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
                            mealList = meals;
                            setOfferList();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else {
            goAway();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(id==R.id.action_my_restaurants){
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    MainActivity.class);
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
        } else if (id == R.id.icon_new_offer) {
            Intent intent4 = new Intent(
                    getApplicationContext(),
                    OfferActivity.class); //here Filippo must insert his class name
            Bundle b4 = new Bundle();
            b4.putString("restaurant_id", restaurantID);
            intent4.putExtras(b4);
            startActivity(intent4);
            return true;
        }

        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.owner_myoffers_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                Intent intent = new Intent(this, OfferActivity.class);
                intent.putExtra("restaurant_id", restaurantID);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    //MODEL FUNCTIONS ==============================================================================

    private void setOfferList() {
        final RecyclerView list = (RecyclerView) findViewById(R.id.offer_list);
        assert list != null;
        list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        list.setNestedScrollingEnabled(false);
        OfferAdapter adapter = new OfferAdapter(this, offerList, mealList);
        list.setAdapter(adapter);
    }

    //ACTIVITY FUNCTIONS ===========================================================================

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
                    .with(MyOffersActivity.this)
                    .load(R.drawable.blank_profile_nav)
                    .centerCrop()
                    .into(nav_picture);
        } else {
            Glide
                    .with(MyOffersActivity.this)
                    .load(photoUri)
                    .centerCrop()
                    .into(nav_picture);
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
