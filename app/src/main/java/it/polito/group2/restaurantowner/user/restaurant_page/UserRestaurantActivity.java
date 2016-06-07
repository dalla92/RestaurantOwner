package it.polito.group2.restaurantowner.user.restaurant_page;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.data.JSONUtil;
import it.polito.group2.restaurantowner.data.Offer;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
import it.polito.group2.restaurantowner.firebasedata.RestaurantTimeSlot;
import it.polito.group2.restaurantowner.firebasedata.Review;
import it.polito.group2.restaurantowner.firebasedata.User;
import it.polito.group2.restaurantowner.gallery.GalleryViewActivity;
import it.polito.group2.restaurantowner.login.LoginManagerActivity;
import it.polito.group2.restaurantowner.owner.MainActivity;
import it.polito.group2.restaurantowner.user.my_orders.MyOrdersActivity;
import it.polito.group2.restaurantowner.user.my_reviews.MyReviewsActivity;
import it.polito.group2.restaurantowner.user.order.OrderActivity;
import it.polito.group2.restaurantowner.user.restaurant_list.UserRestaurantList;

public class UserRestaurantActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final int ADD_REQUEST = 1;
    private String restaurantID;
    private ArrayList<Review> reviews;
    private ArrayList<Offer> offers;
    private ArrayList<Meal> meals;
    private ArrayList<String> categories;
    private Restaurant targetRestaurant;
    private ReviewAdapter reviewAdapter;
    private ProgressDialog mProgressDialog;
    private FirebaseDatabase firebase;
    private Toolbar toolbar;
    private ImageView coverPicture;
    private FloatingActionButton fab;
    private boolean theUserIsTheOwner = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_restaurant);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        meals = new ArrayList<>();
        reviews = new ArrayList<>();
        initializeUserReviewList();
        final CollapsingToolbarLayout collapsing = (CollapsingToolbarLayout) findViewById(R.id.collapsing_user_restaurant);
        collapsing.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
        coverPicture = (ImageView) findViewById(R.id.user_restaurant_image);
        fab = (FloatingActionButton) findViewById(R.id.bookmark_fab);
        firebase = FirebaseDatabase.getInstance();

        showProgressDialog();

        //FirebaseAuth.getInstance().signOut();

        if(getIntent().getExtras()!=null && getIntent().getExtras().getString("restaurant_id")!=null)
            restaurantID = getIntent().getExtras().getString("restaurant_id");
        else
            restaurantID = "-KIMqPtRSEdm0Cvfc3Za";

        Query mealsReference = firebase.getReference("meals").orderByChild("restaurant_id").equalTo(restaurantID);
        Query reviewsRef = firebase.getReference("reviews").orderByChild("restaurant_id").equalTo(restaurantID);
        DatabaseReference restaurantRef = firebase.getReference("restaurants/" + restaurantID);

        restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                targetRestaurant = dataSnapshot.getValue(Restaurant.class);

                restaurantID = targetRestaurant.getRestaurant_id();
                TextView rating = (TextView) findViewById(R.id.restaurant_stars_text);
                RatingBar ratingBar = (RatingBar) findViewById(R.id.restaurant_stars);

                rating.setText(String.format("%.1f",targetRestaurant.getRestaurant_rating()));
                ratingBar.setRating(targetRestaurant.getRestaurant_rating());
                collapsing.setTitle(targetRestaurant.getRestaurant_name());

                String photoURL = targetRestaurant.getRestaurant_photo_firebase_URL();
                if (photoURL == null || photoURL.equals("")) {
                    Glide
                            .with(UserRestaurantActivity.this)
                            .load(R.drawable.no_image)
                            .fitCenter()
                            .listener(new RequestListener<Integer, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    hideProgressDialog();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    hideProgressDialog();
                                    return false;
                                }
                            })
                            .into(coverPicture);
                } else {
                    Glide
                            .with(UserRestaurantActivity.this)
                            .load(photoURL)
                            .fitCenter()
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    hideProgressDialog();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    hideProgressDialog();
                                    return false;
                                }
                            })
                            .into(coverPicture);
                }

                String userID = FirebaseUtil.getCurrentUserId();
                HashMap<String, Boolean> favouriteUsers = targetRestaurant.getFavourite_users();
                if (userID != null && favouriteUsers != null && favouriteUsers.containsKey(userID))
                    fab.setImageDrawable(ContextCompat.getDrawable(UserRestaurantActivity.this, R.drawable.ic_star_on_24dp));
                else
                    fab.setImageDrawable(ContextCompat.getDrawable(UserRestaurantActivity.this, R.drawable.ic_star_off_24dp));

                setButtons();
                setRestaurantInfo();
                setTimesList(targetRestaurant.getRestaurant_time_slot());
                setRestaurantExtraInfo();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("test", "failed read restaurant " + databaseError.getMessage());
            }
        });

        reviewsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("prova", "review added");
                reviewAdapter.addReview(dataSnapshot.getValue(Review.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("prova", "child changed");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("prova", "review removed");
                reviewAdapter.removeReview(dataSnapshot.getValue(Review.class));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d("prova", "child moved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("prova", "child cancelled");
            }
        });

        mealsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren())
                    meals.add(data.getValue(Meal.class));

                categories = getCategoryList();
                setRestaurantMenu();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("prova", "meals cancelled");
            }
        });

        if(targetRestaurant.getUser_id().equals(FirebaseUtil.getCurrentUserId())) {
            theUserIsTheOwner = true;
            ImageView addReview = (ImageView) findViewById(R.id.add_review);
            addReview.setVisibility(View.GONE);
        } else {
            theUserIsTheOwner = false;
        }

        ImageButton button_get_directions = (ImageButton) findViewById(R.id.button_get_directions);
        assert button_get_directions != null;
        button_get_directions.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?daddr=" + targetRestaurant.getRestaurant_latitude_position() + "," + targetRestaurant.getRestaurant_longitude_position()));
                        startActivity(intent);
                    }
                }
        );
        offers = getOffersJSON();

        addBookmarkButtonClick();
        addInfoExpandAnimation();
        addTimesExpandAnimation();
        setCallAction();
        setDrawer();

        setRestaurantOffers();
    }


    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(this, UserRestaurantList.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void setDrawer() {
        //navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        final MenuItem ownerItem = menu.findItem(R.id.nav_owner);
        MenuItem loginItem = menu.findItem(R.id.nav_login);
        MenuItem logoutItem = menu.findItem(R.id.nav_logout);
        MenuItem myProfileItem = menu.findItem(R.id.nav_my_profile);
        MenuItem myOrdersItem = menu.findItem(R.id.nav_my_orders);
        MenuItem mrResItem =  menu.findItem(R.id.nav_my_reservations);
        MenuItem myReviewsItem = menu.findItem(R.id.nav_my_reviews);
        MenuItem myFavItem = menu.findItem(R.id.nav_my_favourites);

        ownerItem.setVisible(false);
        String userID = FirebaseUtil.getCurrentUserId();
        if (userID != null) {
            loginItem.setVisible(false);
            logoutItem.setVisible(true);
            myProfileItem.setVisible(true);
            myOrdersItem.setVisible(true);
            mrResItem.setVisible(true);
            myReviewsItem.setVisible(true);
            myFavItem.setVisible(true);
            //navigationView.inflateHeaderView(R.layout.nav_header_login);

            DatabaseReference userRef = firebase.getReference("users/" + userID);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    TextView nav_username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderUsername);
                    TextView nav_email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderEmail);
                    ImageView nav_picture = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderPicture);
                    User target = dataSnapshot.getValue(User.class);

                    if (target.getOwnerUser())
                        ownerItem.setVisible(true);

                    nav_username.setText(target.getUser_full_name());
                    nav_email.setText(target.getUser_email());

                    String photoUri = target.getUser_photo_firebase_URL();
                    if(photoUri == null || photoUri.equals("")) {
                        Glide
                                .with(UserRestaurantActivity.this)
                                .load(R.drawable.blank_profile_nav)
                                .centerCrop()
                                .into(nav_picture);
                    }
                    else{
                        Glide
                                .with(UserRestaurantActivity.this)
                                .load(photoUri)
                                .centerCrop()
                                .into(nav_picture);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("prova", "cancelled");
                }
            });

        }
        else{
            loginItem.setVisible(true);
            logoutItem.setVisible(false);
            myProfileItem.setVisible(false);
            myOrdersItem.setVisible(false);
            mrResItem.setVisible(false);
            myReviewsItem.setVisible(false);
            myFavItem.setVisible(false);

        }

        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public void onResume(){
        super.onResume();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(id==R.id.nav_owner){
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }
        else if(id==R.id.nav_home){
            Intent intent = new Intent(this, UserRestaurantList.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        else if(id==R.id.nav_login){
            Intent intent = new Intent(this, LoginManagerActivity.class);
            intent.putExtra("login", true);
            startActivity(intent);
            return true;
        } else if(id==R.id.nav_logout){
            Intent intent = new Intent(this, LoginManagerActivity.class);
            intent.putExtra("login", false);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else if(id==R.id.nav_my_profile) {
            Intent intent = new Intent(this, UserProfile.class);
            startActivity(intent);
            return true;
        } else if(id==R.id.nav_my_orders) {
            Intent intent = new Intent(this, MyOrdersActivity.class);
            startActivity(intent);
            return true;
        } else if(id==R.id.nav_my_reservations){
            Intent intent = new Intent(this, UserMyReservations.class);
            startActivity(intent);
            return true;
        } else if(id==R.id.nav_my_reviews){
            Intent intent = new Intent(this, MyReviewsActivity.class);
            startActivity(intent);
            return true;
        } else if(id==R.id.nav_my_favourites){
            Intent intent = new Intent(this, UserMyFavourites.class);
            startActivity(intent);
            return true;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setRestaurantExtraInfo() {
        TextView category = (TextView) findViewById(R.id.restaurant_category);
        TextView extras = (TextView) findViewById(R.id.restaurant_extras);
        TextView services = (TextView) findViewById(R.id.restaurant_services);
        assert category != null;
        assert extras != null;
        assert services != null;

        StringBuilder servicesBuilder = new StringBuilder();
        if(targetRestaurant.getCreditCardAccepted()) {
            servicesBuilder.append(getString(R.string.credit_card));
            servicesBuilder.append(", ");
        }
        if(targetRestaurant.getTvPresent()) {
            servicesBuilder.append(getString(R.string.credit_card));
            servicesBuilder.append(", ");
        }
        if(targetRestaurant.getAnimalAllowed()){
            servicesBuilder.append(getString(R.string.animal_allowed));
            servicesBuilder.append(", ");
        }
        if(targetRestaurant.getWifiPresent()){
            servicesBuilder.append(getString(R.string.wifi));
            servicesBuilder.append(", ");
        }
        if(targetRestaurant.getAirConditionedPresent())
            servicesBuilder.append(getString(R.string.air_conditioned));

        StringBuilder extrasBuilder = new StringBuilder();
        if(targetRestaurant.getCeliacFriendly()) {
            extrasBuilder.append(getString(R.string.gluten_free));
            extrasBuilder.append(", ");
        }
        if(targetRestaurant.getRestaurant_closest_bus() != null) {
            extrasBuilder.append(getString(R.string.near));
            extrasBuilder.append(" ");
            extrasBuilder.append(targetRestaurant.getRestaurant_closest_bus());
            extrasBuilder.append(" ");
            extrasBuilder.append(getString(R.string.bus_stop));
            extrasBuilder.append(", ");
        }
        if(targetRestaurant.getRestaurant_closest_metro() !=null){
            extrasBuilder.append(getString(R.string.near));
            extrasBuilder.append(" ");
            extrasBuilder.append(targetRestaurant.getRestaurant_closest_metro());
            extrasBuilder.append(" ");
            extrasBuilder.append(getString(R.string.metro_stop));
        }

        category.setText(targetRestaurant.getRestaurant_category());
        services.setText(servicesBuilder.toString());
        extras.setText(extrasBuilder.toString());
    }

    private void setRestaurantMenu() {
        final RecyclerView menu = (RecyclerView) findViewById(R.id.user_restaurant_menu);
        assert menu != null;
        menu.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        menu.setNestedScrollingEnabled(false);
        MenuAdapter adapter = new MenuAdapter(categories, meals, this);
        menu.setAdapter(adapter);

        menu.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = menu.getMeasuredHeight();

        final ImageView iconExpand = (ImageView) findViewById(R.id.user_restaurant_menu_icon_expand);
        assert iconExpand != null;
        CardView menuLayout = (CardView) findViewById(R.id.user_restaurant_menu_layout);
        assert menuLayout != null;
        menuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentHeight, newHeight;

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
                            iconExpand.setVisibility(View.GONE);
                            modified = true;
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (iconExpand.getVisibility() == View.GONE && !modified) {
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

    private ArrayList<String> getCategoryList() {
        ArrayList<String> categoryList = new ArrayList<>();
        for(Meal m : meals) {
            if(categoryList.indexOf(m.getMeal_category()) == -1)
                categoryList.add(m.getMeal_category());
        }

        return categoryList;
    }

    public void addReview(View v){
        String userID = FirebaseUtil.getCurrentUserId();
        if (userID == null) {
            FirebaseUtil.showLoginDialog(this);
        }
        else {
            Query reviewQuery = firebase.getReference("reviews/" + restaurantID).orderByChild("user_id").equalTo(userID);
            reviewQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.hasChildren()){
                        Intent intent = new Intent(getApplicationContext(), AddReviewActivity.class);
                        startActivityForResult(intent, ADD_REQUEST);
                    }
                    else{

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        Log.d("result", "" + requestCode + " " + resultCode);
        if (requestCode == ADD_REQUEST) {
            if (resultCode == RESULT_OK) {
                User user = FirebaseUtil.getCurrentUser();
                if(user == null){
                    Toast.makeText(UserRestaurantActivity.this, "Error while adding the review, try again!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String comment = data.getStringExtra("comment");
                float starNumber = data.getFloatExtra("starsNumber", 0.0f);

                Calendar date = Calendar.getInstance();

                DatabaseReference reviewsRef = firebase.getReference("reviews/" + restaurantID).push();
                Review review =new Review();
                review.setRestaurant_id(restaurantID);
                review.setReview_comment(comment);
                review.setReview_timestamp(date.getTimeInMillis());
                review.setReview_id(reviewsRef.getKey());
                review.setReview_rating(starNumber);
                review.setUser_id(user.getUser_id());
                review.setUser_full_name(user.getUser_full_name());
                review.setUser_thumbnail(user.getUser_thumbnail());
                reviewsRef.setValue(review);

                //update restaurant rating
                DatabaseReference ref = firebase.getReference("reviews/" + restaurantID);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        double total_reviews_rating = 0;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Review r = dataSnapshot.getValue(Review.class);
                            total_reviews_rating += r.getReview_rating();
                        }
                        DatabaseReference ref2 = firebase.getReference("restaurants/" + restaurantID + "/restaurant_rating");
                        ref2.setValue(total_reviews_rating);
                        DatabaseReference ref3 = firebase.getReference("restaurants_previews/" + restaurantID + "/restaurant_rating");
                        ref3.setValue(total_reviews_rating);
                    }
                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                    }
                });
            }
        }
    }

    private void setButtons() {

        Button reservationsButton = (Button) findViewById(R.id.reservation_button);
        Button ordersButton = (Button) findViewById(R.id.order_button);
        assert ordersButton != null;
        assert reservationsButton != null;

        if(theUserIsTheOwner) {
            ordersButton.setVisibility(View.GONE);
            reservationsButton.setVisibility(View.GONE);
        } else {
            if (targetRestaurant != null) {
                if (targetRestaurant.getTableReservationAllowed())
                    reservationsButton.setVisibility(View.VISIBLE);
                else
                    reservationsButton.setVisibility(View.GONE);

                if (targetRestaurant.getTakeAwayAllowed())
                    ordersButton.setVisibility(View.VISIBLE);
                else
                    ordersButton.setVisibility(View.GONE);
            }
        }

        ordersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserRestaurantActivity.this, OrderActivity.class);
                intent.putExtra("restaurant_id", targetRestaurant.getRestaurant_id());
                startActivity(intent);
            }
        });


        reservationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserRestaurantActivity.this, UserTableReservationActivity.class);
                //intent.putExtra("Restaurant", targetRestaurant);
                startActivity(intent);
            }
        });
    }

    private void setRestaurantInfo() {
        TextView name = (TextView) findViewById(R.id.restaurant_name);
        TextView address = (TextView) findViewById(R.id.restaurant_address);
        TextView phone = (TextView) findViewById(R.id.restaurant_phone);
        TextView text_rating = (TextView) findViewById(R.id.restaurant_stars_text);
        RatingBar rating = (RatingBar) findViewById(R.id.restaurant_stars);

        name.setText(targetRestaurant.getRestaurant_name());
        address.setText(targetRestaurant.getRestaurant_address());
        phone.setText(targetRestaurant.getRestaurant_telephone_number());
        text_rating.setText(Float.toString(targetRestaurant.getRestaurant_rating()));
        rating.setRating(targetRestaurant.getRestaurant_rating());
    }

    public void openGallery(View v){
        Intent intent = new Intent(this, GalleryViewActivity.class);
        intent.putExtra("restaurantID", restaurantID);
        startActivity(intent);
    }

    private void setRestaurantOffers() {
        if(offers == null || offers.isEmpty()){
            CardView cardOffers = (CardView) findViewById(R.id.restaurant_offers);
            cardOffers.setVisibility(View.GONE);
        }
        else {
            RecyclerView offerList = (RecyclerView) findViewById(R.id.user_offer_list);
            assert offerList != null;
            offerList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            offerList.setNestedScrollingEnabled(false);
            OfferAdapter adapter = new OfferAdapter(offers, this);
            offerList.setAdapter(adapter);
        }
    }

    private void initializeUserReviewList() {
        TextView reviews_num = (TextView) findViewById(R.id.user_restaurant_num_reviews);
        reviews_num.setText(""+reviews.size());
        RecyclerView reviewList = (RecyclerView) findViewById(R.id.user_restaurant_review_list);
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
                if (!clicked) {
                    timesText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_time_18dp, 0, R.drawable.ic_arrow_up_18dp, 0);
                    slideAnimator = ValueAnimator.ofInt(0, targetHeight).setDuration(300);
                    clicked = true;
                } else {
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

    private void setTimesList(ArrayList<RestaurantTimeSlot> restaurant_time_slot) {
        RecyclerView timesList = (RecyclerView) findViewById(R.id.user_time_list);
        assert timesList != null;
        timesList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        timesList.setNestedScrollingEnabled(false);
        TimesAdapter adapter = new TimesAdapter(restaurant_time_slot, this);
        timesList.setAdapter(adapter);
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

    private void addBookmarkButtonClick() {
        final String userID = FirebaseUtil.getCurrentUserId();
        Log.d("prova", "" + userID);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userID == null) {
                    Log.d("prova", "null");
                    FirebaseUtil.showLoginDialog(UserRestaurantActivity.this);
                } else {
                    Log.d("prova", "not null");
                    showProgressDialog();
                    final DatabaseReference restaurantBookmarksRef = firebase.getReference("restaurants/" + restaurantID + "/favourite_users/" + userID);
                    final DatabaseReference userBookmarksRef = firebase.getReference("users/" + userID + "/favourites_restaurants/" + restaurantID);
                    restaurantBookmarksRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() == null) {
                                FirebaseMessaging.getInstance().subscribeToTopic(restaurantID);
                                updateBookmark(restaurantBookmarksRef, userBookmarksRef, true);
                            } else {
                                FirebaseMessaging.getInstance().unsubscribeFromTopic(restaurantID);
                                updateBookmark(restaurantBookmarksRef, userBookmarksRef, false);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            hideProgressDialog();
                            Log.d("prova", "bookmark cancelled");
                            fab.setImageDrawable(ContextCompat.getDrawable(UserRestaurantActivity.this, R.drawable.ic_star_off_24dp));
                        }
                    });
                }
            }
        });
    }

    private void updateBookmark(DatabaseReference restaurantBookmarksRef, final DatabaseReference userBookmarksRef, final boolean bookmark) {
        restaurantBookmarksRef.setValue(bookmark? true: null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (bookmark)
                            fab.setImageDrawable(ContextCompat.getDrawable(UserRestaurantActivity.this, R.drawable.ic_star_on_24dp));
                        else
                            fab.setImageDrawable(ContextCompat.getDrawable(UserRestaurantActivity.this, R.drawable.ic_star_off_24dp));

                        userBookmarksRef.setValue(bookmark ? true : null);
                        hideProgressDialog();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        hideProgressDialog();
                        Toast.makeText(UserRestaurantActivity.this, "Technical Problem, try again or restart the app!", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void cycleTextViewExpansion(View v){
        TextView tv = (TextView) findViewById(R.id.user_review_comment);
        assert tv != null;
        int collapsedMaxLines = 2;
        tv.setMaxLines(5);
        ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines",
                tv.getMaxLines() == collapsedMaxLines? tv.getLineCount() : collapsedMaxLines);
        animation.setDuration(200).start();
    }


    private ArrayList<Offer> getOffersJSON(){
        try {
            return JSONUtil.readJSONOfferList(this, restaurantID);
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}
