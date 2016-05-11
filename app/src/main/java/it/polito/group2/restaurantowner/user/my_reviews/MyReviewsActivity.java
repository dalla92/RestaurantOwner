package it.polito.group2.restaurantowner.user.my_reviews;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.Review;
import it.polito.group2.restaurantowner.owner.SimpleItemTouchHelperCallback;
import it.polito.group2.restaurantowner.user.my_reviews.MyReviewAdapter;

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
        if(bundle != null)
            userID = bundle.getString("userID") == null? null : bundle.getString("userID");

        myReviews = getReviewsJson();

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

    private ArrayList<Review> getReviewsJson() {
        /*try {
            return JSONUtil.readJSONReviewList(this, restaurantID);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        ArrayList<Review> reviews = new ArrayList<>();

        String c1 = "Davvero un bel locale, personale accogliente e mangiare davvero sopra la media. I prezzi sono accessibile e data la qualità del cibo sono più che giusti.";
        Calendar date1 = Calendar.getInstance();
        date1.set(Calendar.HOUR_OF_DAY, 12);
        Review r1 = new Review("1", "Paola C.", date1, c1, UUID.randomUUID().toString(), null, 4.5f);

        String c2 = "Think of Recyclerview not as a ListView 1:1 replacement but rather as a more flexible component for complex use cases. And as you say, your solution is what google expected of you.";
        Calendar date2 = Calendar.getInstance();
        date2.set(Calendar.HOUR_OF_DAY, 10);
        Review r2 = new Review("1", "Mario R.", date2, c2, UUID.randomUUID().toString(), null, 3f);


        Calendar date3 = Calendar.getInstance();
        date3.set(Calendar.HOUR_OF_DAY, 8);
        Review r3 = new Review("1", "Antonio V.", date3, "", UUID.randomUUID().toString(), null, 4f);

        String c4 = "Now look into that last piece of code: onCreateViewHolder(ViewGroup parent, int viewType) the signature already suggest different view types.";
        Calendar date4 = Calendar.getInstance();
        date4.set(Calendar.HOUR_OF_DAY, 12);
        date4.set(Calendar.MINUTE, 30);
        Review r4 = new Review("1", "Paola F.", date4, c4, UUID.randomUUID().toString(), null, 2.5f);

        reviews.add(r1);
        reviews.add(r2);
        reviews.add(r3);
        reviews.add(r4);
        return reviews;
    }

}
