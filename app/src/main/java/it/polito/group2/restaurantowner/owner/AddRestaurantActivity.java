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

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.JSONUtil;
import it.polito.group2.restaurantowner.data.OpenTime;
import it.polito.group2.restaurantowner.data.Restaurant;

public class AddRestaurantActivity extends AppCompatActivity implements FragmentInfo.OnInfoPass, FragmentServices.OnServicesPass, FragmentExtras.OnExtrasPass {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);
        Intent intent = getIntent();
        if(intent.hasExtra("Restaurant"))
            res = (Restaurant) intent.getExtras().get("Restaurant");
        if(res==null){
            res = new Restaurant();
            res.setRestaurantId(UUID.randomUUID().toString());
            res.setPhotoUri("");
            //TODO take off this hardcoded values and get the real values
            res.setRating("4.5");
            res.setReservedPercentage("27%");
            res.setReservationNumber("240");
        }

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
            if(res.getName().equals(""))
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
       res.setName(name);
        res.setAddress(address);
        res.setPhoneNum(phone);
        res.setCategory(category);
    }


    @Override
    public void onServicesPass(Boolean fidelity, Boolean tableRes, String numTables, Boolean takeAway, String orderPerHour, List<String> lunchOpenTime,
                               List<String> lunchCloseTime, List<String> dinnerOpenTime, List<String> dinnerCloseTime, boolean[] lunchClosure, boolean[] dinnerClosure) {
        res.setFidelity(fidelity);
        res.setTableReservation(tableRes);
        if(tableRes)
            res.setTableNum(numTables);
        res.setTakeAway(takeAway);
        if(takeAway)
            res.setOrdersPerHour(orderPerHour);
        ArrayList<OpenTime> openTimeList = new ArrayList<>();
        for(int i=0;i<7;i++){
            OpenTime lunch = new OpenTime();
            lunch.setRestaurantId(res.getRestaurantId());
            lunch.setType("Lunch");
            lunch.setDayOfWeek(i);
            lunch.setIsOpen(!lunchClosure[i]);
            if(lunch.isOpen()){
                lunch.setOpenHour(lunchOpenTime.get(i));
                lunch.setCloseHour(lunchCloseTime.get(i));
            }
            openTimeList.add(lunch);
            OpenTime dinner = new OpenTime();
            dinner.setRestaurantId(res.getRestaurantId());
            dinner.setType("Dinner");
            dinner.setDayOfWeek(i);
            dinner.setIsOpen(!dinnerClosure[i]);
            if(dinner.isOpen()){
                dinner.setOpenHour(dinnerOpenTime.get(i));
                dinner.setCloseHour(dinnerCloseTime.get(i));
            }
            openTimeList.add(dinner);

        }
        try {
            ArrayList<OpenTime> otList = JSONUtil.readJSONOpenTimeList(this, res.getRestaurantId());
            otList.addAll(openTimeList);
            JSONUtil.saveJSONOpenTimeList(this, otList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onExtrasPass(List<RestaurantService> list) {
        if(list!=null) {
            for (RestaurantService rs : list) {
                rs.setRestaurantId(res.getRestaurantId());
            }
            try {
                ArrayList<RestaurantService> rsList = JSONUtil.readJSONServicesList(this,res.getRestaurantId());
                rsList.addAll(list);
                JSONUtil.saveJSONServiceList(this, rsList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
                    fi = FragmentInfo.newInstance(res);
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
