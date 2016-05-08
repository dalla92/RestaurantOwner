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
import android.support.v7.widget.CardView;
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
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.UUID;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.MenuCategory;
import it.polito.group2.restaurantowner.data.Review;
import it.polito.group2.restaurantowner.data.JSONUtil;
import it.polito.group2.restaurantowner.data.Restaurant;
import it.polito.group2.restaurantowner.data.Offer;

public class UserRestaurantActivity extends AppCompatActivity {

    private static final int ADD_REQUEST = 1;
    private String userID, restaurantID;
    private ArrayList<Review> reviews;
    private ArrayList<Offer> offers;
    private ArrayList<MenuCategory> categories;
    private Restaurant targetRestaurant;
    private ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_restaurant);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final CollapsingToolbarLayout collapsing = (CollapsingToolbarLayout) findViewById(R.id.collapsing_user_restaurant);

        /*Firebase firebase = new Firebase("https://have-break.firebaseio.com/restaurants");
        Firebase restaurantRef = firebase.push();
        Restaurant r = new Restaurant("Da Pino", restaurantRef.getKey(), "2", "dunnio", "Via Vittorio Emanuele 14", "01105487980", "Kebab",
                                        true, true, true, "50", "10", "300", "Marconi" , "Caserma Morelli", "4.5", "10", "50%");
        restaurantRef.setValue(r);
        //Query queryRef = firebase.orderByChild("restaurantId");

        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    targetRestaurant = child.getValue(Restaurant.class);
                    //Log.d("prova", targetRestaurant.toString());
                    collapsing.setTitle(targetRestaurant.getName());
                    setRestaurantInfo();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("test", "failed read " + firebaseError.getMessage());
            }
        });*/


        //getRestaurantAndSetButtons();


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
        Collections.sort(reviews);
        offers = getOffersJSON();
        categories = getCategoriesJson();

        setBookmarkButton();
        addInfoExpandAnimation();
        addTimesExpandAnimation();
        setTimesList();
        setCallAction();
        setUserReviews();
        setRestaurantOffers();
        setRestaurantMenu();
    }

    private void setRestaurantMenu() {
        final RecyclerView menu = (RecyclerView) findViewById(R.id.user_restaurant_menu);
        assert menu != null;
        menu.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        menu.setNestedScrollingEnabled(false);
        MenuAdapter adapter = new MenuAdapter(categories, this);
        menu.setAdapter(adapter);

        menu.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = menu.getMeasuredHeight();

        if(targetHeight > 600){
            menu.getLayoutParams().height = 600;
            menu.requestLayout();
            final ImageView iconExpand = (ImageView) findViewById(R.id.user_restaurant_menu_icon_expand);
            assert iconExpand != null;
            iconExpand.setVisibility(View.VISIBLE);

            CardView menuLayout = (CardView) findViewById(R.id.user_restaurant_menu_layout);
            assert menuLayout != null;
            menuLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("prova", "clicked");
                    int currentHeight, newHeight;

                    if (iconExpand.getVisibility() == View.VISIBLE) {
                        currentHeight = 600;
                        newHeight = targetHeight;
                    } else {
                        currentHeight = targetHeight;
                        newHeight = 600;
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
                            menu.getLayoutParams().height = value.intValue();
                            // force all layouts to see which ones are affected by
                            // this layouts height change
                            menu.requestLayout();
                        }
                    });
                    AnimatorSet set = new AnimatorSet();
                    set.play(slideAnimator);
                    set.setInterpolator(new AccelerateDecelerateInterpolator());
                    set.start();
                }
            });

        }
    }

    public void addReview(View v){
        Intent intent = new Intent(getApplicationContext(), AddReviewActivity.class);
        startActivityForResult(intent, ADD_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_REQUEST) {
            if (resultCode == RESULT_OK) {
                String comment = data.getStringExtra("comment");
                float starNumber = data.getFloatExtra("starsNumber", 0.0f);

                Calendar date = Calendar.getInstance();
                Review review =new Review(restaurantID, "Mario Rossi", date, comment, UUID.randomUUID().toString(), null, starNumber);
                reviews.add(review);
                Collections.sort(reviews);
                reviewAdapter.notifyDataSetChanged();
                //TODO save data
            }
        }
    }

    private void getRestaurantAndSetButtons() {
        ArrayList<Restaurant> resList = new ArrayList<>();
        //read all restaurants
        try {
            resList = JSONUtil.readJSONResList(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //if application is re-installed, it will be empty so I need to add one
        if(resList.isEmpty()) {
            Restaurant res = new Restaurant();
            res.setRestaurantId(UUID.randomUUID().toString());
            resList.add(res);
            //in order to read, I need to write first
            try {
                JSONUtil.saveJSONResList(this,resList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        targetRestaurant = resList.get(0); //I overwrite the actual restaurantID
        userID = "0";

        /*Button orders_button = (Button) findViewById(R.id.orders_button);
        orders_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        UserTableReservationActivity.class);
                intent.putExtra("Restaurant", current_restaurant);
                startActivity(intent);
            }
        });

        Button reservations_button = (Button) findViewById(R.id.reservations_button);
        reservations_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        UserMyReservations.class);
                intent.putExtra("Username", username);
                startActivity(intent);
            }
        });*/
    }

    /*private void setRestaurantInfo() {
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
    }*/

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
        reviewAdapter = new ReviewAdapter(reviews, this);
        reviewList.setAdapter(reviewAdapter);
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
        /*try {
            return JSONUtil.readJSONReviewList(this, restaurantID);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        ArrayList<Review> reviews = new ArrayList<>();

        String c1 = "Davvero un bel locale, personale accogliente e mangiare davvero sopra la media. I prezzi sono accessibile e data la qualità del cibo sono più che giusti.";
        Calendar date1 = Calendar.getInstance();
        date1.set(Calendar.HOUR_OF_DAY, 12);
        Review r1 = new Review(restaurantID, "Paola C.", date1, c1, UUID.randomUUID().toString(), null, 4.5f);

        String c2 = "Think of Recyclerview not as a ListView 1:1 replacement but rather as a more flexible component for complex use cases. And as you say, your solution is what google expected of you.";
        Calendar date2 = Calendar.getInstance();
        date2.set(Calendar.HOUR_OF_DAY, 10);
        Review r2 = new Review(restaurantID, "Mario R.", date2, c2, UUID.randomUUID().toString(), null, 3f);


        Calendar date3 = Calendar.getInstance();
        date3.set(Calendar.HOUR_OF_DAY, 8);
        Review r3 = new Review(restaurantID, "Antonio V.", date3, "", UUID.randomUUID().toString(), null, 4f);

        String c4 = "Now look into that last piece of code: onCreateViewHolder(ViewGroup parent, int viewType) the signature already suggest different view types.";
        Calendar date4 = Calendar.getInstance();
        date4.set(Calendar.HOUR_OF_DAY, 12);
        date4.set(Calendar.MINUTE, 30);
        Review r4 = new Review(restaurantID, "Paola F.", date4, c4, UUID.randomUUID().toString(), null, 2.5f);

        reviews.add(r1);
        reviews.add(r2);
        reviews.add(r3);
        reviews.add(r4);
        return reviews;
    }

    private ArrayList<MenuCategory> getCategoriesJson() {
        ArrayList<MenuCategory> categories = new ArrayList<>();
        MenuCategory c1 = new MenuCategory("ciao", "Primi Piatti", restaurantID);
        MenuCategory c2 = new MenuCategory("ciao", "Secondi Piatti", restaurantID);
        MenuCategory c3 = new MenuCategory("ciao", "Contorni", restaurantID);
        MenuCategory c4 = new MenuCategory("ciao", "Dessert", restaurantID);
        categories.add(c1);
        categories.add(c2);
        categories.add(c3);
        categories.add(c4);

        return categories;
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
