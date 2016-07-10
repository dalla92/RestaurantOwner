package it.polito.group2.restaurantowner.user.my_orders;

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
import it.polito.group2.restaurantowner.Utils.DrawerUtil;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.Utils.OnBackUtil;
import it.polito.group2.restaurantowner.Utils.RemoveListenerUtil;
import it.polito.group2.restaurantowner.firebasedata.Order;
import it.polito.group2.restaurantowner.firebasedata.User;
import it.polito.group2.restaurantowner.user.restaurant_list.UserRestaurantList;


public class MyOrdersActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private ProgressDialog mProgressDialog;
    private ArrayList<Order> orderList = null;
    private DatabaseReference q;
    private ValueEventListener l;
    //private String userID;
    //private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_myorders_activity);

        //Toolbar setting
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //User object
        DatabaseReference userRef = FirebaseUtil.getCurrentUserRef();
        if (userRef != null) {
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    setDrawer(user);

                    //orders reference
                    Query ordersRef = FirebaseUtil.getOrdersByUserRef(user.getUser_id());
                    if (ordersRef != null) {
                        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ArrayList<Order> orders = new ArrayList<>();
                                for (DataSnapshot d : dataSnapshot.getChildren()) {
                                    orders.add(d.getValue(Order.class));
                                }
                                FirebaseUtil.hideProgressDialog(mProgressDialog);
                                orderList = orders;
                                setOrderList();
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
        } else {
            OnBackUtil.clean_stack_and_go_to_main_activity(this);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUtil.initProgressDialog(this);
        FirebaseUtil.showProgressDialog(mProgressDialog);
        if(q!=null)
            q.addValueEventListener(l);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(q!=null)
            RemoveListenerUtil.remove_value_event_listener(q, l);
    }

    @Override
    public void onResume(){
        super.onResume();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return DrawerUtil.drawer_user_not_restaurant_page(this, item);
    }

    //MODEL FUNCTIONS ==============================================================================

    private void setOrderList() {
        final RecyclerView list = (RecyclerView) findViewById(R.id.order_list);
        assert list != null;
        list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        list.setNestedScrollingEnabled(false);
        OrderAdapter adapter = new OrderAdapter(this, orderList);
        list.setAdapter(adapter);
    }

    //ACTIVITY FUNCTIONS ===========================================================================

    private void setDrawer(User user) {
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

        TextView nav_username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderUsername);
        TextView nav_email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderEmail);
        ImageView nav_picture = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView);

        if (user.getOwnerUser())
            ownerItem.setVisible(true);

        nav_username.setText(user.getUser_full_name());
        nav_email.setText(user.getUser_email());
        String photoUri = user.getUser_photo_firebase_URL();

        if(photoUri == null || photoUri.equals("")) {
            Glide
                    .with(MyOrdersActivity.this)
                    .load(R.drawable.blank_profile_nav)
                    .centerCrop()
                    .into(nav_picture);
        } else {
            Glide
                    .with(MyOrdersActivity.this)
                    .load(photoUri)
                    .centerCrop()
                    .into(nav_picture);
        }

        navigationView.setNavigationItemSelectedListener(this);
    }

}
