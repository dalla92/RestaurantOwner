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
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.Review;
import it.polito.group2.restaurantowner.owner.MainActivity;
import it.polito.group2.restaurantowner.owner.SimpleItemTouchHelperCallback;
import it.polito.group2.restaurantowner.user.my_orders.MyOrdersActivity;
import it.polito.group2.restaurantowner.user.restaurant_page.UserMyFavourites;
import it.polito.group2.restaurantowner.user.restaurant_page.UserMyReservations;
import it.polito.group2.restaurantowner.user.restaurant_page.UserProfile;
import it.polito.group2.restaurantowner.user.restaurant_list.UserRestaurantList;

public class MyReviewsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private String userID;
    private MyReviewAdapter mAdapter;
    private ArrayList<Review> reviews;
    private String user_id;

    protected Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reviews);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        handler = new Handler();

        /*Bundle bundle = getIntent().getExtras();
        if(bundle != null)
            userID = bundle.getString("userID") == null? null : bundle.getString("userID");*/

        reviews = getReviewsJson();

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.user_review_list);
        assert mRecyclerView != null;
        //mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapter = new MyReviewAdapter(reviews, this, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);

        TextView tvEmptyView = (TextView) findViewById(R.id.empty_view);
        if (reviews.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            tvEmptyView.setVisibility(View.VISIBLE);

        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);
        }

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //   remove progress item
                        mAdapter.removeNullItem();

                        //add items one by one
                        int start = reviews.size();
                        int end = start + 20;

                        //TODO download more data
                        for (int i = start + 1; i <= end; i++) {
                            String c1 = "Davvero un bel locale, personale accogliente e mangiare davvero sopra la media. I prezzi sono accessibile e data la qualità del cibo sono più che giusti.";
                            Calendar date1 = Calendar.getInstance();
                            date1.set(Calendar.HOUR_OF_DAY, 12);
                            Review r1 = new Review("1", "Paola C.", date1, c1, UUID.randomUUID().toString(), 4.5f);
                            reviews.add(r1);
                            mAdapter.notifyItemInserted(reviews.size());
                        }

                        mAdapter.setLoaded();
                        //or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
                    }
                }, 700);

            }
        });

        //navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

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

    private ArrayList<Review> getReviewsJson() {
        ArrayList<Review> reviews = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            String c1 = "Davvero un bel locale, personale accogliente e mangiare davvero sopra la media. I prezzi sono accessibile e data la qualità del cibo sono più che giusti.";
            Calendar date1 = Calendar.getInstance();
            date1.set(Calendar.HOUR_OF_DAY, 12);
            Review r1 = new Review("1", "Paola C.", date1, c1, UUID.randomUUID().toString(), 4.5f);

            String c2 = "Think of Recyclerview not as a ListView 1:1 replacement but rather as a more flexible component for complex use cases. And as you say, your solution is what google expected of you.";
            Calendar date2 = Calendar.getInstance();
            date2.set(Calendar.HOUR_OF_DAY, 10);
            Review r2 = new Review("1", "Mario R.", date2, c2, UUID.randomUUID().toString(), 3f);


            Calendar date3 = Calendar.getInstance();
            date3.set(Calendar.HOUR_OF_DAY, 8);
            Review r3 = new Review("1", "Antonio V.", date3, "", UUID.randomUUID().toString(), 4f);

            String c4 = "Now look into that last piece of code: onCreateViewHolder(ViewGroup parent, int viewType) the signature already suggest different view types.";
            Calendar date4 = Calendar.getInstance();
            date4.set(Calendar.HOUR_OF_DAY, 12);
            date4.set(Calendar.MINUTE, 30);
            Review r4 = new Review("1", "Paola F.", date4, c4, UUID.randomUUID().toString(), 2.5f);

            reviews.add(r1);
            reviews.add(r2);
            reviews.add(r3);
            reviews.add(r4);
        }

        return reviews;
    }

}
