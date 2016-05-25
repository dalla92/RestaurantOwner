package it.polito.group2.restaurantowner.user.my_reviews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.JSONUtil;
import it.polito.group2.restaurantowner.data.Review;
import it.polito.group2.restaurantowner.data.User;
import it.polito.group2.restaurantowner.owner.MainActivity;
import it.polito.group2.restaurantowner.owner.SimpleItemTouchHelperCallback;
import it.polito.group2.restaurantowner.user.my_orders.MyOrdersActivity;
import it.polito.group2.restaurantowner.user.restaurant_page.UserMyFavourites;
import it.polito.group2.restaurantowner.user.restaurant_page.UserMyReservations;
import it.polito.group2.restaurantowner.user.restaurant_page.UserProfile;
import it.polito.group2.restaurantowner.user.restaurant_list.UserRestaurantList;

public class MyReviewsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private String userID;
    private MyReviewAdapter adapter;
    private ArrayList<Review> myReviews;
    private String user_id;
    private ArrayList<User> users = new ArrayList<User>();
    private Context context;
    public User current_user;
    private Drawable d;

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
        //navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //TODO Rearrange the following code
        if(getIntent().getExtras()!=null && getIntent().getExtras().getString("user_id")!=null) {
            user_id = getIntent().getExtras().getString("user_id");
            try {
                users = JSONUtil.readJSONUsersList(this, null);
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
        TextView nav_username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderUsername);
        TextView nav_email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderEmail);
        ImageView nav_photo = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView);
        if(current_user!=null) {
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
            nav_photo.setImageResource(R.drawable.blank_profile);
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
    protected void onDestroy() {
        super.onDestroy();
        d = null;
        System.gc();
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
        Review r1 = new Review("1", "Paola C.", date1, c1, UUID.randomUUID().toString(), 4.5f);

        String c2 = "Think of Recyclerview not as a ListView 1:1 replacement but rather as a more flexible component for complex use cases. And as you say, your solution is what google expected of you.";
        Calendar date2 = Calendar.getInstance();
        date2.set(Calendar.HOUR_OF_DAY, 10);
        Review r2 = new Review("1", "Mario R.", date2, c2, UUID.randomUUID().toString(), 3f);


        Calendar date3 = Calendar.getInstance();
        date3.set(Calendar.HOUR_OF_DAY, 8);
        Review r3 = new Review("1", "Antonio V.", date3, "", UUID.randomUUID().toString(), 4f);

        String c4 = "Now look into that last piece of code: onCreateViewHolder(ViewGroup parent, int viewType) the signature already suggest different view types.";
        Calendar date4 = Calendar.getInstance();
        date4.set(Calendar.HOUR_OF_DAY, 12);
        date4.set(Calendar.MINUTE, 30);
        Review r4 = new Review("1", "Paola F.", date4, c4, UUID.randomUUID().toString(), 2.5f);

        reviews.add(r1);
        reviews.add(r2);
        reviews.add(r3);
        reviews.add(r4);
        return reviews;
    }

}
