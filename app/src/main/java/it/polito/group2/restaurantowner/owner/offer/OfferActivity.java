package it.polito.group2.restaurantowner.owner.offer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;

import it.polito.group2.restaurantowner.HaveBreak;
import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.Offer;
import it.polito.group2.restaurantowner.firebasedata.User;
import it.polito.group2.restaurantowner.owner.MainActivity;
import it.polito.group2.restaurantowner.owner.my_offers.MyOffersActivity;

public class OfferActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OfferFragment.OnActionListener,
        CategoryFragment.OnActionListener {

    private Offer offer;

    private String userID = null;
    private String restaurantID = null;
    private String offerID = null;

    private User user;

    private FirebaseDatabase firebase;
    private ProgressDialog mProgressDialog;

    private ArrayList<Meal> restaurantMealList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_offer_activity);

        //TODO corregere quando viene passato un utente corretto
        userID = "-KITUg8848bUzejyV7oD";// = FirebaseUtil.getCurrentUserId();

        if(getIntent().getExtras()!=null && getIntent().getExtras().getString("restaurant_id")!=null) {
            restaurantID = getIntent().getExtras().getString("restaurant_id");
        }

        if(getIntent().getExtras()!=null && getIntent().getExtras().getString("offer_id")!=null) {
            offerID = getIntent().getExtras().getString("offer_id");
        }

        if(userID == null || restaurantID == null) {
            Log.d("FILIPPO", "utente non loggato o restaurantID non ricevuto");
            Intent intent = new Intent(this, HaveBreak.class);
            finish();
            startActivity(intent);
        }

        showProgressDialog();
        firebase = FirebaseDatabase.getInstance();
        restaurantMealList = new ArrayList<Meal>();
        DatabaseReference userReference = firebase.getReference("users/" + userID);
        Query mealsReference = firebase.getReference("meals").orderByChild("restaurant_id").equalTo(restaurantID);
        DatabaseReference offerReference = null;
        if(offerID != null) {
            offerReference = firebase.getReference("offers/" + offerID);
        }
        hideProgressDialog();

        mealsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                restaurantMealList.add(dataSnapshot.getValue(Meal.class));
                Log.d("FILIPPO", "onChildAdded");
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Meal changedMeal = dataSnapshot.getValue(Meal.class);
                for (Meal m : restaurantMealList) {
                    if (m.getMeal_id().equals(changedMeal.getMeal_id())) {
                        restaurantMealList.remove(m);
                        restaurantMealList.add(changedMeal);
                        break;
                    }
                }
                Log.d("FILIPPO", "onChildChanged");
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Meal changedMeal = dataSnapshot.getValue(Meal.class);
                for (Meal m : restaurantMealList) {
                    if (m.getMeal_id().equals(changedMeal.getMeal_id())) {
                        restaurantMealList.remove(m);
                        break;
                    }
                }
                Log.d("FILIPPO", "onChildRemoved");
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        if(offerID != null && offerReference != null) {
            offerReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    offer = dataSnapshot.getValue(Offer.class);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        } else {
            offer = setNewOffer();
        }

        //Toolbar setting
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Navigation drawer setting
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Navigation drawer user info
        TextView nav_username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderUsername);
        TextView nav_email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderEmail);
        ImageView nav_photo = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView);
        if(user != null) {
            if (user.getUser_full_name() != null)
                nav_username.setText(user.getUser_full_name());
            if (user.getUser_email() != null)
                nav_email.setText(user.getUser_email());
        }
        SharedPreferences userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
        Uri photouri = null;
        if(userDetails.getString("photouri", null) != null) {
            photouri = Uri.parse(userDetails.getString("photouri", null));
            File f = new File(getRealPathFromURI(photouri));
            Drawable d = Drawable.createFromPath(f.getAbsolutePath());
            navigationView.getHeaderView(0).setBackground(d);
        } else {
            nav_photo.setImageResource(R.drawable.blank_profile);
        }

        //Fragment loader
        if(findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            OfferFragment offerFragment = OfferFragment.newInstance(this.offer, this.restaurantMealList);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, offerFragment, "OFFER");
            transaction.commit();
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
            startActivity(intent1);
            return true;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSaveClicked(Offer offer) {
        this.offer = offer;
        DatabaseReference offersReference = firebase.getReference("offers/");
        DatabaseReference keyReference= offersReference.push();
        this.offer.setOfferID(keyReference.getKey());
        keyReference.setValue(this.offer);
        this.offer = setNewOffer();
        Intent intent = new Intent(this, MyOffersActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSaveListClicked(Offer offer) {
        this.offer = offer;
        OfferFragment offerFragment = OfferFragment.newInstance(offer, this.restaurantMealList);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, offerFragment, "OFFER");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onCategoryListRq(Offer offer) {
        this.offer = offer;
        CategoryFragment categoryFragment = CategoryFragment.newInstance(getCategoryList(), offer);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, categoryFragment, "CATEGORY");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onMealListRq(Offer offer) {
        this.offer = offer;
        MealFragment mealFragment = MealFragment.newInstance(restaurantMealList, offer);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mealFragment, "MEAL");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private Offer setNewOffer() {
        Offer o = new Offer();
        o.setRestaurantID(restaurantID);
        o.setUserID(userID);
        o.setOfferEnabled(true);
        return o;
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

    private ArrayList<String> getCategoryList() {
        ArrayList<String> categoryList = new ArrayList<String>();
        for(Meal m : restaurantMealList) {
            if(categoryList.indexOf(m.getMeal_category()) == -1)
                categoryList.add(m.getMeal_category());
        }

        return categoryList;
    }

}
