package it.polito.group2.restaurantowner.user.my_orders;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import it.polito.group2.restaurantowner.HaveBreak;
import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.firebasedata.Order;
import it.polito.group2.restaurantowner.firebasedata.User;
import it.polito.group2.restaurantowner.login.LoginManagerActivity;
import it.polito.group2.restaurantowner.owner.MainActivity;
import it.polito.group2.restaurantowner.user.my_reviews.MyReviewsActivity;
import it.polito.group2.restaurantowner.user.restaurant_page.UserMyFavourites;
import it.polito.group2.restaurantowner.user.restaurant_page.UserMyReservations;
import it.polito.group2.restaurantowner.user.restaurant_page.UserProfile;
import it.polito.group2.restaurantowner.user.restaurant_list.UserRestaurantList;


public class MyOrdersActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String userID;
    private User user;

    private FirebaseDatabase firebase;
    private ProgressDialog mProgressDialog;
    private ArrayList<Order> orderList;             //order list got from firebase
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_myorders_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO corregere quando viene passato un utente corretto
        userID = "-KITUg8848bUzejyV7oD";// = FirebaseUtil.getCurrentUserId();

        if(userID == null) {
            Log.d("FILIPPO", "utente non loggato o restaurantID non ricevuto");
            Intent intent = new Intent(this, HaveBreak.class);
            finish();
            startActivity(intent);
        }

        showProgressDialog();
        firebase = FirebaseDatabase.getInstance();
        orderList = new ArrayList<Order>();
        Query ordersReference = firebase.getReference("orders").orderByChild("user_id").equalTo(userID);
        DatabaseReference userReference = firebase.getReference("users/" + userID);
        hideProgressDialog();

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO gestire se l'utente viene cancellato
            }
        });

        ordersReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                orderList.add(dataSnapshot.getValue(Order.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Order changedOrder = dataSnapshot.getValue(Order.class);
                for (Order o : orderList) {
                    if (o.getOrder_id().equals(changedOrder.getOrder_id())) {
                        orderList.remove(o);
                        orderList.add(changedOrder);
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Order changedOrder = dataSnapshot.getValue(Order.class);
                for (Order o : orderList) {
                    if (o.getOrder_id().equals(changedOrder.getOrder_id())) {
                        orderList.remove(o);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //TODO capire quando si verifica
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO capire quando si verifica
            }
        });

        setOrderList();
        setDrawer();
    }

    private void setOrderList() {
        final RecyclerView list = (RecyclerView) findViewById(R.id.order_list);
        assert list != null;
        list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        list.setNestedScrollingEnabled(false);
        OrderAdapter adapter = new OrderAdapter(this, orderList);
        list.setAdapter(adapter);
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


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
                ImageView nav_picture = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderPicture);
                User target = dataSnapshot.getValue(User.class);

                if (target.getOwnerUser())
                    ownerItem.setVisible(true);

                nav_username.setText(target.getUser_full_name());
                nav_email.setText(target.getUser_email());

                String photoUri = target.getUser_photo_firebase_URL();
                if(photoUri == null || photoUri.equals("")) {
                    Glide
                            .with(MyOrdersActivity.this)
                            .load(R.drawable.blank_profile_nav)
                            .centerCrop()
                            .into(nav_picture);
                }
                else{
                    Glide
                            .with(MyOrdersActivity.this)
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(id==R.id.nav_owner){
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }
        else if(id==R.id.nav_home){
            Intent intent = new Intent(this, UserRestaurantList.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        } else if(id==R.id.nav_logout){
            Intent intent = new Intent(this, LoginManagerActivity.class);
            intent.putExtra("login", false);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else if(id==R.id.nav_my_profile) {
            Intent intent = new Intent(this, UserProfile.class);
            startActivity(intent);
            return true;
        } else if(id==R.id.nav_my_orders) {
            Intent intent = new Intent(this, MyOrdersActivity.class);
            startActivity(intent);
            return true;
        } else if(id==R.id.nav_my_reservations){
            Intent intent = new Intent(this, UserMyReservations.class);
            startActivity(intent);
            return true;
        } else if(id==R.id.nav_my_reviews){
            Intent intent = new Intent(this, MyReviewsActivity.class);
            startActivity(intent);
            return true;
        } else if(id==R.id.nav_my_favourites){
            Intent intent = new Intent(this, UserMyFavourites.class);
            startActivity(intent);
            return true;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
