package it.polito.group2.restaurantowner.user.restaurant_page;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.firebasedata.Review;

public class AddReviewActivity extends AppCompatActivity {

    private String reviewID;
    private EditText comment;
    private RatingBar stars;
    private ProgressDialog mProgressDialog;
    private FirebaseDatabase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().getExtras()!=null && getIntent().getExtras().getString("review")!=null)
            reviewID = getIntent().getExtras().getString("review");

        comment = (EditText) findViewById(R.id.edit_review_comment);
        stars = (RatingBar) findViewById(R.id.user_review_rating_bar);
        mProgressDialog = FirebaseUtil.initProgressDialog(this);

        firebase = FirebaseDatabase.getInstance();
        FirebaseUtil.showProgressDialog(mProgressDialog);
        
        if(reviewID != null){
            Query reviewQuery = firebase.getReference("reviews").orderByChild("review_id").equalTo(reviewID);
            reviewQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Review review = null;
                    for(DataSnapshot data: dataSnapshot.getChildren())
                        review = data.getValue(Review.class);

                    if (review != null) {
                        comment.setText(review.getReview_comment());
                        stars.setRating(review.getReview_rating());
                        FirebaseUtil.hideProgressDialog(mProgressDialog);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    FirebaseUtil.hideProgressDialog(mProgressDialog);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_review, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_offer) {

            if(stars.getRating() == 0.0f){
                AlertDialog alertDialog = new AlertDialog.Builder(AddReviewActivity.this).create();
                alertDialog.setTitle("Warning");
                alertDialog.setMessage("You cannot add a review without rating it!");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

                return false;
            }

            Intent intent = new Intent();
            if(reviewID != null)
                intent.putExtra("review", reviewID);
            intent.putExtra("comment", comment.getText().toString());
            intent.putExtra("starsNumber", stars.getRating());
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }

        if(id == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
