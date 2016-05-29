package it.polito.group2.restaurantowner.owner;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;

import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.JSONUtil;
import it.polito.group2.restaurantowner.data.Restaurant;
import it.polito.group2.restaurantowner.firebasedata.Review;
import it.polito.group2.restaurantowner.gallery.GalleryViewActivity;
import it.polito.group2.restaurantowner.owner.my_offers.MyOffersActivity;
import it.polito.group2.restaurantowner.user.restaurant_page.UserRestaurantActivity;

public class ReviewsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    //private boolean card_view_clicked=false;
    private RecyclerView rv;
    //private RecyclerView mRecyclerView;
    private RecyclerView.Adapter rvAdapter;
    private RecyclerView.LayoutManager rvLayoutManager;

    private String restaurantID = "0"; //restaurant reference
    private ArrayList<Review> reviewList = new ArrayList<Review>();

    private Menu menu;
    private boolean card_view_clicked=false;

    public ArrayList<Restaurant> all_restaurants = new ArrayList<Restaurant>();
    public Restaurant current_restaurant;
    public Context context;
    public int MODIFY_INFO = 4;

    public ArrayList<Review> commentList = new ArrayList<Review>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        Bundle b = getIntent().getExtras();
        if(b != null) {
            restaurantID = b.getString("restaurantID");
        } else {
            //error on restaurant id
        }

        context = this;
        try {
            all_restaurants = JSONUtil.readJSONResList(context);
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        for(Restaurant r : all_restaurants){
            if(r.getRestaurantId().equals(restaurantID)){
                current_restaurant = r;
                break;
            }
        }

        populateCommentList(restaurantID);

        //navigation drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        */

        //cardview implementation
        rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        initializeAdapterReviews();

        /*final AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
        ScrollView scroll = (ScrollView) findViewById(R.id.parent_scroll);
        scroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                appbar.setExpanded(false);
            }
        });*/

    }

    public void populateCommentList(String restaurantID){

        /*
        try {
            commentList = JSONUtil.readJsonReviewList(this.getApplicationContext(), restaurantID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */


    }


    private void initializeAdapterReviews(){
        Adapter_Reviews adapter = new Adapter_Reviews(commentList, this.getApplicationContext());
        rv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reviews, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(id==R.id.action_user_part) {
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    UserRestaurantActivity.class);
            startActivity(intent1);
            return true;
        }
        else if(id==R.id.action_my_restaurants){
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    MainActivity.class);
            startActivity(intent1);
            return true;
        } else if(id==R.id.action_gallery) {
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    GalleryViewActivity.class);
            Bundle b = new Bundle();
            b.putString("restaurant_id", restaurantID);
            intent1.putExtras(b);
            startActivity(intent1);
            return true;
        } else if(id==R.id.action_menu) {
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    MenuRestaurant_page.class);
            Bundle b = new Bundle();
            b.putString("restaurant_id", restaurantID);
            intent1.putExtras(b);
            startActivity(intent1);
            return true;
        } else if(id==R.id.action_offers) {
            Intent intent2 = new Intent(
                    getApplicationContext(),
                    MyOffersActivity.class);
            Bundle b2 = new Bundle();
            b2.putString("restaurant_id", restaurantID);
            intent2.putExtras(b2);
            startActivity(intent2);
            return true;
        } else if(id==R.id.action_reservations){
            Intent intent3 = new Intent(
                    getApplicationContext(),
                    ReservationActivity.class);
            Bundle b3 = new Bundle();
            b3.putString("restaurant_id", restaurantID);
            intent3.putExtras(b3);
            startActivity(intent3);
            return true;
        } else if(id==R.id.action_reviews){
            Intent intent4 = new Intent(
                    getApplicationContext(),
                    ReviewsActivity.class); //here Filippo must insert his class name
            Bundle b4 = new Bundle();
            b4.putString("restaurant_id", restaurantID);
            intent4.putExtras(b4);
            startActivity(intent4);
            return true;
        } else if(id==R.id.action_statistics){
            Intent intent5 = new Intent(
                    getApplicationContext(),
                    StatisticsActivity.class); //here Filippo must insert his class name
            Bundle b5 = new Bundle();
            b5.putString("restaurant_id", restaurantID);
            intent5.putExtras(b5);
            startActivity(intent5);
            return true;
        } else if(id==R.id.action_edit){
            Intent intent6 = new Intent(
                    getApplicationContext(),
                    AddRestaurantActivity.class);
            intent6.putExtra("Restaurant", current_restaurant);
            final AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
            appbar.setExpanded(false);
            startActivityForResult(intent6, MODIFY_INFO);
            return true;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void myClickHandler_expand_comment(View v){
        final View default_view = v;
        TextView comment = (TextView) v.findViewById(R.id.comment);
        final int original_comment_height=comment.getMeasuredHeight();
        int i;
        String comment_start = comment.getText().toString().substring(0, 7);
        for(i=0; i< reviewList.size(); i++){
            if(comment_start.equals(reviewList.get(i).getReview_comment().substring(0, 7)))
                break;
        }
        if(!card_view_clicked) {
            card_view_clicked=true;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, 300, 1f);
            comment.setMaxLines(Integer.MAX_VALUE);
            comment.setText(reviewList.get(i).getReview_comment());
            comment.setLayoutParams(lp);
        }
        else {
            card_view_clicked=false;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, v.getLayoutParams().height, 1f);
            String comment_ell = reviewList.get(i).getReview_comment().substring(0, 7);
            comment_ell = comment_ell + getResources().getString(R.string.show_more);
            comment.setText(comment_ell);
            comment.setMaxLines(2);
            comment.setLayoutParams(lp);
        }
        v.requestLayout();
    }

}
