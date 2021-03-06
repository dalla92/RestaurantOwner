package it.polito.group2.restaurantowner.owner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.DrawerUtil;
import it.polito.group2.restaurantowner.Utils.OnBackUtil;
import it.polito.group2.restaurantowner.Utils.RemoveListenerUtil;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
import it.polito.group2.restaurantowner.firebasedata.RestaurantPreview;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.firebasedata.User;
import it.polito.group2.restaurantowner.user.restaurant_list.UserRestaurantList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final static int ACTION_ADD = 1;
    private OwnerRestaurantPreviewAdapter mAdapter;
    ArrayList<RestaurantPreview> resList = new ArrayList<>();
    private FirebaseDatabase firebase;
    private ProgressDialog mProgressDialog;
    private Query resPreviewQuery;
    private ValueEventListener resPreviewValueListener;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String userID = FirebaseUtil.getCurrentUserId();
        if(userID == null){
            Toast.makeText(MainActivity.this, getResources().getString(R.string.need_login), Toast.LENGTH_SHORT).show();
            finish();
        }
        mProgressDialog = FirebaseUtil.initProgressDialog(this);
        FirebaseUtil.showProgressDialog(mProgressDialog);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        GridLayoutManager mLayoutManager = null;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager = new GridLayoutManager(this, 1);
            mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(1, 5, true));
        }
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLayoutManager = new GridLayoutManager(this, 2);
            mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 5, true));
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent mIntent = new Intent(MainActivity.this, Restaurant_page.class);
                        String id = resList.get(position).getRestaurant_id();
                        mIntent.putExtra("RestaurantId", id);
                        startActivity(mIntent);
                    }
                })
        );

        mAdapter = new OwnerRestaurantPreviewAdapter(resList,this);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        firebase = FirebaseDatabase.getInstance();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        setDrawer();
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        resPreviewQuery = firebase.getReference("restaurants_previews").orderByChild("user_id").equalTo(userID);
        resPreviewValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mAdapter.clear();
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    RestaurantPreview snap_res = data.getValue(RestaurantPreview.class);
                    mAdapter.addItem(snap_res);
                    resList.add(snap_res);
                }
                FirebaseUtil.hideProgressDialog(mProgressDialog);
                if(mAdapter.getItemCount() == 0)
                    Toast.makeText(MainActivity.this, "You don't own any restaurants", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseUtil.hideProgressDialog(mProgressDialog);
            }
        };
        /*resPreviewValueListener = new ValueEventListener(){

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                RestaurantPreview snap_res = dataSnapshot.getValue(RestaurantPreview.class);
                mAdapter.addItem(0, snap_res);
                FirebaseUtil.hideProgressDialog(mProgressDialog);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int res = mAdapter.findRestPrev(dataSnapshot.getValue(RestaurantPreview.class));
                if(res!=-1){
                    mAdapter.removeItem(res);
                    mAdapter.addItem(res, dataSnapshot.getValue(RestaurantPreview.class));
                    //        adapter.notifyItemChanged(res);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int res = mAdapter.findRestPrev(dataSnapshot.getValue(RestaurantPreview.class));
                if(res!=-1){
                    mAdapter.removeItem(res);
                    //        adapter.notifyItemRemoved(res);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        };*/

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUtil.showProgressDialog(mProgressDialog);
        resPreviewQuery.addValueEventListener(resPreviewValueListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        RemoveListenerUtil.remove_value_event_listener(resPreviewQuery, resPreviewValueListener);
        resList.clear();
        mAdapter.notifyDataSetChanged();
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

    private void setDrawer() {
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
                    if(photoUri == null || photoUri.equals("")) {
                        Glide
                                .with(getApplicationContext())
                                .load(R.drawable.blank_profile_nav)
                                .centerCrop()
                                .into(nav_picture);
                    }
                    else{
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

        navigationView.setNavigationItemSelectedListener(this);
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
                startActivity(intent);
                return true;



            default:
                // uf we got here, the user's action was not recognized.
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
            mAdapter = new OwnerRestaurantPreviewAdapter(resList,this);
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


            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return DrawerUtil.drawer_owner_main_activity(this, item);
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
