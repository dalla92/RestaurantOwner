package it.polito.group2.restaurantowner.owner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.Utils.OnBackUtil;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
import it.polito.group2.restaurantowner.firebasedata.RestaurantPreview;
import it.polito.group2.restaurantowner.firebasedata.RestaurantTimeSlot;

public class AddRestaurantActivity extends AppCompatActivity implements FragmentInfo.OnInfoPass, FragmentServices.OnServicesPass, FragmentExtras.OnExtrasPass, GoogleApiClient.OnConnectionFailedListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private Restaurant res = null;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseDatabase firebase;
    private ProgressDialog mProssesDialog;
    private boolean editMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);
        Intent intent = getIntent();
        editMode = false;

        mProssesDialog = FirebaseUtil.initProgressDialog(this);
        firebase = FirebaseDatabase.getInstance();
        if(intent.hasExtra("Restaurant")) {
            res = (Restaurant) intent.getExtras().get("Restaurant");
            editMode = true;
        }
        if(res==null){
            res = new Restaurant();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .build();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_restaurant, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_save) {
            FirebaseUtil.showProgressDialog(mProssesDialog);
            saveData();
            if (res.getRestaurant_name().equals(""))
                Toast.makeText(this, "Please insert restaurant name to continue", Toast.LENGTH_SHORT).show();
            else {
                final String userID = FirebaseUtil.getCurrentUserId();
                if (userID != null) {
                    DatabaseReference restaurantRef;
                    if(editMode){
                        restaurantRef = firebase.getReference("restaurants/" + res.getRestaurant_id());
                    }
                    else {
                        res.setUser_id(userID);

                        //resList.add(0,res);
                        //mAdapter.addItem(0, res);
                        DatabaseReference restaurantsReference = firebase.getReference("restaurants");
                        restaurantRef = restaurantsReference.push();
                        res.setRestaurant_id(restaurantRef.getKey());
                    }
                    if (res.getRestaurant_address() != null && !res.getRestaurant_address().trim().equals("")) {
                        String address = res.getRestaurant_address();
                        try {
                            Geocoder geocoder = new Geocoder(AddRestaurantActivity.this);
                            List<Address> addresses;
                            addresses = geocoder.getFromLocationName(address, 1);
                            if(addresses.size() > 0) {
                                res.setRestaurant_latitude_position(addresses.get(0).getLatitude());
                                res.setRestaurant_longitude_position(addresses.get(0).getLongitude());
                           }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    final Restaurant finalRes = res;

                    restaurantRef.setValue(res).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //save also the restaurant preview if the restaurant was succefully written
                            DatabaseReference restaurantReference2 = firebase.getReference("restaurants_previews/" + finalRes.getRestaurant_id());
                            RestaurantPreview r_p = new RestaurantPreview();
                            if (finalRes.getRestaurant_address() != null && !finalRes.getRestaurant_address().trim().equals("")) {
                                String address = finalRes.getRestaurant_address();
                                Geocoder geocoder = new Geocoder(AddRestaurantActivity.this, Locale.ITALY);
                                try {
                                    List<Address> addressList = geocoder.getFromLocationName(address, 1);
                                    if (addressList != null && addressList.size() > 0) {
                                        if (addressList.get(0).hasLatitude())
                                            r_p.setLat(addressList.get(0).getLatitude());
                                        if (addressList.get(0).hasLongitude())
                                            r_p.setLon(addressList.get(0).getLongitude());
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            r_p.setRestaurant_id(finalRes.getRestaurant_id());
                            r_p.setRestaurant_name(finalRes.getRestaurant_name());
                            r_p.setRestaurant_price_range(1);
                            r_p.setRestaurant_rating(1);
                            r_p.setUser_id(userID);
                            r_p.setTables_number(finalRes.getRestaurant_total_tables_number());
                            r_p.setRestaurant_category(finalRes.getRestaurant_category());
                            r_p.setLat(finalRes.getRestaurant_latitude_position());
                            r_p.setLon(finalRes.getRestaurant_longitude_position());
                            restaurantReference2.setValue(r_p);

                            //saving the names of the restaurant with the id in restaurant_names for search purpose
                            firebase.getReference("restaurant_names/" + finalRes.getRestaurant_name().toLowerCase() + "/" + finalRes.getRestaurant_id()).setValue(true);
                            FirebaseUtil.hideProgressDialog(mProssesDialog);
                            Toast.makeText(AddRestaurantActivity.this, "Restaurant added successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

                }
                else
                    Toast.makeText(this,"You are not logged in!", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveData(){
        mSectionsPagerAdapter.saveDataFromFragments();
    }

    @Override
    public void onInfoPass(String name, String address, String phone, String category) {
       res.setRestaurant_name(name);
        res.setRestaurant_address(address);
        res.setRestaurant_telephone_number(phone);
        res.setRestaurant_category(category);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if( mGoogleApiClient != null )
            mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if( mGoogleApiClient != null )
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if(res == null || res.getRestaurant_id() == null)
            OnBackUtil.clean_stack_and_go_to_main_activity(this);
        else
            OnBackUtil.clean_stack_and_go_to_restaurant_page(this, res.getRestaurant_id());
    }

    @Override
    public void onServicesPass(Boolean fidelity, Boolean tableRes, String numTables, Boolean takeAway, String orderPerHour, List<String> lunchOpenTime,
                               List<String> lunchCloseTime, List<String> dinnerOpenTime, List<String> dinnerCloseTime, boolean[] lunchClosure, boolean[] dinnerClosure) {

        res.setFidelityProgramAccepted(fidelity);
        res.setTableReservationAllowed(tableRes);
        if(tableRes)
            res.setRestaurant_total_tables_number(Integer.parseInt(numTables));
        res.setTakeAwayAllowed(takeAway);
        if(takeAway)
            res.setRestaurant_orders_per_hour(Integer.parseInt(orderPerHour));
        ArrayList<RestaurantTimeSlot> openTimeList = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                RestaurantTimeSlot daySlot = new RestaurantTimeSlot();
                //daySlot.setRestaurant_id(res.getRestaurant_id());
                daySlot.setDay_of_week(i);
                if (!lunchClosure[i]) {
                    daySlot.setLunch(true);
                    daySlot.setOpen_lunch_time(lunchOpenTime.get(i));
                    daySlot.setClose_lunch_time(lunchCloseTime.get(i));
                }
                else
                    daySlot.setLunch(false);

                if (!dinnerClosure[i]) {
                    daySlot.setDinner(true);
                    daySlot.setOpen_dinner_time(dinnerOpenTime.get(i));
                    daySlot.setClose_dinner_time(dinnerCloseTime.get(i));
                }
                else
                    daySlot.setDinner(false);

                openTimeList.add(daySlot);
            }
        res.setRestaurant_time_slot(openTimeList);

    }

    @Override
    public void onExtrasPass(Restaurant myRes) {
        if(myRes!=null) {
            res.setAnimalAllowed(myRes.getAnimalAllowed());
            res.setWifiPresent(myRes.getWifiPresent());
            res.setCreditCardAccepted(myRes.getCreditCardAccepted());
            res.setCeliacFriendly(myRes.getCeliacFriendly());
            res.setAirConditionedPresent(myRes.getAirConditionedPresent());
            res.setTvPresent(myRes.getTvPresent());
            res.setRestaurant_squared_meters(myRes.getRestaurant_squared_meters());
            res.setRestaurant_closest_metro(myRes.getRestaurant_closest_metro());
            res.setRestaurant_closest_bus(myRes.getRestaurant_closest_bus());
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private FragmentInfo fi;
        private FragmentServices fs;
        private FragmentExtras fe;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position==0) {
                    fi = FragmentInfo.newInstance(res, mGoogleApiClient, AddRestaurantActivity.this);
                return fi;
            }
            if(position==1) {
                    fs = FragmentServices.newInstance(res,AddRestaurantActivity.this);
                return fs;
            }
            else {
                    fe = FragmentExtras.newInstance(res,AddRestaurantActivity.this);
                return fe;
            }
        }

        // Here we can finally safely save a reference to the created
        // Fragment, no matter where it came from (either getItem() or
        // FragmentManger). Simply save the returned Fragment from
        // super.instantiateItem() into an appropriate reference depending
        // on the ViewPager position.
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            // save the appropriate reference depending on position
            switch (position) {
                case 0:
                    fi = (FragmentInfo) createdFragment;
                    break;
                case 1:
                    fs = (FragmentServices) createdFragment;
                    break;
                case 2:
                    fe = (FragmentExtras) createdFragment;
                    break;
            }
            return createdFragment;
        }

        public void saveDataFromFragments() {
            // do work on the referenced Fragments, but first check if they
            // even exist yet, otherwise you'll get an NPE.

            if (fi != null) {
                fi.passData();
            }

            if (fs != null) {
                fs.passData();
            }

            if (fe != null) {
                fe.passData();
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.addres_section1);
                case 1:
                    return getString(R.string.addres_section2);
                case 2:
                    return getString(R.string.addres_section3);
            }
            return null;
        }
    }
}
