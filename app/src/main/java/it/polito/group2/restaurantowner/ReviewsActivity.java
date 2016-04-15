package it.polito.group2.restaurantowner;

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
import android.view.ViewTreeObserver;

import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.UUID;

public class ReviewsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    //private boolean card_view_clicked=false;
    private RecyclerView rv;
    //private RecyclerView mRecyclerView;
    private RecyclerView.Adapter rvAdapter;
    private RecyclerView.LayoutManager rvLayoutManager;

    private String restaurantID = "0"; //restaurant reference
    private ArrayList<Comment> commentList = new ArrayList<Comment>();

    private Menu menu;
    private boolean card_view_clicked=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_reviews);

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

        Comment comment;
        for(int i=0;i<10;i++) {
            comment = new Comment();
            comment.setDate("1" + i + "/03/2016 1" + i + ":30");
            comment.setComment("Commento dell'utente");
            comment.setCommentID(UUID.randomUUID().toString());
            comment.setStars_number(4);
            comment.setUsername("Utente " + i);
            comment.setRestaurantId("");
            commentList.add(comment);
        }

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
        for(i=0; i<commentList.size(); i++){
            if(comment_start.equals(commentList.get(i).comment.substring(0, 7)))
                break;
        }
        if(!card_view_clicked) {
            card_view_clicked=true;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, 300, 1f);
            comment.setMaxLines(Integer.MAX_VALUE);
            comment.setText(commentList.get(i).comment);
            comment.setLayoutParams(lp);
        }
        else {
            card_view_clicked=false;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, v.getLayoutParams().height, 1f);
            String comment_ell = commentList.get(i).comment.substring(0, 7);
            comment_ell = comment_ell + getResources().getString(R.string.show_more);
            comment.setText(comment_ell);
            comment.setMaxLines(2);
            comment.setLayoutParams(lp);
        }
        v.requestLayout();
    }

}
