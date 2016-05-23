package it.polito.group2.restaurantowner.user.restaurant_page;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.JSONUtil;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.TableReservation;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
import it.polito.group2.restaurantowner.firebasedata.User;
import it.polito.group2.restaurantowner.owner.Adapter_Meals;
import it.polito.group2.restaurantowner.owner.MainActivity;
import it.polito.group2.restaurantowner.owner.MenuRestaurant_edit;
import it.polito.group2.restaurantowner.owner.RecyclerItemClickListener;
import it.polito.group2.restaurantowner.owner.SimpleItemTouchHelperCallback;
import it.polito.group2.restaurantowner.user.my_orders.MyOrdersActivity;
import it.polito.group2.restaurantowner.user.my_reviews.MyReviewsActivity;

/**
 * Created by Alessio on 27/04/2016.
 */
public class UserMyReservations extends AppCompatActivity{

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
    private ProgressDialog progressDialog;
    private HashMap<String, String> restaurant_names_and_phone = new HashMap<String, String>();
    private int j=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_my_reservations);
        initToolBar();
        context = this;

        //get the right restaurant
        Bundle b = getIntent().getExtras();
        //TODO Decomment after integrations
        /*
        if(b!=null)
            user_id = b.getString("user_id");
        */
        if(user_id==null)
            user_id = "fake_user_id";

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //get and fill related data
        get_data_from_firebase();

        //take the right user
        get_user_from_firebase();

    }

    private void progress_dialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Its loading....");
        progressDialog.setTitle("");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    public void get_data_from_firebase(){
        progress_dialog();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://have-break-9713d.firebaseio.com/table_reservations/");
        ref.addValueEventListener(new ValueEventListener() {
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
                        if(adapter!=null)
                            adapter.notifyDataSetChanged();
                    }
                }

                //ordering from last done
                Collections.reverse(all_table_reservations);

                //Searching the names of the restaurants
                //DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://have-break-9713d.firebaseio.com/restaurants/");
                for(TableReservation tr : all_table_reservations) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://have-break-9713d.firebaseio.com/restaurants/" + tr.getRestaurant_id());
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            Restaurant snap_restaurant = snapshot.getValue(Restaurant.class);
                            restaurant_names_and_phone.put(snap_restaurant.getRestaurant_name(), snap_restaurant.getRestaurant_telephone_number());

                            if (j == all_table_reservations.size() - 1) {
                                //recycler view
                                mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                                mRecyclerView.setHasFixedSize(true);
                                /*
                                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                    mLayoutManager = new GridLayoutManager(this, 1);
                                    mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(1,5,true));
                                }
                                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                    mLayoutManager = new GridLayoutManager(this, 2);
                                    mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2,5,true));
                                }
                                */

                                // use a linear layout manager
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                                mRecyclerView.setLayoutManager(mLayoutManager);
                                adapter = new My_Reservations_Adapter(context, all_table_reservations, progressDialog, restaurant_names_and_phone);
                                mRecyclerView.setAdapter(adapter);
                                //delete with swipe
                                ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
                                ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
                                mItemTouchHelper.attachToRecyclerView(mRecyclerView);

                                //progressDialog.dismiss(); done inside the adapter

                            }

                            j++;

                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                            System.out.println("The read failed: " + firebaseError.getMessage());
                        }
                    });
                }

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

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    private void get_user_from_firebase(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://have-break-9713d.firebaseio.com/users/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot usSnapshot: snapshot.getChildren()) {
                    User snap_user = usSnapshot.getValue(User.class);
                    String snap_user_id = snap_user.getUser_id();
                    if(user_id.equals(snap_user_id)){
                        current_user = snap_user;
                        break;
                    }
                }

                //TODO decomment handle logged/not logged user
                /*
                if(user_id==null){ //not logged
                    Menu nav_Menu = navigationView.getMenu();
                    nav_Menu.findItem(R.id.nav_my_profile).setVisible(false);
                    nav_Menu.findItem(R.id.nav_my_orders).setVisible(false);
                    nav_Menu.findItem(R.id.nav_my_reservations).setVisible(false);
                    nav_Menu.findItem(R.id.nav_my_reviews).setVisible(false);
                    nav_Menu.findItem(R.id.nav_my_favorites).setVisible(false);
                }
                else{ //logged
                    //if user is logged does not need to logout for any reason; he could authenticate with another user so Login is still maintained
                }
                */

                //navigation drawer
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
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
                            Bundle b1 = new Bundle();
                            b1.putString("user_id", user_id);
                            intent1.putExtras(b1);
                            startActivity(intent1);
                            return true;
                        }
                        else if(id==R.id.nav_home){
                            Intent intent1 = new Intent(
                                    getApplicationContext(),
                                    UserRestaurantList.class);
                            Bundle b1 = new Bundle();
                            b1.putString("user_id", user_id);
                            intent1.putExtras(b1);
                            startActivity(intent1);
                            return true;
                        }
                        else if(id==R.id.nav_login){
                            Intent intent1 = new Intent(
                                    getApplicationContext(),
                                    UserRestaurantList.class);
                            startActivity(intent1);
                            return true;
                        } else if(id==R.id.nav_my_profile) {
                            Intent intent1 = new Intent(
                                    getApplicationContext(),
                                    UserProfile.class);
                            Bundle b1 = new Bundle();
                            b1.putString("user_id", user_id);
                            intent1.putExtras(b1);
                            startActivity(intent1);
                            return true;
                        } else if(id==R.id.nav_my_orders) {
                            Intent intent1 = new Intent(
                                    getApplicationContext(),
                                    MyOrdersActivity.class);
                            Bundle b1 = new Bundle();
                            b1.putString("user_id", user_id);
                            intent1.putExtras(b1);
                            startActivity(intent1);
                            return true;
                        } else if(id==R.id.nav_my_reservations){
                            Intent intent3 = new Intent(
                                    getApplicationContext(),
                                    UserMyReservations.class);
                            Bundle b3 = new Bundle();
                            b3.putString("user_id", user_id);
                            intent3.putExtras(b3);
                            startActivity(intent3);
                            return true;
                        } else if(id==R.id.nav_my_reviews){
                            Intent intent3 = new Intent(
                                    getApplicationContext(),
                                    MyReviewsActivity.class);
                            Bundle b3 = new Bundle();
                            b3.putString("user_id", user_id);
                            intent3.putExtras(b3);
                            startActivity(intent3);
                            return true;
                        } else if(id==R.id.nav_my_favourites){
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
                if(current_user != null) {
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
                    //TODO load photo
                    /*
                    if (current_user.getUser_photo_firebase_URL() != null)
                        Glide.load(current_user.getUser_photo_firebase_URL()).into(nav_photo);

                    SharedPreferences userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
                    Uri photouri = null;
                    if (userDetails.getString("photouri", null) != null) {
                        photouri = Uri.parse(userDetails.getString("photouri", null));
                        File f = new File(getRealPathFromURI(photouri));
                        Drawable d = Drawable.createFromPath(f.getAbsolutePath());
                        navigationView.getHeaderView(0).setBackground(d);
                    } else
                        nav_photo.setImageResource(R.drawable.blank_profile);
                    */

            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
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
    protected void onDestroy() {
        super.onDestroy();
        d = null;
        System.gc();
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
            super.onBackPressed();
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
