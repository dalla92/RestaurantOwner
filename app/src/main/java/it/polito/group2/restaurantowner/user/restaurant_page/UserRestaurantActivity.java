package it.polito.group2.restaurantowner.user.restaurant_page;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.owner.JSONUtil;
import it.polito.group2.restaurantowner.owner.Restaurant;
import it.polito.group2.restaurantowner.owner.offer.Offer;

public class UserRestaurantActivity extends AppCompatActivity {

    private String userID, restaurantID = "id1";
    private ArrayList<Review> reviews;
    private ArrayList<Offer> offers;
    private Restaurant targetRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_restaurant);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final CollapsingToolbarLayout collapsing = (CollapsingToolbarLayout) findViewById(R.id.collapsing_user_restaurant);

        Firebase firebase = new Firebase("https://have-break.firebaseio.com/restaurants");
        /*Firebase restaurantRef = firebase.child(restaurantID);
        Restaurant r = new Restaurant("Da Pino", restaurantID, "2", "dunnio", "Via Vittorio Emanuele 14", "01105487980", "Kebab",
                                        true, true, true, "50", "10", "300", "Marconi" , "Caserma Morelli", "4.5", "10", "50%");
        restaurantRef.setValue(r);*/
        //Query queryRef = firebase.orderByChild("restaurantId");

        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    targetRestaurant = child.getValue(Restaurant.class);
                    Log.d("prova", targetRestaurant.toString());
                    collapsing.setTitle(targetRestaurant.getName());
                    setRestaurantInfo();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("test", "failed read " + firebaseError.getMessage());
            }
        });


        collapsing.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));

        ImageView image = (ImageView) findViewById(R.id.user_restaurant_image);
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int primaryDark = ContextCompat.getColor(UserRestaurantActivity.this, R.color.colorPrimaryDark);
                int primary = ContextCompat.getColor(UserRestaurantActivity.this, R.color.colorPrimary);

                collapsing.setContentScrimColor(palette.getMutedColor(primary));
                collapsing.setStatusBarScrimColor(palette.getDarkVibrantColor(primaryDark));
            }
        });

        reviews = getReviewsJson();
        offers = getOffersJSON();

        setBookmarkButton();
        addInfoExpandAnimation();
        addTimesExpandAnimation();
        setTimesList();
        setCallAction();
        setUserReviews();
        setRestaurantOffers();
    }

    private void setRestaurantInfo() {
        TextView name = (TextView) findViewById(R.id.restaurant_name);
        TextView address = (TextView) findViewById(R.id.restaurant_address);
        TextView phone = (TextView) findViewById(R.id.restaurant_phone);
        TextView text_rating = (TextView) findViewById(R.id.restaurant_stars_text);
        RatingBar rating = (RatingBar) findViewById(R.id.restaurant_stars);

        name.setText(targetRestaurant.getName());
        address.setText(targetRestaurant.getAddress());
        phone.setText(targetRestaurant.getPhoneNum());
        text_rating.setText(targetRestaurant.getRating());
        rating.setRating(Float.valueOf(targetRestaurant.getRating()));
    }

    private void setRestaurantOffers() {
        RecyclerView offerList = (RecyclerView) findViewById(R.id.user_offer_list);
        assert offerList != null;
        offerList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        offerList.setNestedScrollingEnabled(false);
        OfferAdapter adapter = new OfferAdapter(offers, this);
        offerList.setAdapter(adapter);
    }

    private void setUserReviews() {
        TextView reviews_num = (TextView) findViewById(R.id.user_restaurant_num_reviews);
        reviews_num.setText(""+reviews.size());
        RecyclerView reviewList = (RecyclerView) findViewById(R.id.user_review_list);
        assert reviewList != null;
        reviewList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        reviewList.setNestedScrollingEnabled(false);
        ReviewAdapter adapter = new ReviewAdapter(reviews, this);
        reviewList.setAdapter(adapter);
    }


    private void addTimesExpandAnimation() {
        final TextView timesText = (TextView) findViewById(R.id.restaurant_today_time);
        assert timesText != null;
        timesText.setOnClickListener(new View.OnClickListener() {
            boolean clicked = false;

            @Override
            public void onClick(View v) {

                final RecyclerView timesList = (RecyclerView) findViewById(R.id.user_time_list);
                timesList.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                int targetHeight = timesList.getMeasuredHeight();
                ValueAnimator slideAnimator;
                if(!clicked) {
                    timesText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_time_18dp, 0, R.drawable.ic_arrow_up_18dp, 0);
                    slideAnimator = ValueAnimator.ofInt(0, targetHeight).setDuration(300);
                    clicked = true;
                }
                else{
                    timesText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_time_18dp, 0, R.drawable.ic_arrow_down_18dp, 0);
                    slideAnimator = ValueAnimator.ofInt(targetHeight, 0).setDuration(300);
                    clicked = false;
                }

                slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        // get the value the interpolator is at
                        Integer value = (Integer) animation.getAnimatedValue();
                        // I'm going to set the layout's height 1:1 to the tick
                        timesList.getLayoutParams().height = value.intValue();
                        // force all layouts to see which ones are affected by
                        // this layouts height change
                        timesList.requestLayout();
                    }
                });
                AnimatorSet set = new AnimatorSet();
                set.play(slideAnimator);
                set.setInterpolator(new AccelerateDecelerateInterpolator());
                set.start();
            }

        });
    }

    private void setCallAction() {
        final TextView restaurantPhone = (TextView) findViewById(R.id.restaurant_phone);
        assert restaurantPhone != null;
        restaurantPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int colorStart = v.getSolidColor();
                int colorEnd = R.color.colorPrimary;

                ValueAnimator colorAnimator = ObjectAnimator.ofInt(v, "backgroundColor", colorStart, colorEnd);
                colorAnimator.setDuration(200);
                colorAnimator.setEvaluator(new ArgbEvaluator());
                colorAnimator.setRepeatCount(1);
                colorAnimator.setRepeatMode(ValueAnimator.REVERSE);
                colorAnimator.start();

                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + restaurantPhone.getText()));
                startActivity(callIntent);
            }
        });
    }

    private void setTimesList() {
        RecyclerView timesList = (RecyclerView) findViewById(R.id.user_time_list);
        assert timesList != null;
        timesList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        timesList.setNestedScrollingEnabled(false);
        timesList.setAdapter(new RecyclerView.Adapter<TimesViewHolder>() {
            String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

            @Override
            public TimesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
               View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.time_item, parent, false);
                return new TimesViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(TimesViewHolder holder, int position) {
                holder.time_day.setText(days[position]);
                holder.time_lunch.setText("10:00AM-14:30AM");
                holder.time_dinner.setText("19:00-23:30");
            }

            @Override
            public int getItemCount() {
                return days.length;
            }
        });
    }

    private class TimesViewHolder extends RecyclerView.ViewHolder {
        public TextView time_day, time_lunch, time_dinner;

        public TimesViewHolder(View view) {
            super(view);
            time_day = (TextView) view.findViewById(R.id.time_day);
            time_lunch = (TextView) view.findViewById(R.id.time_lunch);
            time_dinner = (TextView) view.findViewById(R.id.time_dinner);
        }
    }

    private void addInfoExpandAnimation() {
        LinearLayout fixedLayout = (LinearLayout) findViewById(R.id.fixed_layout);
        final LinearLayout layoutToExpand = (LinearLayout) findViewById(R.id.layout_info_expand);
        assert fixedLayout != null;
        fixedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentHeight, newHeight;

                final ImageView iconExpand = (ImageView) findViewById(R.id.icon_expand);
                assert iconExpand != null;
                assert layoutToExpand != null;
                layoutToExpand.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                int targetHeight = layoutToExpand.getMeasuredHeight();
                if (iconExpand.getVisibility() == View.VISIBLE) {
                    currentHeight = 0;
                    newHeight = targetHeight;
                } else {
                    currentHeight = targetHeight;
                    newHeight = 0;
                }

                ValueAnimator slideAnimator = ValueAnimator.ofInt(currentHeight, newHeight).setDuration(300);
                slideAnimator.addListener(new Animator.AnimatorListener() {
                    boolean modified = false;

                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (iconExpand.getVisibility() == View.VISIBLE) {
                            iconExpand.setVisibility(View.INVISIBLE);
                            modified = true;
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (iconExpand.getVisibility() == View.INVISIBLE && !modified) {
                            iconExpand.setVisibility(View.VISIBLE);
                            modified = false;
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        // get the value the interpolator is at
                        Integer value = (Integer) animation.getAnimatedValue();
                        // I'm going to set the layout's height 1:1 to the tick
                        layoutToExpand.getLayoutParams().height = value.intValue();
                        // force all layouts to see which ones are affected by
                        // this layouts height change
                        layoutToExpand.requestLayout();
                    }
                });
                AnimatorSet set = new AnimatorSet();
                set.play(slideAnimator);
                set.setInterpolator(new AccelerateDecelerateInterpolator());
                set.start();
            }
        });
    }


    private void setBookmarkButton() {
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.bookmark_fab);
        if (fab != null) {
            fab.setBackgroundTintList(ColorStateList.valueOf( ContextCompat.getColor(this, android.R.color.white)));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!isBookmark()) {
                        fab.setImageDrawable(ContextCompat.getDrawable(UserRestaurantActivity.this, R.drawable.ic_star_on_24dp));
                    }
                    else{
                        fab.setImageDrawable(ContextCompat.getDrawable(UserRestaurantActivity.this,R.drawable.ic_star_off_24dp));
                    }
                }
            });
        }
    }

    public void cycleTextViewExpansion(View v){
        TextView tv = (TextView) findViewById(R.id.user_review_comment);
        assert tv != null;
        //int collapsedMaxLines = 2;
        tv.setMaxLines(5);
        /*ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines",
                tv.getMaxLines() == collapsedMaxLines? tv.getLineCount() : collapsedMaxLines);
        animation.setDuration(200).start();*/
    }

    private ArrayList<Review> getReviewsJson() {
        ArrayList<Review> reviews = new ArrayList<>();
        String c1 = "Davvero un bel locale, personale accogliente e mangiare davvero sopra la media. I prezzi sono accessibile e data la qualità del cibo sono più che giusti.";
        Review r1 = new Review(restaurantID, "Paola Caruso", Calendar.getInstance(), c1, UUID.randomUUID().toString(), null, 4.5f);
        String c2 = "Think of Recyclerview not as a ListView 1:1 replacement but rather as a more flexible component for complex use cases. And as you say, your solution is what google expected of you.";
        Review r2 = new Review(restaurantID, "Paola Caruso", Calendar.getInstance(), c2, UUID.randomUUID().toString(), null, 4.5f);
        String c3 = "n adapter who can delegate onClick to an interface passed on the constructor, which is the correct pattern for both ListView and Recyclerview.";
        Review r3 = new Review(restaurantID, "Paola Caruso", Calendar.getInstance(), c3, UUID.randomUUID().toString(), null, 4.5f);
        String c4 = "Now look into that last piece of code: onCreateViewHolder(ViewGroup parent, int viewType) the signature already suggest different view types.";
        Review r4 = new Review(restaurantID, "Paola Caruso", Calendar.getInstance(), c4, UUID.randomUUID().toString(), null, 4.5f);
        reviews.add(r1);
        reviews.add(r2);
        reviews.add(r3);
        reviews.add(r4);
        return reviews;
    }

    private ArrayList<Offer> getOffersJSON(){
        try {
            return JSONUtil.readJSONOfferList(this, restaurantID);
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private boolean isBookmark() {
        try {
            JSONUtil.isBookmark(this, userID, restaurantID);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

}
