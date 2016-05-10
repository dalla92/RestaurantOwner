package it.polito.group2.restaurantowner.user;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.Review;
import it.polito.group2.restaurantowner.owner.SimpleItemTouchHelperCallback;
import it.polito.group2.restaurantowner.user.restaurant_page.ReviewAdapter;

public class MyReviewsActivity extends AppCompatActivity {

    private String userID;
    private MyReviewAdapter adapter;
    private ArrayList<Review> myReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reviews);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        userID = bundle.getString("userID") == null? null : bundle.getString("userID");

        RecyclerView reviewList = (RecyclerView) findViewById(R.id.user_review_list);
        assert reviewList != null;
        reviewList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        reviewList.setNestedScrollingEnabled(false);
        adapter = new MyReviewAdapter(myReviews, this);
        reviewList.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(reviewList);
    }

}
