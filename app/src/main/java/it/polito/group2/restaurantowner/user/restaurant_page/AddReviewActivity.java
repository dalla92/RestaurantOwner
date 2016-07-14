package it.polito.group2.restaurantowner.user.restaurant_page;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;
import it.polito.group2.restaurantowner.R;

public class AddReviewActivity extends AppCompatActivity {

    private String reviewID;
    private EditText comment;
    private RatingBar stars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        comment = (EditText) findViewById(R.id.edit_review_comment);
        stars = (RatingBar) findViewById(R.id.user_review_rating_bar);

        if(getIntent().getExtras()!=null && getIntent().getExtras().getString("comment") != null){
            float floatStars = getIntent().getExtras().getFloat("stars", 0f);
            String commentText = getIntent().getExtras().getString("comment");
            reviewID = getIntent().getExtras().getString("reviewID");

            comment.setText(commentText);
            stars.setRating(floatStars);
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
                alertDialog.setTitle(getResources().getString(R.string.warning));
                alertDialog.setMessage(getResources().getString(R.string.error_review));
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
