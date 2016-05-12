package it.polito.group2.restaurantowner.user.restaurant_page;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.UUID;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.Bookmark;
import it.polito.group2.restaurantowner.data.MenuCategory;
import it.polito.group2.restaurantowner.data.Review;
import it.polito.group2.restaurantowner.data.JSONUtil;
import it.polito.group2.restaurantowner.data.Restaurant;
import it.polito.group2.restaurantowner.data.Offer;
import it.polito.group2.restaurantowner.data.User;
import it.polito.group2.restaurantowner.owner.MainActivity;
import it.polito.group2.restaurantowner.user.my_orders.MyOrdersActivity;
import it.polito.group2.restaurantowner.user.my_reviews.MyReviewsActivity;
import it.polito.group2.restaurantowner.user.restaurant_page.gallery.GalleryViewActivity;

public class UserRestaurantActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final int ADD_REQUEST = 1;
    private String userID, restaurantID;
    private ArrayList<Review> reviews;
    private ArrayList<Offer> offers;
    private ArrayList<MenuCategory> categories;
    private Restaurant targetRestaurant;
    private ReviewAdapter reviewAdapter;
    private String user_id;
    private String restaurant_id;
    private ArrayList<User> users = new ArrayList<User>();
    private Context context;
    public User current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_restaurant);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        if(getIntent().getExtras()!=null && getIntent().getExtras().getString("restaurant_id")!=null) {
            restaurant_id = getIntent().getExtras().getString("restaurant_id");
        }
        if(getIntent().getExtras()!=null && getIntent().getExtras().getString("user_id")!=null) {
            user_id = getIntent().getExtras().getString("user_id");
        }

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


        getRestaurantAndSetButtons();

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

        Glide.with(this).load(targetRestaurant.getPhotoUri()).into(image);

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
        setDrawer(toolbar);

        //TODO Rearrange the following code
        if(getIntent().getExtras()!=null && getIntent().getExtras().getString("user_id")!=null) {
            user_id = getIntent().getExtras().getString("user_id");
            try {
                users = JSONUtil.readJSONUsersList(context, null);
            }
            catch(JSONException e){
                e.printStackTrace();
            }
            for(User u : users){
                if(u.getId().equals(user_id)){
                    current_user = u;
                    break;
                }
            }
        }
        else{
            current_user = new User();
            current_user.setEmail("jkjs@dskj");
            //current_user.setFidelity_points(110);
            current_user.setFirst_name("Alex");
            current_user.setIsOwner(true);
            current_user.setPassword("tipiacerebbe");
            current_user.setPhone_number("0989897879789");
            current_user.setVat_number("sw8d9wd8w9d8w9d9");
        }
    }

    private void setDrawer(Toolbar toolbar) {
        //navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //TODO decomment handle logged/not logged user
        /*
        if(user_id==null){ //not logged
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
        TextView nav_username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderUsername);
        TextView nav_email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderEmail);
        ImageView nav_photo = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView);
        if(current_user.getFirst_name()!=null && current_user.getLast_name()==null)
            nav_username.setText(current_user.getFirst_name());
        else if(current_user.getFirst_name()==null && current_user.getLast_name()!=null)
            nav_username.setText(current_user.getLast_name());
        else if(current_user.getFirst_name()!=null && current_user.getLast_name()!=null)
            nav_username.setText(current_user.getFirst_name() + " " + current_user.getLast_name());
        if(current_user.getEmail()!=null)
            nav_email.setText(current_user.getEmail());
        if(current_user.getPhoto()!=null)
            nav_photo.setImageBitmap(current_user.getPhoto());
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
            b1.putString("user_id", user_id);
            intent1.putExtras(b1);
            startActivity(intent1);
            return true;
        }
        else if(id==R.id.nav_home){
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    UserRestaurantList.class);
            Bundle b1 = new Bundle();
            b1.putString("user_id", user_id);
            intent1.putExtras(b1);
            startActivity(intent1);
            return true;
        }
        else if(id==R.id.nav_login){
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    UserRestaurantList.class);
            startActivity(intent1);
            return true;
        } else if(id==R.id.nav_my_profile) {
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    UserProfile.class);
            Bundle b1 = new Bundle();
            b1.putString("user_id", user_id);
            intent1.putExtras(b1);
            startActivity(intent1);
            return true;
        } else if(id==R.id.nav_my_orders) {
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    MyOrdersActivity.class);
            Bundle b1 = new Bundle();
            b1.putString("user_id", user_id);
            intent1.putExtras(b1);
            startActivity(intent1);
            return true;
        } else if(id==R.id.nav_my_reservations){
            Intent intent3 = new Intent(
                    getApplicationContext(),
                    UserMyReservations.class);
            Bundle b3 = new Bundle();
            b3.putString("user_id", user_id);
            intent3.putExtras(b3);
            startActivity(intent3);
            return true;
        } else if(id==R.id.nav_my_reviews){
            Intent intent3 = new Intent(
                    getApplicationContext(),
                    MyReviewsActivity.class);
            Bundle b3 = new Bundle();
            b3.putString("user_id", user_id);
            intent3.putExtras(b3);
            startActivity(intent3);
            return true;
        } else if(id==R.id.nav_my_favourites){
            Intent intent3 = new Intent(
                    getApplicationContext(),
                    UserMyFavourites.class);
            Bundle b3 = new Bundle();
            b3.putString("user_id", user_id);
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
                //"EEE dd MMM yyyy 'at' HH:mm
                Review review =new Review(restaurantID, "djskdj", "Wed 16 Oct 1992 'at' 20:03", comment, UUID.randomUUID().toString(), null, starNumber);
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

    public void openGallery(View v){
        Intent intent = new Intent(this, GalleryViewActivity.class);
        intent.putExtra("restaurantID", restaurantID);
        startActivity(intent);
    }

    private void setRestaurantOffers() {
        if(offers.isEmpty()){
            /*CardView cardOffers = (CardView) findViewById(R.id.restaurant_offers);
            cardOffers.setVisibility(View.GONE);*/
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

    private void setUserReviews() {
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
            fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.white)));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bookmark user_bookmark = new Bookmark();
                    if(restaurant_id!=null)
                        user_bookmark.setRestaurant_id(restaurant_id);
                    if(user_id!=null)
                        user_bookmark.setRestaurant_id(user_id);
                    ArrayList<Bookmark> bookmarks_temp;
                    if(!isBookmark()) {
                        //read, add and write
                        fab.setImageDrawable(ContextCompat.getDrawable(UserRestaurantActivity.this, R.drawable.ic_star_on_24dp));
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
        Review r1 =new Review(restaurantID, "Paola C.", "Wed 20 Oct 2006 'at' 20:03", c1, UUID.randomUUID().toString(), null, 3.5f);

        String c2 = "Abbiamo preso la pizza da un metro e dobbiamo dare un giudizio più che positivo! Se il locale fosse un po più grande sicuramente farebbe concorrenza a parecchi grandi nomi!";
        Calendar date2 = Calendar.getInstance();
        date2.set(Calendar.HOUR_OF_DAY, 10);
        Review r2 =new Review(restaurantID, "Mario R.", "Thu 13 Jul 2106 'at' 21:03", c2, UUID.randomUUID().toString(), null, 3f);


        Calendar date3 = Calendar.getInstance();
        date3.set(Calendar.HOUR_OF_DAY, 8);
        Review r3 =new Review(restaurantID, "Giovanni C.", "Sun 10 Jan 1997 'at' 20:30", "", UUID.randomUUID().toString(), null, 4.5f);

        String c4 = "Sono andata a cena il mio ragazzo e siamo rimasti molto sazi e contenti. Abbiamo preso le chiacchiere al crudo ed erano veramente buone! La pizza era molto buona, una delle più buone mangiate a Torino.";
        Calendar date4 = Calendar.getInstance();
        date4.set(Calendar.HOUR_OF_DAY, 12);
        date4.set(Calendar.MINUTE, 30);
        Review r4 =new Review(restaurantID, "Francesco G.", "Wed 28 Sep 2005 'at' 22:03", c4, UUID.randomUUID().toString(), null, 3.5f);

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
