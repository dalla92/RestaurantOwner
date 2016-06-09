package it.polito.group2.restaurantowner.owner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
import it.polito.group2.restaurantowner.firebasedata.RestaurantPreview;
import it.polito.group2.restaurantowner.firebasedata.User;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.user.restaurant_list.UserRestaurantList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final static int ACTION_ADD = 1;
    private RestaurantPreviewAdapter mAdapter;
    private  RecyclerView  mRecyclerView;
    ArrayList<Restaurant> resList = new ArrayList<>();
    private static final int VERTICAL_ITEM_SPACE = 5;
    private FirebaseDatabase firebase;
    private ProgressDialog mProgressDialog;

    private String userID;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userID = FirebaseUtil.getCurrentUserId();

        showProgressDialog();
        firebase = FirebaseDatabase.getInstance();
        Query restaurantReference = firebase.getReference("restaurants").orderByChild("user_id").equalTo(userID);
        DatabaseReference userReference = firebase.getReference("users/" + userID);
        hideProgressDialog();

        restaurantReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mAdapter.addItem(0, dataSnapshot.getValue(Restaurant.class));
                //resList.add(dataSnapshot.getValue(Restaurant.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Restaurant changedRes = dataSnapshot.getValue(Restaurant.class);
                for (Restaurant r : resList) {
                    if (r.getRestaurant_id().equals(changedRes.getRestaurant_id())) {
                        resList.remove(r);
                        resList.add(changedRes);
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Restaurant removedRes = dataSnapshot.getValue(Restaurant.class);
                for (Restaurant r : resList) {
                    if (r.getRestaurant_id().equals(removedRes.getRestaurant_id())) {
                        mAdapter.removeItem(resList.indexOf(r));
                        resList.remove(r);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

         mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        GridLayoutManager mLayoutManager = null;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager = new GridLayoutManager(this, 1);
            mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(1,5,true));
        }
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLayoutManager = new GridLayoutManager(this, 2);
            mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2,5,true));
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent mIntent = new Intent(MainActivity.this,Restaurant_page.class);
                        String id = resList.get(position).getRestaurant_id();
                        mIntent.putExtra("RestaurantId", id);
                        startActivity(mIntent);
                    }
                })
        );

        mAdapter = new RestaurantPreviewAdapter(resList,this);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {

            case R.id.action_add:
                Intent intent = new Intent(this,AddRestaurantActivity.class);
                startActivityForResult(intent, ACTION_ADD);
                return true;



            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
/*
    @Override
    public void onRestart() {
        super.onRestart();  // Always call the superclass method first

        try {
            resList = JSONUtil.readJSONResList(this);
            mAdapter = new RestaurantPreviewAdapter(resList,this);
            mRecyclerView.setAdapter(mAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == ACTION_ADD) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Restaurant res = (Restaurant) data.getExtras().get("Restaurant");
                if(userID!=null) {
                    res.setUser_id(userID);
                    //resList.add(0,res);
                    //mAdapter.addItem(0, res);
                    DatabaseReference restaurantReference = firebase.getReference("restaurants");
                    DatabaseReference item = restaurantReference.push();
                    res.setRestaurant_id(item.getKey());
                    item.setValue(res);

                    //save also the restaurant preview
                    DatabaseReference restaurantReference2 = firebase.getReference("restaurants_previews/" + res.getRestaurant_id());
                    RestaurantPreview r_p = new RestaurantPreview();
                    if (res.getRestaurant_latitude_position() != 0)
                        r_p.setLat(res.getRestaurant_latitude_position());
                    if (res.getRestaurant_latitude_position() != 0)
                        r_p.setLon(res.getRestaurant_latitude_position());
                    r_p.setRestaurant_id(res.getRestaurant_id());
                    r_p.setRestaurant_name(res.getRestaurant_name());
                    r_p.setRestaurant_price_range(1);
                    r_p.setRestaurant_rating(1);
                    r_p.setTables_number(res.getRestaurant_total_tables_number());
                    r_p.setRestaurant_category(res.getRestaurant_category());
                    restaurantReference2.setValue(r_p);
                }
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id==R.id.action_user_part) {
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    UserRestaurantList.class);
            startActivity(intent1);
            return true;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
