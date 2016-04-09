package it.polito.group2.restaurantowner;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    private Restaurant res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);

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
            Intent intent=new Intent();
            intent.putExtra("Restaurant",res);
            setResult(RESULT_OK,intent);
            finish();//finishing activity
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveData(){

        res = new Restaurant();
        FragmentInfo fragmentInfo = (FragmentInfo) mSectionsPagerAdapter.getItem(0);
        fragmentInfo.passData();
        FragmentServices fragmentServices = (FragmentServices) mSectionsPagerAdapter.getItem(1);
        fragmentServices.passData();
        FragmentExtras fragmentExtras = (FragmentExtras) mSectionsPagerAdapter.getItem(2);
        fragmentExtras.passData();
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
                               List<String> lunchCloseTime, List<String> dinnerOpenTime, List<String> dinnerCloseTime, List<Boolean> lunchClosure, List<Boolean> dinnerClosure) {
        res.setFidelity(fidelity);
        res.setTableReservation(tableRes);
        if(tableRes)
            res.setTableNum(numTables);
        res.setTakeAway(takeAway);
        if(takeAway)
            res.setOrdersPerHour(orderPerHour);
        for(int i=0;i<7;i++){
            OpenTime lunch = new OpenTime();
            lunch.setRestaurantName(res.getName());
            lunch.setType("Lunch");
            lunch.setDayOfWeek(String.valueOf(i));
            lunch.setIsOpen(lunchClosure.get(i));
            if(lunch.isOpen()){
                lunch.setOpenHour(lunchOpenTime.get(i));
                lunch.setCloseHour(lunchCloseTime.get(i));
            }

            OpenTime dinner = new OpenTime();
            dinner.setRestaurantName(res.getName());
            dinner.setType("Dinner");
            dinner.setDayOfWeek(String.valueOf(i));
            dinner.setIsOpen(dinnerClosure.get(i));
            if(dinner.isOpen()){
                dinner.setOpenHour(dinnerOpenTime.get(i));
                dinner.setCloseHour(dinnerCloseTime.get(i));
            }
        }

    }

    @Override
    public void onExtrasPass(List<RestaurantService> list) {
        if(list!=null)
            for(RestaurantService rs : list){
               rs.setRestaurantId(res.getName());
            }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private FragmentInfo fi = null;
        private FragmentServices fs = null;
        private FragmentExtras fe = null;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position==0) {
                if (fi==null)
                    fi = FragmentInfo.newInstance(position + 1);
                return fi;
            }
            if(position==1) {
                if (fs==null)
                    fs = FragmentServices.newInstance(position + 1);
                return fs;
            }
            else {
                if (fe == null)
                    fe = FragmentExtras.newInstance(position + 1);
                return fe;
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
