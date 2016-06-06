package it.polito.group2.restaurantowner.owner.my_offers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import it.polito.group2.restaurantowner.firebasedata.Offer;
import it.polito.group2.restaurantowner.firebasedata.User;
import it.polito.group2.restaurantowner.gallery.GalleryViewActivity;
import it.polito.group2.restaurantowner.owner.MainActivity;
import it.polito.group2.restaurantowner.owner.MenuRestaurant_page;
import it.polito.group2.restaurantowner.owner.ReservationActivity;
import it.polito.group2.restaurantowner.owner.ReviewsActivity;
import it.polito.group2.restaurantowner.owner.StatisticsActivity;
import it.polito.group2.restaurantowner.owner.offer.OfferActivity;

public class MyOffersActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private String userID;
    private String restaurantID;
    private User user;

    private FirebaseDatabase firebase;
    private ProgressDialog mProgressDialog;
    private ArrayList<Offer> offerList;             //offer list got from firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_myoffers_activity);

        userID = "-KITUg8848bUzejyV7oD";// = FirebaseUtil.getCurrentUserId();
        if(getIntent().getExtras()!=null && getIntent().getExtras().getString("restaurant_id")!=null) {
            restaurantID = getIntent().getExtras().getString("restaurant_id");
        }

        if(userID == null || restaurantID == null) {
            Log.d("FILIPPO", "utente non loggato o restaurantID non ricevuto");
            Intent intent = new Intent(this, HaveBreak.class);
            finish();
            startActivity(intent);
        }

        showProgressDialog();
        firebase = FirebaseDatabase.getInstance();
        offerList = new ArrayList<Offer>();
        Query offersReference = firebase.getReference("offers").orderByChild("restaurant_id").equalTo(restaurantID);
        DatabaseReference userReference = firebase.getReference("users/" + userID);
        hideProgressDialog();


        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        offersReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                offerList.add(dataSnapshot.getValue(Offer.class));
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Offer changedOffer = dataSnapshot.getValue(Offer.class);
                for (Offer o : offerList) {
                    if (o.getOfferID().equals(changedOffer.getOfferID())) {
                        offerList.remove(o);
                        offerList.add(changedOffer);
                        break;
                    }
                }
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Offer changedOffer = dataSnapshot.getValue(Offer.class);
                for (Offer o : offerList) {
                    if (o.getOfferID().equals(changedOffer.getOfferID())) {
                        offerList.remove(o);
                        break;
                    }
                }
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

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

        setOfferList();
    }

    private void setOfferList() {
        final RecyclerView list = (RecyclerView) findViewById(R.id.offer_list);
        assert list != null;
        list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        list.setNestedScrollingEnabled(false);
        OfferAdapter adapter = new OfferAdapter(this, offerList);
        list.setAdapter(adapter);
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
        if(id==R.id.action_my_restaurants){
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    MainActivity.class);
            startActivity(intent1);
            return true;
        } else if(id==R.id.action_gallery) {
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    GalleryViewActivity.class);
            Bundle b = new Bundle();
            b.putString("restaurant_id", restaurantID);
            intent1.putExtras(b);
            startActivity(intent1);
            return true;
        } else if(id==R.id.action_menu) {
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    MenuRestaurant_page.class);
            Bundle b = new Bundle();
            b.putString("restaurant_id", restaurantID);
            intent1.putExtras(b);
            startActivity(intent1);
            return true;
        } else if(id==R.id.action_offers) {
            Intent intent2 = new Intent(
                    getApplicationContext(),
                    MyOffersActivity.class);
            Bundle b2 = new Bundle();
            b2.putString("restaurant_id", restaurantID);
            intent2.putExtras(b2);
            startActivity(intent2);
            return true;
        } else if(id==R.id.action_reservations){
            Intent intent3 = new Intent(
                    getApplicationContext(),
                    ReservationActivity.class);
            Bundle b3 = new Bundle();
            b3.putString("restaurant_id", restaurantID);
            intent3.putExtras(b3);
            startActivity(intent3);
            return true;
        } else if(id==R.id.action_reviews){
            Intent intent4 = new Intent(
                    getApplicationContext(),
                    ReviewsActivity.class); //here Filippo must insert his class name
            Bundle b4 = new Bundle();
            b4.putString("restaurant_id", restaurantID);
            intent4.putExtras(b4);
            startActivity(intent4);
            return true;
        } else if(id==R.id.action_statistics){
            Intent intent5 = new Intent(
                    getApplicationContext(),
                    StatisticsActivity.class); //here Filippo must insert his class name
            Bundle b5 = new Bundle();
            b5.putString("restaurant_id", restaurantID);
            intent5.putExtras(b5);
            startActivity(intent5);
            return true;
        } else if (id == R.id.icon_new_offer) {
            Intent intent4 = new Intent(
                    getApplicationContext(),
                    OfferActivity.class); //here Filippo must insert his class name
            Bundle b4 = new Bundle();
            b4.putString("restaurant_id", restaurantID);
            intent4.putExtras(b4);
            startActivity(intent4);
            return true;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openOfferDatails(View v) {
        v.findViewById(R.id.offer_details).setVisibility(View.VISIBLE);
    }

    public void closeOfferDatails(View v) {
        v.findViewById(R.id.offer_details).setVisibility(View.GONE);
    }

    public void editOffer(View v) {
        TextView offerID = (TextView) v.findViewById(R.id.offer_id);
        String id = offerID.getText().toString();
        Intent intent = new Intent(getApplicationContext(), OfferActivity.class);
        Bundle b = new Bundle();
        b.putString("restaurant_id", restaurantID);
        b.putString("offer_id", id);
        intent.putExtras(b);
        startActivity(intent);
    }

}
