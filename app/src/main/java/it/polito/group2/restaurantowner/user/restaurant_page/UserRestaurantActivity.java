package it.polito.group2.restaurantowner.user.restaurant_page;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.DrawerUtil;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.Utils.OnBackUtil;
import it.polito.group2.restaurantowner.Utils.RemoveListenerUtil;
import it.polito.group2.restaurantowner.firebasedata.Offer;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
import it.polito.group2.restaurantowner.firebasedata.RestaurantTimeSlot;
import it.polito.group2.restaurantowner.firebasedata.Review;
import it.polito.group2.restaurantowner.firebasedata.User;
import it.polito.group2.restaurantowner.gallery.GalleryViewActivity;
import it.polito.group2.restaurantowner.user.order.OrderActivity;

public class UserRestaurantActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final int ADD_REQUEST = 1;
    private String restaurantID;
    private ArrayList<Review> reviews;
    private ArrayList<it.polito.group2.restaurantowner.firebasedata.Offer> offers;
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
    private TextView timesText;
    private Query reviewsQuery;
    private ChildEventListener reviewsChildListener;
    private boolean coming_from_owner_restaurant_page = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_restaurant);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        meals = new ArrayList<>();
        reviews = new ArrayList<>();
        offers = new ArrayList<>();
        initializeUserReviewList();
        final CollapsingToolbarLayout collapsing = (CollapsingToolbarLayout) findViewById(R.id.collapsing_user_restaurant);
        collapsing.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
        coverPicture = (ImageView) findViewById(R.id.user_restaurant_image);
        fab = (FloatingActionButton) findViewById(R.id.bookmark_fab);

        firebase = FirebaseDatabase.getInstance();
        mProgressDialog = FirebaseUtil.initProgressDialog(this);
        FirebaseUtil.showProgressDialog(mProgressDialog);

        if(getIntent().getExtras()!=null)
            coming_from_owner_restaurant_page = getIntent().getExtras().getBoolean("coming_from_owner_restaurant_page");

        if(getIntent().getExtras()!=null && getIntent().getExtras().getString("restaurant_id")!=null)
            restaurantID = getIntent().getExtras().getString("restaurant_id");

        Query mealsQuery = firebase.getReference("meals/" + restaurantID);
        Query offersQuery = firebase.getReference("offers/" + restaurantID).orderByChild("offerEnabled").equalTo(true);
        reviewsQuery = firebase.getReference("reviews/" + restaurantID).orderByPriority();

        DatabaseReference restaurantRef = firebase.getReference("restaurants/" + restaurantID);
        restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                targetRestaurant = dataSnapshot.getValue(Restaurant.class);

                //restaurantID = targetRestaurant.getRestaurant_id();
                RatingBar ratingBar = (RatingBar) findViewById(R.id.restaurant_stars);

                ratingBar.setRating(targetRestaurant.getRestaurant_rating());
                collapsing.setTitle(targetRestaurant.getRestaurant_name());

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

                String photoURL = targetRestaurant.getRestaurant_photo_firebase_URL();
                if (photoURL == null || photoURL.equals("")) {
                    Glide
                            .with(UserRestaurantActivity.this)
                            .load(R.drawable.no_image)
                            .fitCenter()
                            .into(coverPicture);
                } else {
                    Glide
                            .with(UserRestaurantActivity.this)
                            .load(photoURL)
                            .fitCenter()
                            .into(coverPicture);
                }

                String userID = FirebaseUtil.getCurrentUserId();
                HashMap<String, Boolean> favouriteUsers = targetRestaurant.getFavourite_users();
                if (userID != null && favouriteUsers != null && favouriteUsers.containsKey(userID))
                    fab.setImageDrawable(ContextCompat.getDrawable(UserRestaurantActivity.this, R.drawable.ic_star_on_24dp));
                else
                    fab.setImageDrawable(ContextCompat.getDrawable(UserRestaurantActivity.this, R.drawable.ic_star_off_24dp));

                if(targetRestaurant.openNow())
                    timesText.setText(getResources().getString(R.string.open_now));
                else{
                    timesText.setText(getResources().getString(R.string.close_now));
                }

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

        reviewsChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("prova", "review added");
                reviewAdapter.addReview(dataSnapshot.getValue(Review.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("prova", "child changed");
                reviewAdapter.modifyReview(dataSnapshot.getValue(Review.class));
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
        };

        mealsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren())
                    meals.add(data.getValue(Meal.class));

                FirebaseUtil.hideProgressDialog(mProgressDialog);
                categories = getCategoryList();
                setRestaurantMenu();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("prova", "meals cancelled");
            }
        });

        offersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren())
                    offers.add(data.getValue(Offer.class));

                Log.d("prova", ""+offers.size());
                setRestaurantOffers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("offer", "meals cancelled");
            }
        });

        addBookmarkButtonClick();
        addInfoExpandAnimation();
        addTimesExpandAnimation();
        setCallAction();
        setDrawer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        reviewsQuery.addChildEventListener(reviewsChildListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
        RemoveListenerUtil.remove_child_event_listener(reviewsQuery, reviewsChildListener);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(!coming_from_owner_restaurant_page)
                OnBackUtil.clean_stack_and_go_to_user_restaurant_list(this);
            else
                OnBackUtil.clean_stack_and_go_to_restaurant_page(this, restaurantID);
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
                    TextView nav_points = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderPoints);
                    ImageView nav_picture = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderPicture);
                    User target = dataSnapshot.getValue(User.class);

                    if (target.getOwnerUser())
                        ownerItem.setVisible(true);

                    nav_username.setText(target.getUser_full_name());
                    nav_email.setText(target.getUser_email());
                    nav_points.setText(String.valueOf(target.getUser_fidelity_points()));

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
        return DrawerUtil.drawer_user_not_restaurant_page(this, item);
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
            servicesBuilder.append(getString(R.string.tv));
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
        if(targetRestaurant.getRestaurant_closest_bus() != null &&
            !targetRestaurant.getRestaurant_closest_bus().trim().equals("")) {
            extrasBuilder.append(getString(R.string.near));
            extrasBuilder.append(" ");
            extrasBuilder.append(targetRestaurant.getRestaurant_closest_bus());
            extrasBuilder.append(" ");
            extrasBuilder.append(getString(R.string.bus_stop));
            extrasBuilder.append(", ");
        }
        if(targetRestaurant.getRestaurant_closest_metro() !=null &&
            !targetRestaurant.getRestaurant_closest_metro().trim().equals("")){
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
            FirebaseUtil.showProgressDialog(mProgressDialog);
            Query reviewQuery = firebase.getReference("reviews/" + userID).orderByChild("restaurant_id").equalTo(restaurantID);
            reviewQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.hasChildren()){
                        Intent intent = new Intent(getApplicationContext(), AddReviewActivity.class);
                        startActivityForResult(intent, ADD_REQUEST);
                    }
                    else{
                        AlertDialog.Builder alert = new AlertDialog.Builder(UserRestaurantActivity.this);
                        alert.setTitle(getResources().getString(R.string.warning));
                        alert.setMessage(getResources().getString(R.string.already_review));

                        alert.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Review review = null;
                                for (DataSnapshot data : dataSnapshot.getChildren())
                                    review = data.getValue(Review.class);

                                Intent intent = new Intent(getApplicationContext(), AddReviewActivity.class);
                                intent.putExtra("comment", review.getReview_comment());
                                intent.putExtra("stars", review.getReview_rating());
                                intent.putExtra("reviewID", review.getReview_id());
                                startActivityForResult(intent, ADD_REQUEST);
                            }
                        });
                        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        alert.show();
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
                final String userID = FirebaseUtil.getCurrentUserId();
                if(userID == null){
                    Toast.makeText(UserRestaurantActivity.this, getResources().getString(R.string.error_review), Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseReference userRef = firebase.getReference("users/" + userID);
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        String comment = data.getStringExtra("comment") != null? data.getStringExtra("comment").trim() : null;
                        float starNumber = data.getFloatExtra("starsNumber", 0.0f);
                        String reviewID = data.getStringExtra("review");
                        Calendar date = Calendar.getInstance();

                        Review review = new Review();
                        review.setRestaurant_id(restaurantID);
                        review.setReview_comment(comment);
                        review.setReview_timestamp(date.getTimeInMillis());
                        review.setReview_rating(starNumber);
                        review.setUser_id(user.getUser_id());
                        review.setUser_full_name(user.getUser_full_name());
                        review.setUser_thumbnail(user.getUser_thumbnail());

                        DatabaseReference userReviewsRef, restaurantReviewsRef;
                        if(reviewID == null) {
                            userReviewsRef = firebase.getReference("reviews/" + userID).push();
                            restaurantReviewsRef = firebase.getReference("reviews/" + restaurantID + "/" + userReviewsRef.getKey());

                            review.setReview_id(userReviewsRef.getKey());
                            if(comment != null && !comment.equals("")) {
                                userReviewsRef.setPriority(-date.getTimeInMillis());
                                restaurantReviewsRef.setPriority(-date.getTimeInMillis());
                            }
                            else {
                                userReviewsRef.setPriority(Long.toString(date.getTimeInMillis()));
                                restaurantReviewsRef.setPriority(Long.toString(date.getTimeInMillis()));
                            }
                        }
                        else{
                            userReviewsRef = firebase.getReference("reviews/" + userID + "/" + reviewID);
                            restaurantReviewsRef = firebase.getReference("reviews/" + restaurantID + "/" + reviewID);
                            review.setReview_id(reviewID);
                        }

                        restaurantReviewsRef.setValue(review);
                        userReviewsRef.setValue(review)
                                .addOnCompleteListener(UserRestaurantActivity.this, new OnCompleteListener<Void>() {

                                    @Override
                                    public void onComplete(@NotNull Task<Void> task) {
                                        //update restaurant rating
                                        Query reviewsRef = firebase.getReference("reviews/" + restaurantID);
                                        reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot snapshot) {
                                                double total_reviews_rating = 0;
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                    Review r = dataSnapshot.getValue(Review.class);
                                                    total_reviews_rating += r.getReview_rating();
                                                }
                                                total_reviews_rating = total_reviews_rating/snapshot.getChildrenCount();
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
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

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

        String userID = FirebaseUtil.getCurrentUserId();

        if(theUserIsTheOwner || userID == null) {
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
                /*
                AlertDialog.Builder alert = new AlertDialog.Builder(UserRestaurantActivity.this);
                alert.setTitle("Warning");
                alert.setMessage("Questa funzionalità è in fase sviluppo, stay tuned!");

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();
*/
                if(targetRestaurant.openNow()) {
                    Intent intent = new Intent(UserRestaurantActivity.this, OrderActivity.class);
                    intent.putExtra("restaurant_id", targetRestaurant.getRestaurant_id());
                    startActivity(intent);
                }
                else{
                    Toast.makeText(UserRestaurantActivity.this, getResources().getString(R.string.cant_order), Toast.LENGTH_SHORT).show();
                }
            }
        });


        reservationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                AlertDialog.Builder alert = new AlertDialog.Builder(UserRestaurantActivity.this);
                alert.setTitle("Warning");
                alert.setMessage("Questa funzionalità è in fase sviluppo, stay tuned!");

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();
                */
                // TODO fix reservation
                Intent intent = new Intent(UserRestaurantActivity.this, UserTableReservationActivity.class);
                intent.putExtra("restaurant_id", targetRestaurant.getRestaurant_id());
                startActivity(intent);
            }
        });
    }

    private void setRestaurantInfo() {
        TextView name = (TextView) findViewById(R.id.restaurant_name);
        TextView address = (TextView) findViewById(R.id.restaurant_address);
        TextView phone = (TextView) findViewById(R.id.restaurant_phone);
        RatingBar rating = (RatingBar) findViewById(R.id.restaurant_stars);

        name.setText(targetRestaurant.getRestaurant_name());
        address.setText(targetRestaurant.getRestaurant_address());
        phone.setText(targetRestaurant.getRestaurant_telephone_number());
        rating.setRating(targetRestaurant.getRestaurant_rating());
    }

    public void openGallery(View v){
        Intent intent = new Intent(this, GalleryViewActivity.class);
        intent.putExtra("restaurant_id", restaurantID);
        intent.putExtra("coming_from_user", new Boolean(true));
        startActivity(intent);
    }

    private void setRestaurantOffers() {
        if(offers == null || offers.isEmpty()){
            CardView cardOffers = (CardView) findViewById(R.id.restaurant_offers);
            cardOffers.setVisibility(View.GONE);
        }
        else {
            final RecyclerView offerList = (RecyclerView) findViewById(R.id.user_offer_list);
            assert offerList != null;
            offerList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            offerList.setNestedScrollingEnabled(false);
            OfferAdapter adapter = new OfferAdapter(offers, this, meals);
            offerList.setAdapter(adapter);

            final TextView offerText = (TextView) findViewById(R.id.offer_text);
            assert offerText != null;
            offerText.setOnClickListener(new View.OnClickListener() {
                boolean clicked = false;

                @Override
                public void onClick(View v) {
                    offerList.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    int targetHeight = offerList.getMeasuredHeight();
                    ValueAnimator slideAnimator;
                    if (!clicked) {
                        offerText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up_24dp, 0);
                        slideAnimator = ValueAnimator.ofInt(0, targetHeight).setDuration(300);
                        clicked = true;
                    } else {
                        offerText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_24dp, 0);
                        slideAnimator = ValueAnimator.ofInt(targetHeight, 0).setDuration(300);
                        clicked = false;
                    }

                    slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            // get the value the interpolator is at
                            Integer value = (Integer) animation.getAnimatedValue();
                            // I'm going to set the layout's height 1:1 to the tick
                            offerList.getLayoutParams().height = value.intValue();
                            // force all layouts to see which ones are affected by
                            // this layouts height change
                            offerList.requestLayout();
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

    private void initializeUserReviewList() {
        /*TextView reviews_num = (TextView) findViewById(R.id.user_restaurant_num_reviews);
        reviews_num.setText(""+reviews.size());*/
        RecyclerView reviewList = (RecyclerView) findViewById(R.id.user_restaurant_review_list);
        assert reviewList != null;
        reviewList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        reviewList.setNestedScrollingEnabled(false);
        reviewAdapter = new ReviewAdapter(reviews, this);
        reviewList.setAdapter(reviewAdapter);
    }


    private void addTimesExpandAnimation() {
        timesText = (TextView) findViewById(R.id.restaurant_today_time);
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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userID == null) {
                    FirebaseUtil.showLoginDialog(UserRestaurantActivity.this);
                } else {
                    FirebaseUtil.showProgressDialog(mProgressDialog);
                    final DatabaseReference restaurantBookmarksRef = firebase.getReference("restaurants/" + restaurantID + "/favourite_users/" + userID);
                    final DatabaseReference userBookmarksRef = firebase.getReference("users/" + userID + "/favourites_restaurants/" + restaurantID);
                    restaurantBookmarksRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() == null) {
                                FirebaseMessaging.getInstance().subscribeToTopic(restaurantID);
                                Toast.makeText(UserRestaurantActivity.this, getString(R.string.bookmark_added), Toast.LENGTH_SHORT).show();
                                updateBookmark(restaurantBookmarksRef, userBookmarksRef, true);
                            } else {
                                FirebaseMessaging.getInstance().unsubscribeFromTopic(restaurantID);
                                Toast.makeText(UserRestaurantActivity.this, getString(R.string.bookmark_removed), Toast.LENGTH_SHORT).show();
                                updateBookmark(restaurantBookmarksRef, userBookmarksRef, false);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            FirebaseUtil.hideProgressDialog(mProgressDialog);
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
                        FirebaseUtil.hideProgressDialog(mProgressDialog);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        FirebaseUtil.hideProgressDialog(mProgressDialog);
                        Toast.makeText(UserRestaurantActivity.this, getResources().getString(R.string.technical_problem), Toast.LENGTH_SHORT).show();
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

}
