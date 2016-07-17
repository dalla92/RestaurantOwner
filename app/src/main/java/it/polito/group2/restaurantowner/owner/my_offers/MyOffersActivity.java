package it.polito.group2.restaurantowner.owner.my_offers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.DrawerUtil;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.Utils.OnBackUtil;
import it.polito.group2.restaurantowner.Utils.RemoveListenerUtil;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.Offer;
import it.polito.group2.restaurantowner.firebasedata.User;
import it.polito.group2.restaurantowner.owner.MainActivity;
import it.polito.group2.restaurantowner.owner.offer.OfferActivity;

public class MyOffersActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private Toolbar toolbar;
    private String restaurantID;
    private ProgressDialog mProgressDialog;
    private ArrayList<Offer> offerList = null;
    private ArrayList<Meal> mealList = null;
    private DatabaseReference q;
    private ValueEventListener l;
    private FirebaseDatabase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_myoffers_activity);

        //Toolbar setting
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebase = FirebaseDatabase.getInstance();

        //User object
        DatabaseReference userRef = FirebaseUtil.getCurrentUserRef();
        if (userRef == null) {
            OnBackUtil.clean_stack_and_go_to_main_activity(this);
        }

        setDrawer();

        if (getIntent().getExtras() != null && getIntent().getExtras().getString("restaurant_id") != null) {
            restaurantID = getIntent().getExtras().getString("restaurant_id");
            q = FirebaseUtil.getOffersRef(restaurantID);
            if (q == null)
                OnBackUtil.clean_stack_and_go_to_main_activity(this);
            assert q != null;
            l = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<Offer> offers = new ArrayList<>();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        offers.add(d.getValue(Offer.class));
                    }
                    offerList = offers;
                    DatabaseReference mealsRef = FirebaseUtil.getMealsRef(restaurantID);
                    if (mealsRef == null)
                        OnBackUtil.clean_stack_and_go_to_main_activity(getBaseContext());
                    assert mealsRef != null;
                    mealsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<Meal> meals = new ArrayList<>();
                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                meals.add(d.getValue(Meal.class));
                            }
                            FirebaseUtil.hideProgressDialog(mProgressDialog);
                            mealList = meals;
                            setOfferList();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
        } else {
            OnBackUtil.clean_stack_and_go_to_main_activity(this);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        mProgressDialog = FirebaseUtil.initProgressDialog(this);
        FirebaseUtil.showProgressDialog(mProgressDialog);
        q.addValueEventListener(l);
    }
    @Override
    protected void onStop() {
        super.onStop();
        RemoveListenerUtil.remove_value_event_listener(q, l);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            OnBackUtil.clean_stack_and_go_to_restaurant_page(this, restaurantID);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return DrawerUtil.drawer_owner_not_restaurant_page(this, item, restaurantID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.owner_myoffers_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                Intent intent = new Intent(this, OfferActivity.class);
                intent.putExtra("restaurant_id", restaurantID);
                startActivity(intent);
                break;
        }
        return true;
    }

    //MODEL FUNCTIONS ==============================================================================

    private void setOfferList() {
        final RecyclerView list = (RecyclerView) findViewById(R.id.offer_list);
        assert list != null;
        list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        list.setNestedScrollingEnabled(false);
        OfferAdapter adapter = new OfferAdapter(this, offerList, mealList);
        list.setAdapter(adapter);
    }

    //ACTIVITY FUNCTIONS ===========================================================================

    private void setDrawer() {
        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        String userID = FirebaseUtil.getCurrentUserId();
        if (userID != null) {

            DatabaseReference userRef = firebase.getReference("users/" + userID);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    TextView nav_username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderUsername);
                    TextView nav_email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderEmail);
                    ImageView nav_picture = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderPicture);
                    User target = dataSnapshot.getValue(it.polito.group2.restaurantowner.firebasedata.User.class);
                    TextView nav_points = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderPoints);
                    nav_points.setText(target.getUser_fidelity_points() + " " + getString(R.string.points));

                    nav_username.setText(target.getUser_full_name());
                    nav_email.setText(target.getUser_email());

                    String photoUri = target.getUser_photo_firebase_URL();
                    if (photoUri == null || photoUri.equals("")) {
                        Glide
                                .with(getApplicationContext())
                                .load(R.drawable.blank_profile_nav)
                                .centerCrop()
                                .into(nav_picture);
                    } else {
                        Glide
                                .with(getApplicationContext())
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

        Menu menu = navigationView.getMenu();
        MenuItem i = menu.findItem(R.id.action_edit);
        i.setVisible(false);
        MenuItem i2 = menu.findItem(R.id.action_show_as);
        i2.setVisible(false);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressWarnings("StatementWithEmptyBody")
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Handle navigation view item clicks here.
                return DrawerUtil.drawer_owner_not_restaurant_page(MyOffersActivity.this, item, restaurantID);
            }
        });
    }
}
