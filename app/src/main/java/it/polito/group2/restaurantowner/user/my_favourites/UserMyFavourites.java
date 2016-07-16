package it.polito.group2.restaurantowner.user.my_favourites;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.DrawerUtil;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.Utils.OnBackUtil;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
import it.polito.group2.restaurantowner.firebasedata.RestaurantPreview;
import it.polito.group2.restaurantowner.firebasedata.User;
import it.polito.group2.restaurantowner.user.restaurant_page.UserRestaurantActivity;

import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UserMyFavourites extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String user_id;
    private ListView listView;
    private ArrayList<RestaurantPreview> favourite_restaurants;
    private Context context;
    private Toolbar toolbar;
    private FirebaseDatabase firebase;
    private ValueEventListener l;
    private ProgressDialog mProgressDialog;
    private FavouriteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_my_favourites);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebase = FirebaseDatabase.getInstance();
        favourite_restaurants = new ArrayList<>();

        mProgressDialog = FirebaseUtil.initProgressDialog(this);
        FirebaseUtil.showProgressDialog(mProgressDialog);
        user_id = FirebaseUtil.getCurrentUserId();

        if(user_id == null){
            Toast.makeText(UserMyFavourites.this, getResources().getString(R.string.need_login), Toast.LENGTH_SHORT).show();
            finish();
        }
        //list view implementation
        listView = (ListView) findViewById(R.id.list);
        adapter = new FavouriteAdapter(this, R.layout.favourite_layout, favourite_restaurants);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // ListView Clicked item value
                RestaurantPreview itemValue = (RestaurantPreview) listView.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), UserRestaurantActivity.class);
                intent.putExtra("restaurant_id", itemValue.getRestaurant_id());
                startActivity(intent);
            }
        });

        setDrawer();

        get_favourites_from_firebase();
    }

    public void get_favourites_from_firebase(){
        DatabaseReference ref = firebase.getReference("users/" + user_id + "/" + "favourites_restaurants");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                HashMap<String, Boolean> favourites = (HashMap<String, Boolean>) snapshot.getValue();
                final ArrayList<String> favouritesID = new ArrayList<>();
                if(favourites == null) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.none_bookmarks), Toast.LENGTH_SHORT).show();
                    FirebaseUtil.hideProgressDialog(mProgressDialog);
                }
                else {
                    for (String key : favourites.keySet()) {
                        favouritesID.add(key);
                    }

                    if (favouritesID.isEmpty()) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.none_bookmarks), Toast.LENGTH_SHORT).show();
                        FirebaseUtil.hideProgressDialog(mProgressDialog);
                    }
                    else {
                        for (int i = 0; i < favouritesID.size(); i++) {
                            final int index = i;
                            DatabaseReference ref = firebase.getReference("restaurants_previews/" + favouritesID.get(i));
                            Log.d("prova", favouritesID.get(i));
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    RestaurantPreview restaurantPreview = snapshot.getValue(RestaurantPreview.class);
                                    favourite_restaurants.add(restaurantPreview);
                                    adapter.notifyDataSetChanged();
                                    if(index == favouritesID.size() - 1)
                                        FirebaseUtil.hideProgressDialog(mProgressDialog);
                                }

                                @Override
                                public void onCancelled(DatabaseError firebaseError) {
                                    System.out.println("The read failed: " + firebaseError.getMessage());
                                }
                            });
                        }
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    private void setDrawer() {
        //navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        final MenuItem ownerItem = menu.findItem(R.id.nav_owner);
        MenuItem loginItem = menu.findItem(R.id.nav_login);
        MenuItem logoutItem = menu.findItem(R.id.nav_logout);
        MenuItem myProfileItem = menu.findItem(R.id.nav_my_profile);
        MenuItem myOrdersItem = menu.findItem(R.id.nav_my_orders);
        MenuItem mrResItem =  menu.findItem(R.id.nav_my_reservations);
        MenuItem myReviewsItem = menu.findItem(R.id.nav_my_reviews);
        MenuItem myFavItem = menu.findItem(R.id.nav_my_favourites);

        ownerItem.setVisible(false);
        String userID = FirebaseUtil.getCurrentUserId();
        if (userID != null) {
            loginItem.setVisible(false);
            logoutItem.setVisible(true);
            myProfileItem.setVisible(true);
            myOrdersItem.setVisible(true);
            mrResItem.setVisible(true);
            myReviewsItem.setVisible(true);
            myFavItem.setVisible(true);
            //navigationView.inflateHeaderView(R.layout.nav_header_login);

            DatabaseReference userRef = firebase.getReference("users/" + userID);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    TextView nav_username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderUsername);
                    TextView nav_email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderEmail);
                    TextView nav_points = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderPoints);
                    ImageView nav_picture = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderPicture);
                    User target = dataSnapshot.getValue(User.class);

                    if (target.getOwnerUser())
                        ownerItem.setVisible(true);

                    nav_username.setText(target.getUser_full_name());
                    nav_email.setText(target.getUser_email());
                    nav_points.setText(String.valueOf(target.getUser_fidelity_points()));

                    String photoUri = target.getUser_photo_firebase_URL();
                    if (!isFinishing()) {
                        if (photoUri == null || photoUri.equals("")) {
                            Glide
                                    .with(UserMyFavourites.this)
                                    .load(R.drawable.blank_profile_nav)
                                    .centerCrop()
                                    .into(nav_picture);
                        } else {
                            Glide
                                    .with(UserMyFavourites.this)
                                    .load(photoUri)
                                    .centerCrop()
                                    .into(nav_picture);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("prova", "cancelled");
                }
            });

        }
        else{
            loginItem.setVisible(true);
            logoutItem.setVisible(false);
            myProfileItem.setVisible(false);
            myOrdersItem.setVisible(false);
            mrResItem.setVisible(false);
            myReviewsItem.setVisible(false);
            myFavItem.setVisible(false);

        }

        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public void onResume(){
        super.onResume();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return DrawerUtil.drawer_user_not_restaurant_page(this, item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            OnBackUtil.clean_stack_and_go_to_user_restaurant_list(this);
        }
    }

}