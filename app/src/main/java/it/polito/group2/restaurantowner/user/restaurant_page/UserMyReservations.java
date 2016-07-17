package it.polito.group2.restaurantowner.user.restaurant_page;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.DrawerUtil;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.Utils.OnBackUtil;
import it.polito.group2.restaurantowner.Utils.RemoveListenerUtil;
import it.polito.group2.restaurantowner.firebasedata.TableReservation;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
import it.polito.group2.restaurantowner.firebasedata.User;
import it.polito.group2.restaurantowner.login.LoginManagerActivity;
import it.polito.group2.restaurantowner.owner.MainActivity;
import it.polito.group2.restaurantowner.owner.SimpleItemTouchHelperCallback;
import it.polito.group2.restaurantowner.user.my_favourites.UserMyFavourites;
import it.polito.group2.restaurantowner.user.my_orders.MyOrdersActivity;
import it.polito.group2.restaurantowner.user.my_reviews.MyReviewsActivity;
import it.polito.group2.restaurantowner.user.restaurant_list.UserRestaurantList;

/**
 * Created by Alessio on 27/04/2016.
 */
public class UserMyReservations extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    ArrayList<TableReservation> all_table_reservations = new ArrayList<TableReservation>();
    private My_Reservations_Adapter adapter;
    List<Restaurant> resList = new ArrayList<Restaurant>();
    private String restaurant_number;
    private String user_id;
    private ArrayList<User> users = new ArrayList<User>();
    private Context context;
    public User current_user;
    public Drawable d;
    private RecyclerView mRecyclerView;
    private int j=0;
    private ProgressDialog mProgressDialog;
    private FirebaseDatabase firebase;
    private Query q;
    private ValueEventListener l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_my_reservations);
        initToolBar();
        context = this;

        firebase = FirebaseDatabase.getInstance();
        mProgressDialog = FirebaseUtil.initProgressDialog(this);
        FirebaseUtil.showProgressDialog(mProgressDialog);
        user_id = FirebaseUtil.getCurrentUserId();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        //get and fill related data
        get_data_from_firebase();

        setDrawer();

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
                    ImageView nav_picture = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderPicture);
                    User target = dataSnapshot.getValue(User.class);

                    TextView nav_points = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderPoints);
                    nav_points.setText(target.getUser_fidelity_points() + " " + getString(R.string.points));

                    if (target.getOwnerUser())
                        ownerItem.setVisible(true);

                    nav_username.setText(target.getUser_full_name());
                    nav_email.setText(target.getUser_email());

                    String photoUri = target.getUser_photo_firebase_URL();
                    if (!isFinishing()) {
                        if (photoUri == null || photoUri.equals("")) {
                            Glide
                                    .with(UserMyReservations.this)
                                    .load(R.drawable.blank_profile_nav)
                                    .centerCrop()
                                    .into(nav_picture);
                        } else {
                            Glide
                                    .with(UserMyReservations.this)
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

    public void get_data_from_firebase(){
        q = firebase.getReferenceFromUrl("https://have-break-9713d.firebaseio.com/table_reservations/").orderByChild("user_id").equalTo(user_id);
        l = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot revSnapshot : snapshot.getChildren()) {
                    TableReservation snap_table_reservations = revSnapshot.getValue(TableReservation.class);
                    String snap_user_id = snap_table_reservations.getUser_id();
                    if (snap_user_id.equals(user_id)) {
                        for (TableReservation r_temp : all_table_reservations) {
                            if (r_temp.getTable_reservation_id().equals(snap_table_reservations.getTable_reservation_id())) {
                                all_table_reservations.remove(r_temp);
                                break;
                            }
                        }
                        all_table_reservations.add(snap_table_reservations);
                        if (adapter != null)
                            adapter.notifyDataSetChanged();
                    }
                }

                if (all_table_reservations.isEmpty()) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), R.string.missing_reservation, Toast.LENGTH_SHORT).show();
                }
                else {
                    //ordering from last done
                    Collections.reverse(all_table_reservations);

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    adapter = new My_Reservations_Adapter(context, all_table_reservations, mProgressDialog);
                    mRecyclerView.setAdapter(adapter);

                    //Searching the names of the restaurants
                    //DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://have-break-9713d.firebaseio.com/restaurants/");

                /*
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot resSnapshot : snapshot.getChildren()) {
                            Restaurant snap_restaurant = resSnapshot.getValue(Restaurant.class);
                            String snap_restaurant_id = snap_restaurant.getRestaurant_id();
                            for(TableReservation tr : all_table_reservations) {
                                if (snap_restaurant_id.equals(tr.getRestaurant_id())) {
                                    restaurant_names_and_phone.put(snap_restaurant.getRestaurant_name(), snap_restaurant.getRestaurant_telephone_number());
                                    break;
                                }
                            }
                        }
                */
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        q.addValueEventListener(l);
    }
    @Override
    protected void onStop() {
        super.onStop();
        RemoveListenerUtil.remove_value_event_listener(q, l);
    }

    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.my_reservations);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

}
