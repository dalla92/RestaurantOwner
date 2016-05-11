package it.polito.group2.restaurantowner.owner;

import android.support.design.widget.NavigationView;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.Review;

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

        populateCommentList(restaurantID);

        //navigation drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

        Review review;
        for(int i=0;i<10;i++) {
            review = new Review();
            Calendar date = Calendar.getInstance();
            date.set(Calendar.HOUR_OF_DAY, i+1);
            review.setDate(date);
            review.setComment("Commento dell'utente");
            review.setReviewID(UUID.randomUUID().toString());
            review.setStars_number(4);
            review.setUserID("Utente " + i);
            review.setRestaurantId("");
            reviewList.add(review);
        }

    }


    private void initializeAdapterReviews(){
        Adapter_Reviews adapter = new Adapter_Reviews(reviewList, this.getApplicationContext());
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        /*
        int id = item.getItemId();
        if (id == R.id.nav_logout) {
            // TODO Handle the logout action
        } else if (id == R.id.nav_manage) {
            // TODO Handle the manage action
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        */
        return true;
    }

    public void myClickHandler_expand_comment(View v){
        final View default_view = v;
        TextView comment = (TextView) v.findViewById(R.id.comment);
        final int original_comment_height=comment.getMeasuredHeight();
        int i;
        String comment_start = comment.getText().toString().substring(0, 7);
        for(i=0; i< reviewList.size(); i++){
            if(comment_start.equals(reviewList.get(i).getComment().substring(0, 7)))
                break;
        }
        if(!card_view_clicked) {
            card_view_clicked=true;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, 300, 1f);
            comment.setMaxLines(Integer.MAX_VALUE);
            comment.setText(reviewList.get(i).getComment());
            comment.setLayoutParams(lp);
        }
        else {
            card_view_clicked=false;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, v.getLayoutParams().height, 1f);
            String comment_ell = reviewList.get(i).getComment().substring(0, 7);
            comment_ell = comment_ell + getResources().getString(R.string.show_more);
            comment.setText(comment_ell);
            comment.setMaxLines(2);
            comment.setLayoutParams(lp);
        }
        v.requestLayout();
    }

}
