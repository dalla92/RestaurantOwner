package it.polito.group2.restaurantowner.user.restaurant_page;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.Bookmark;
import it.polito.group2.restaurantowner.data.MenuCategory;
import it.polito.group2.restaurantowner.firebasedata.Review;
import it.polito.group2.restaurantowner.data.JSONUtil;
import it.polito.group2.restaurantowner.data.Offer;
import it.polito.group2.restaurantowner.firebasedata.User;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
import it.polito.group2.restaurantowner.gallery.GalleryViewActivity;
import it.polito.group2.restaurantowner.login.FirebaseUtil;
import it.polito.group2.restaurantowner.login.LoginManagerActivity;
import it.polito.group2.restaurantowner.login.UserSessionManager;
import it.polito.group2.restaurantowner.owner.MainActivity;
import it.polito.group2.restaurantowner.user.my_orders.MyOrdersActivity;
import it.polito.group2.restaurantowner.user.my_reviews.MyReviewsActivity;
import it.polito.group2.restaurantowner.user.order.OrderActivity;
import it.polito.group2.restaurantowner.user.restaurant_list.UserRestaurantList;

public class UserRestaurantActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final int ADD_REQUEST = 1;
    private String userID, restaurantID;
    private ArrayList<Review> reviews;
    private ArrayList<Offer> offers;
    private ArrayList<MenuCategory> categories;
    private Restaurant targetRestaurant;
    private ReviewAdapter reviewAdapter;
    private Context context;
    private boolean bookmark = false;
    private ProgressDialog mProgressDialog;
    private FirebaseDatabase firebase;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_restaurant);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        //UserSessionManager sessionManager = new UserSessionManager(this);
        reviews = new ArrayList<>();
        initializeUserReviewList();
        final CollapsingToolbarLayout collapsing = (CollapsingToolbarLayout) findViewById(R.id.collapsing_user_restaurant);
        ImageView coverPicture = (ImageView) findViewById(R.id.user_restaurant_image);
        firebase = FirebaseDatabase.getInstance();
        showProgressDialog();

        //FirebaseAuth.getInstance().signOut();

        if(getIntent().getExtras()!=null && getIntent().getExtras().getString("restaurant_id")!=null) {
            restaurantID = getIntent().getExtras().getString("restaurant_id");
        }
        if(getIntent().getExtras()!=null && getIntent().getExtras().getString("user_id")!=null) {
            userID = getIntent().getExtras().getString("user_id");
        }

        collapsing.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
        DatabaseReference restaurantRef = firebase.getReference("restaurants/" + restaurantID);
        restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                targetRestaurant = dataSnapshot.getValue(Restaurant.class);

                restaurantID = targetRestaurant.getRestaurant_id();
                reviews = new ArrayList<>();
                collapsing.setTitle(targetRestaurant.getRestaurant_name());
                setRestaurantInfo();
                hideProgressDialog();

                Query reviewsRef = firebase.getReference("reviews").orderByChild("restaurant_id").equalTo(restaurantID);
                reviewsRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d("prova", "child added");
                        reviews.add(dataSnapshot.getValue(Review.class));
                        reviewAdapter.setReviewData(reviews);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Log.d("prova", "child changed");
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Log.d("prova", "child removed");
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("test", "failed read restaurant " + databaseError.getMessage());
            }
        });

        getRestaurantAndSetButtons();

        Glide
                .with(this)
                .load(R.drawable.image)
                .fitCenter()
                .into(coverPicture);

        /*if(targetRestaurant!=null) {
            String photoURL = targetRestaurant.getRestaurant_photo_firebase_URL();
            if (photoURL == null || photoURL.equals(""))
                Glide.with(this).load(R.drawable.no_image).into(coverPicture);
            else
                Glide.with(this).load(photoURL).into(coverPicture);
        }*/

        /*Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int primaryDark = ContextCompat.getColor(UserRestaurantActivity.this, R.color.colorPrimaryDark);
                int primary = ContextCompat.getColor(UserRestaurantActivity.this, R.color.colorPrimary);

                collapsing.setContentScrimColor(palette.getMutedColor(primary));
                collapsing.setStatusBarScrimColor(palette.getDarkVibrantColor(primaryDark));
            }
        });*/


        offers = getOffersJSON();
        categories = getCategoriesJson();

        setBookmarkButton();
        addInfoExpandAnimation();
        addTimesExpandAnimation();
        setTimesList();
        setCallAction();
        setRestaurantOffers();
        setRestaurantMenu();
        setDrawer();


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

    private void setDrawer() {
        //navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem loginItem = menu.findItem(R.id.nav_login);
        MenuItem logoutItem = menu.findItem(R.id.nav_logout);
        MenuItem myProfileItem = menu.findItem(R.id.nav_my_profile);
        MenuItem myOrdersItem = menu.findItem(R.id.nav_my_orders);
        MenuItem mrResItem =  menu.findItem(R.id.nav_my_reservations);
        MenuItem myReviewsItem = menu.findItem(R.id.nav_my_reviews);
        MenuItem myFavItem = menu.findItem(R.id.nav_my_favourites);

        String userID = FirebaseUtil.getCurrentUserId();
        if (userID != null) {
            loginItem.setVisible(false);
            logoutItem.setVisible(true);
            myProfileItem.setVisible(true);
            myOrdersItem.setVisible(true);
            mrResItem.setVisible(true);
            myReviewsItem.setVisible(true);
            myFavItem.setVisible(true);
            navigationView.inflateHeaderView(R.layout.nav_header_login);

            DatabaseReference userRef = firebase.getReference("users/" + userID);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    TextView nav_username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderUsername);
                    TextView nav_email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderEmail);
                    ImageView nav_picture = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderPicture);
                    User target = dataSnapshot.getValue(User.class);

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

        /*View navHeader = LayoutInflater.from(context).inflate(R.layout.nav_header_login, null);
        navigationView.addHeaderView(navHeader);*/

        /*final LinearLayout loginLayout = (LinearLayout) navigationView.getHeaderView(0).findViewById(R.id.header_login);
        final FrameLayout mainLayout = (FrameLayout) navigationView.getHeaderView(0).findViewById(R.id.header_main);


        //navigationView.setNavigationItemSelectedListener(this);
        //TODO decomment handle logged/not logged user
        /*
        if(userID==null){ //not logged
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_my_profile).setVisible(false);
            nav_Menu.findItem(R.id.nav_my_orders).setVisible(false);
            nav_Menu.findItem(R.id.nav_my_reservations).setVisible(false);
            nav_Menu.findItem(R.id.nav_my_reviews).setVisible(false);
            nav_Menu.findItem(R.id.nav_my_favorites).setVisible(false);
        }
        else{ //logged
            //if user is logged does not need to logout for any reason; he could authenticate with another user so Login is still maintained
        }
        */
        /*TextView nav_username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderUsername);
        TextView nav_email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderEmail);
        ImageView nav_photo = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView);
        if(current_user != null) {
            if (current_user.getFirst_name() != null && current_user.getLast_name() == null)
                nav_username.setText(current_user.getFirst_name());
            else if (current_user.getFirst_name() == null && current_user.getLast_name() != null)
                nav_username.setText(current_user.getLast_name());
            else if (current_user.getFirst_name() != null && current_user.getLast_name() != null)
                nav_username.setText(current_user.getFirst_name() + " " + current_user.getLast_name());
            if (current_user.getEmail() != null)
                nav_email.setText(current_user.getEmail());
            if (current_user.getPhoto() != null)
                nav_photo.setImageBitmap(current_user.getPhoto());
        }
        SharedPreferences userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
        Uri photouri = null;
        if(userDetails.getString("photouri", null)!=null) {
            photouri = Uri.parse(userDetails.getString("photouri", null));
            File f = new File(getRealPathFromURI(photouri));
            Drawable d = Drawable.createFromPath(f.getAbsolutePath());
            navigationView.getHeaderView(0).setBackground(d);
        }
        else
            nav_photo.setImageResource(R.drawable.blank_profile);*/
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(id==R.id.nav_owner){
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    MainActivity.class);
            Bundle b1 = new Bundle();
            b1.putString("user_id", userID);
            intent1.putExtras(b1);
            startActivity(intent1);
            return true;
        }
        else if(id==R.id.nav_home){
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    UserRestaurantList.class);
            Bundle b1 = new Bundle();
            b1.putString("user_id", userID);
            intent1.putExtras(b1);
            startActivity(intent1);
            return true;
        }
        else if(id==R.id.nav_login){
            Intent intent = new Intent(
                    getApplicationContext(),
                    LoginManagerActivity.class);
            intent.putExtra("login", true);
            startActivity(intent);
            return true;
        } else if(id==R.id.nav_logout){
            Intent intent = new Intent(
                    getApplicationContext(),
                    LoginManagerActivity.class);
            intent.putExtra("login", false);
            startActivity(intent);
        } else if(id==R.id.nav_my_profile) {
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    UserProfile.class);
            Bundle b1 = new Bundle();
            b1.putString("user_id", userID);
            intent1.putExtras(b1);
            startActivity(intent1);
            return true;
        } else if(id==R.id.nav_my_orders) {
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    MyOrdersActivity.class);
            Bundle b1 = new Bundle();
            b1.putString("user_id", userID);
            intent1.putExtras(b1);
            startActivity(intent1);
            return true;
        } else if(id==R.id.nav_my_reservations){
            Intent intent3 = new Intent(
                    getApplicationContext(),
                    UserMyReservations.class);
            Bundle b3 = new Bundle();
            b3.putString("user_id", userID);
            intent3.putExtras(b3);
            startActivity(intent3);
            return true;
        } else if(id==R.id.nav_my_reviews){
            Intent intent3 = new Intent(
                    getApplicationContext(),
                    MyReviewsActivity.class);
            Bundle b3 = new Bundle();
            b3.putString("user_id", userID);
            intent3.putExtras(b3);
            startActivity(intent3);
            return true;
        } else if(id==R.id.nav_my_favourites){
            Intent intent3 = new Intent(
                    getApplicationContext(),
                    UserMyFavourites.class);
            Bundle b3 = new Bundle();
            b3.putString("user_id", userID);
            intent3.putExtras(b3);
            startActivity(intent3);
            return true;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    public void addReview(View v){
        Intent intent = new Intent(getApplicationContext(), AddReviewActivity.class);
        startActivityForResult(intent, ADD_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("result", "" + requestCode + " " + resultCode);
        if (requestCode == ADD_REQUEST) {
            if (resultCode == RESULT_OK) {
                String comment = data.getStringExtra("comment");
                float starNumber = data.getFloatExtra("starsNumber", 0.0f);

                GregorianCalendar date = new GregorianCalendar();
                //"EEE dd MMM yyyy 'at' HH:mm

                DatabaseReference reviewsRef = firebase.getReference("reviews").push();
                Review review =new Review();
                review.setRestaurant_id(restaurantID);
                review.setReview_comment(comment);
                review.setReview_date(date);
                review.setReview_id(reviewsRef.getKey());
                review.setReview_rating(starNumber);
                review.setUser_id("-KI8O-d9u-c9zDVk7L5V");
                review.setUser_full_name("Paolo Parisi");
                review.setUser_thumbnail("https://www.flickr.com/photos/142675931@N04/26785603890/in/dateposted-public/");
                reviewsRef.setValue(review);

                /*reviews.add(review);
                Collections.sort(reviews);
                reviewAdapter.notifyDataSetChanged();*/

            }
        }
    }

    private void getRestaurantAndSetButtons() {

        Button orders_button = (Button) findViewById(R.id.order_button);
        assert orders_button != null;
        orders_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        OrderActivity.class);
                intent.putExtra("restaurant_id", targetRestaurant.getRestaurant_id());
                intent.putExtra("user_id", userID);
                startActivity(intent);
            }
        });

        Button reservations_button = (Button) findViewById(R.id.reservation_button);
        assert reservations_button != null;
        reservations_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        UserTableReservationActivity.class);
                intent.putExtra("userID", userID);
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
            //fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.white)));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bookmark user_bookmark = new Bookmark();
                    if(restaurantID!=null)
                        user_bookmark.setRestaurant_id(restaurantID);
                    if(userID!=null)
                        user_bookmark.setRestaurant_id(userID);
                    ArrayList<Bookmark> bookmarks_temp;
                    if(!bookmark) {
                        //read, add and write
                        fab.setImageDrawable(ContextCompat.getDrawable(UserRestaurantActivity.this, R.drawable.ic_star_on_24dp));
                        bookmark = true;
                        try {
                            bookmarks_temp = JSONUtil.readBookmarkList(context, null);
                            bookmarks_temp.add(user_bookmark);
                            JSONUtil.saveBookmarksList(context, bookmarks_temp);
                        }
                        catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                    else{
                        //read, remove and write
                        fab.setImageDrawable(ContextCompat.getDrawable(UserRestaurantActivity.this,R.drawable.ic_star_off_24dp));
                        bookmark = false;
                        try {
                            bookmarks_temp = JSONUtil.readBookmarkList(context, null);
                            bookmarks_temp.remove(user_bookmark);
                            JSONUtil.saveBookmarksList(context, bookmarks_temp);
                        }
                        catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
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
            return JSONUtil.isBookmark(this, userID, restaurantID);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

}
