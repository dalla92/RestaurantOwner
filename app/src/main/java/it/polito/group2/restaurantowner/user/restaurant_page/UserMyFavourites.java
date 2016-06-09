package it.polito.group2.restaurantowner.user.restaurant_page;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
import it.polito.group2.restaurantowner.firebasedata.User;
import it.polito.group2.restaurantowner.owner.MainActivity;
import it.polito.group2.restaurantowner.user.my_orders.MyOrdersActivity;
import it.polito.group2.restaurantowner.user.my_reviews.MyReviewsActivity;
import it.polito.group2.restaurantowner.user.restaurant_list.UserRestaurantList;

import android.provider.MediaStore;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserMyFavourites extends AppCompatActivity{

    private String user_id;
    private ListView listView;
    private ArrayList<String> favourites = new ArrayList<String>();
    private ArrayList<Restaurant> favourite_restaurants = new ArrayList<Restaurant>();
    private Context context;
    public User current_user;
    public Drawable d;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_my_favourites);

        context = this;

        mProgressDialog = FirebaseUtil.initProgressDialog(this);
        FirebaseUtil.showProgressDialog(mProgressDialog);
        user_id = FirebaseUtil.getCurrentUserId();

        get_favourites_from_firebase();

        get_user_from_firebase();


    }

    public void get_favourites_from_firebase(){
        FirebaseUtil.showProgressDialog(mProgressDialog);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://have-break-9713d.firebaseio.com/users/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot usSnapshot: snapshot.getChildren()) {
                    User snap_user = usSnapshot.getValue(User.class);
                    String snap_user_id = snap_user.getUser_id();
                    if(snap_user_id.equals(user_id)) {
                        for (String s : snap_user.getFavourites_restaurants().keySet()){
                            favourites.add(s);
                        }
                        break;
                    }
                }

                if(favourites.isEmpty()){
                    Toast.makeText(getApplicationContext(), "No favourite restaurants", Toast.LENGTH_SHORT).show();
                }
                else {

                    if (favourites != null && !favourites.isEmpty()) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://have-break-9713d.firebaseio.com/restaurants/");
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                for (DataSnapshot resSnapshot : snapshot.getChildren()) {
                                    Restaurant snap_restaurant = resSnapshot.getValue(Restaurant.class);
                                    String snap_restaurant_id = snap_restaurant.getRestaurant_id();
                                    if (favourites.contains(snap_restaurant_id)) {
                                        favourite_restaurants.add(snap_restaurant);
                                    }
                                }

                                //list view implementation
                                listView = (ListView) findViewById(R.id.list);
                                FavouriteAdapter adapter = new FavouriteAdapter(context, R.layout.favourite_layout, favourite_restaurants);
                                listView.setAdapter(adapter);

                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view,
                                                            int position, long id) {
                                        // ListView Clicked item value
                                        Restaurant itemValue = (Restaurant) listView.getItemAtPosition(position);
                                        Intent intent3 = new Intent(
                                                getApplicationContext(),
                                                UserRestaurantActivity.class);
                                        Bundle b3 = new Bundle();
                                        b3.putString("user_id", user_id);
                                        b3.putString("restaurant_id", itemValue.getRestaurant_id());
                                        intent3.putExtras(b3);
                                        startActivity(intent3);
                                    }
                                });

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
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    private void get_user_from_firebase(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://have-break-9713d.firebaseio.com/users/"+ user_id);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User current_user = snapshot.getValue(User.class);

                //navigation drawer
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @SuppressWarnings("StatementWithEmptyBody")
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        // Handle navigation view item clicks here.
                        int id = item.getItemId();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (id == R.id.nav_owner) {
                            Intent intent1 = new Intent(
                                    getApplicationContext(),
                                    MainActivity.class);
                            Bundle b1 = new Bundle();
                            b1.putString("user_id", user_id);
                            intent1.putExtras(b1);
                            startActivity(intent1);
                            return true;
                        } else if (id == R.id.nav_home) {
                            Intent intent1 = new Intent(
                                    getApplicationContext(),
                                    UserRestaurantList.class);
                            Bundle b1 = new Bundle();
                            b1.putString("user_id", user_id);
                            intent1.putExtras(b1);
                            startActivity(intent1);
                            return true;
                        } else if (id == R.id.nav_login) {
                            Intent intent1 = new Intent(
                                    getApplicationContext(),
                                    UserRestaurantList.class);
                            startActivity(intent1);
                            return true;
                        } else if (id == R.id.nav_my_profile) {
                            Intent intent1 = new Intent(
                                    getApplicationContext(),
                                    UserProfile.class);
                            Bundle b1 = new Bundle();
                            b1.putString("user_id", user_id);
                            intent1.putExtras(b1);
                            startActivity(intent1);
                            return true;
                        } else if (id == R.id.nav_my_orders) {
                            Intent intent1 = new Intent(
                                    getApplicationContext(),
                                    MyOrdersActivity.class);
                            Bundle b1 = new Bundle();
                            b1.putString("user_id", user_id);
                            intent1.putExtras(b1);
                            startActivity(intent1);
                            return true;
                        } else if (id == R.id.nav_my_reservations) {
                            Intent intent3 = new Intent(
                                    getApplicationContext(),
                                    UserMyReservations.class);
                            Bundle b3 = new Bundle();
                            b3.putString("user_id", user_id);
                            intent3.putExtras(b3);
                            startActivity(intent3);
                            return true;
                        } else if (id == R.id.nav_my_reviews) {
                            Intent intent3 = new Intent(
                                    getApplicationContext(),
                                    MyReviewsActivity.class);
                            Bundle b3 = new Bundle();
                            b3.putString("user_id", user_id);
                            intent3.putExtras(b3);
                            startActivity(intent3);
                            return true;
                        } else if (id == R.id.nav_my_favourites) {
                            Intent intent3 = new Intent(
                                    getApplicationContext(),
                                    UserMyFavourites.class);
                            Bundle b3 = new Bundle();
                            b3.putString("user_id", user_id);
                            intent3.putExtras(b3);
                            startActivity(intent3);
                            return true;
                        }

                        drawer.closeDrawer(GravityCompat.START);
                        return true;
                    }
                });
                TextView nav_username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderUsername);
                TextView nav_email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderEmail);
                ImageView nav_photo = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView);
                if (current_user != null) {
                    if (current_user.getUser_full_name() != null && current_user.getUser_full_name() == null)
                        nav_username.setText(current_user.getUser_full_name());
                    if (current_user.getUser_email() != null)
                        nav_email.setText(current_user.getUser_email());
                    if (current_user.getUser_photo_firebase_URL() != null)
                        Glide.with(context)
                                .load(current_user.getUser_photo_firebase_URL()) //"http://nuuneoi.com/uploads/source/playstore/cover.jpg"
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.blank_profile)
                                .into(nav_photo);
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        d = null;
        System.gc();
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}