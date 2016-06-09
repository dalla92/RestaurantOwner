package it.polito.group2.restaurantowner.owner;

import android.content.Intent;
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

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.JSONUtil;
import it.polito.group2.restaurantowner.data.OpenTime;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
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

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Restaurant res = null;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);
        Intent intent = getIntent();
        if(intent.hasExtra("Restaurant"))
            res = (Restaurant) intent.getExtras().get("Restaurant");
        if(res==null){
            res = new Restaurant();
            //res.setRestaurant_id(UUID.randomUUID().toString());
            res.setRestaurant_photo_firebase_URL("");
            //TODO take off this hardcoded values and get the real values
            res.setRestaurant_rating(4);
            res.setRestaurant_total_tables_number(200);
            res.setRestaurant_orders_per_hour(50);
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

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
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
            saveData();
            if(res.getRestaurant_name().equals(""))
                Toast.makeText(this,"Please insert restaurant name to continue", Toast.LENGTH_SHORT).show();
            else {
                Intent intent = new Intent();
                intent.putExtra("Restaurant", res);
                setResult(RESULT_OK, intent);
                finish();//finishing activity
                return true;
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
        for(int i=0;i<7;i++){

            RestaurantTimeSlot daySlot = new RestaurantTimeSlot();
            //daySlot.setRestaurant_id(res.getRestaurant_id());
            daySlot.setDay_of_week(i);
            if(!lunchClosure[i]){
                daySlot.setLunch(true);
                daySlot.setOpen_lunch_time(lunchOpenTime.get(i));
                daySlot.setClose_lunch_time(lunchCloseTime.get(i));
            }
            if(!dinnerClosure[i]){
                daySlot.setDinner(true);
                daySlot.setOpen_dinner_time(dinnerOpenTime.get(i));
                daySlot.setClose_dinner_time(dinnerCloseTime.get(i));
            }
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
            //res.setCeliacFriendly(myRes.getCeliacFriendly());
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
