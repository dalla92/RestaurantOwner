package it.polito.group2.restaurantowner.user.my_reviews;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.DrawerUtil;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.Utils.OnBackUtil;
import it.polito.group2.restaurantowner.Utils.OnLoadMoreListener;
import it.polito.group2.restaurantowner.firebasedata.Review;
import it.polito.group2.restaurantowner.firebasedata.User;
import it.polito.group2.restaurantowner.owner.SimpleItemTouchHelperCallback;
import it.polito.group2.restaurantowner.user.restaurant_list.UserRestaurantList;

public class MyReviewsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private String userID;
    private MyReviewAdapter mAdapter;
    private ArrayList<Review> reviews;
    protected Handler handler;
    private FirebaseDatabase firebase;
    private Toolbar toolbar;
    private String lastKnownKey;
    private boolean moreReviews;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reviews);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        handler = new Handler();

        userID = FirebaseUtil.getCurrentUserId();
        if(userID == null) {
            Toast.makeText(MyReviewsActivity.this, "Error!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, UserRestaurantList.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        moreReviews = false;
        reviews = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.user_review_list);
        assert mRecyclerView != null;
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapter = new MyReviewAdapter(reviews, this, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);

        firebase = FirebaseDatabase.getInstance();

        Query reviewsQuery = firebase.getReference("reviews/" + userID).orderByPriority().limitToFirst(10);
        reviewsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Review review = data.getValue(Review.class);
                    reviews.add(review);
                    mAdapter.notifyItemInserted(reviews.size());
                    lastKnownKey = data.getKey();
                }

                moreReviews = reviews.size() == 10;
                mAdapter.updateScrollListener(moreReviews);

                TextView tvEmptyView = (TextView) findViewById(R.id.empty_view);
                if (reviews.isEmpty()) {
                    mRecyclerView.setVisibility(View.GONE);
                    tvEmptyView.setVisibility(View.VISIBLE);

                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    tvEmptyView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("prova", "cancelled");
            }
        });

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Query reviewsQuery = firebase.getReference("reviews/" + userID).orderByPriority().startAt(lastKnownKey).limitToFirst(10);
                reviewsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //   remove progress item
                        mAdapter.removeNullItem();

                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Review review = data.getValue(Review.class);
                            reviews.add(review);
                            mAdapter.notifyItemInserted(reviews.size());
                            lastKnownKey = data.getKey();
                        }

                        moreReviews = (reviews.size() % 10) == 0;
                        mAdapter.setLoaded();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("prova", "cancelled");
                    }
                });

            }
        });

        setDrawer();
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
                TextView nav_points = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderPoints);
                ImageView nav_picture = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderPicture);
                User target = dataSnapshot.getValue(User.class);

                if (target.getOwnerUser())
                    ownerItem.setVisible(true);

                nav_username.setText(target.getUser_full_name());
                nav_email.setText(target.getUser_email());
                nav_points.setText(String.valueOf(target.getUser_fidelity_points()));

                String photoUri = target.getUser_photo_firebase_URL();
                if(photoUri == null || photoUri.equals("")) {
                    Glide
                            .with(MyReviewsActivity.this)
                            .load(R.drawable.blank_profile_nav)
                            .centerCrop()
                            .into(nav_picture);
                }
                else{
                    Glide
                            .with(MyReviewsActivity.this)
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
        return DrawerUtil.drawer_user_not_restaurant_page(this, item);
    }

}
