package it.polito.group2.restaurantowner;

import android.app.Dialog;
import android.app.TimePickerDialog;
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

public class AddRestaurantActivity extends AppCompatActivity {

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class FragmentInfo extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        String selectedCategory = null;

        public FragmentInfo() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static FragmentInfo newInstance(int sectionNumber) {
            FragmentInfo fragment = new FragmentInfo();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_add_restaurant_info, container, false);
            EditText name = (EditText) rootView.findViewById(R.id.resadd_info_name);
            EditText address = (EditText) rootView.findViewById(R.id.resadd_info_address);
            EditText phone = (EditText) rootView.findViewById(R.id.resadd_info_phone);
            final Spinner category = (Spinner) rootView.findViewById(R.id.resadd_info_category);

            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                    R.array.categories_array, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            category.setAdapter(adapter);
            selectedCategory = String.valueOf(category.getSelectedItem());

            category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedCategory = String.valueOf(parent.getItemAtPosition(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            return rootView;
        }
    }

    public static class FragmentServices extends Fragment implements TimePickerDialog.OnTimeSetListener {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        Button buttonLunchOpen;
        Button buttonLunchClose;
        Button buttonDinnerOpen;
        Button buttonDinnerClose;
        ArrayList<String> lunchOpenTime = new ArrayList<String>();
        ArrayList<String> lunchCloseTime = new ArrayList<String>();
        ArrayList<String> dinnerOpenTime = new ArrayList<String>();
        ArrayList<String> dinnerCloseTime = new ArrayList<String>();
        ArrayList<Boolean> listLunchClose = new ArrayList<Boolean>();
        ArrayList<Boolean> listDinnerClose = new ArrayList<Boolean>();
        boolean allDays=false;
        int selectedDay = 0;


        public FragmentServices() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static FragmentServices newInstance(int sectionNumber) {
            FragmentServices fragment = new FragmentServices();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_add_restaurant_services, container, false);

            for(int i=0;i<8;i++){
                lunchOpenTime.add(i,"SET");
                lunchCloseTime.add(i,"SET");
                dinnerOpenTime.add(i,"SET");
                dinnerCloseTime.add(i,"SET");
                listLunchClose.add(i, false);
                listDinnerClose.add(i, false);
            }

            final CheckBox cbLunchClosed = (CheckBox) rootView.findViewById(R.id.checkBoxLunchClose);
            final CheckBox cbDinnerClosed = (CheckBox) rootView.findViewById(R.id.checkBoxDinnerClose);

            buttonLunchOpen = (Button) rootView.findViewById(R.id.buttonLunchOpen);
            buttonLunchOpen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogFragment newFragment = new TimePickerFragment() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String time = hourOfDay + ":" + minute;
                            if(allDays)
                                for(int i=0;i<8;i++)
                                    lunchOpenTime.set(i,time);
                            else
                                lunchOpenTime.set(selectedDay,time);
                            buttonLunchOpen.setText(time);
                        }
                    };
                    newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
                }
            });

            buttonLunchClose = (Button) rootView.findViewById(R.id.buttonLunchClose);
            buttonLunchClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogFragment newFragment = new TimePickerFragment() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String time = hourOfDay + ":" + minute;
                            if(allDays)
                                for(int i=0;i<8;i++)
                                    lunchCloseTime.set(i,time);
                            else
                                lunchCloseTime.set(selectedDay,time);
                            buttonLunchClose.setText(time);
                        }
                    };
                    newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
                }
            });

            buttonDinnerOpen = (Button) rootView.findViewById(R.id.buttonDinnerOpen);
            buttonDinnerOpen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogFragment newFragment = new TimePickerFragment() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                            String time = hourOfDay + ":" + minute;
                            if(allDays)
                                for(int i=0;i<8;i++)
                                    dinnerOpenTime.set(i,time);
                            else
                                dinnerOpenTime.set(selectedDay,time);
                            buttonDinnerOpen.setText(time);

                        }
                    };
                    newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
                }
            });

            buttonDinnerClose = (Button) rootView.findViewById(R.id.buttonDinnerClose);
            buttonDinnerClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogFragment newFragment = new TimePickerFragment() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String time = hourOfDay + ":" + minute;
                            if(allDays)
                                for(int i=0;i<8;i++)
                                    dinnerCloseTime.set(i,time);
                            else
                                dinnerCloseTime.set(selectedDay,time);
                            buttonDinnerClose.setText(time);
                        }
                    };
                    newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
                }
            });


            cbLunchClosed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((CheckBox) v).isChecked()) {
                        lunchOpenTime.set(selectedDay,"CLOSED");
                        lunchCloseTime.set(selectedDay,"CLOSED");
                        buttonLunchOpen.setText("CLOSED");
                        buttonLunchOpen.setEnabled(false);
                        buttonLunchClose.setText("CLOSED");
                        buttonLunchClose.setEnabled(false);
                        listLunchClose.set(selectedDay, true);
                    }
                    else{
                        lunchOpenTime.set(selectedDay,"SET");
                        lunchCloseTime.set(selectedDay,"SET");
                        buttonLunchOpen.setText("SET");
                        buttonLunchOpen.setEnabled(true);
                        buttonLunchClose.setText("SET");
                        buttonLunchClose.setEnabled(true);
                        listLunchClose.set(selectedDay, false);
                    }
                }
            });

            cbDinnerClosed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((CheckBox) v).isChecked()) {
                        dinnerOpenTime.set(selectedDay,"CLOSED");
                        dinnerCloseTime.set(selectedDay,"CLOSED");
                        buttonDinnerOpen.setText("CLOSED");
                        buttonDinnerOpen.setEnabled(false);
                        buttonDinnerClose.setText("CLOSED");
                        buttonDinnerClose.setEnabled(false);
                        listDinnerClose.set(selectedDay, true);
                    }
                    else{
                        dinnerOpenTime.set(selectedDay,"SET");
                        dinnerCloseTime.set(selectedDay,"SET");
                        buttonDinnerOpen.setText("SET");
                        buttonDinnerOpen.setEnabled(true);
                        buttonDinnerClose.setText("SET");
                        buttonDinnerClose.setEnabled(true);
                        listDinnerClose.set(selectedDay, false);
                    }
                }
            });


            final Spinner day = (Spinner) rootView.findViewById(R.id.spinnerDays);

            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                    R.array.week_days_array, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            day.setAdapter(adapter);
            selectedDay = day.getSelectedItemPosition();

            day.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position==7){
                        allDays=true;
                        cbLunchClosed.setVisibility(CheckBox.INVISIBLE);
                        cbDinnerClosed.setVisibility(CheckBox.INVISIBLE);
                    }
                    else {
                        allDays = false;
                        cbLunchClosed.setVisibility(CheckBox.VISIBLE);
                        cbLunchClosed.setChecked(listLunchClose.get(position));
                        cbDinnerClosed.setVisibility(CheckBox.VISIBLE);
                        cbDinnerClosed.setChecked(listDinnerClose.get(position));

                    }
                    selectedDay = position;
                    buttonLunchOpen.setEnabled(!listLunchClose.get(position));
                    buttonLunchClose.setEnabled(!listLunchClose.get(position));
                    buttonDinnerOpen.setEnabled(!listDinnerClose.get(position));
                    buttonDinnerClose.setEnabled(!listDinnerClose.get(position));
                    buttonLunchOpen.setText(lunchOpenTime.get(selectedDay));
                    buttonLunchClose.setText(lunchCloseTime.get(selectedDay));
                    buttonDinnerOpen.setText(dinnerOpenTime.get(selectedDay));
                    buttonDinnerClose.setText(dinnerCloseTime.get(selectedDay));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            return rootView;
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            buttonLunchOpen.setText(hourOfDay + minute);
        }
    }

    public static class FragmentExtras extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public FragmentExtras() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static FragmentExtras newInstance(int sectionNumber) {
            FragmentExtras fragment = new FragmentExtras();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_add_restaurant_extras, container, false);
            return rootView;
        }
    }


    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position==0)
                return FragmentInfo.newInstance(position + 1);
            if(position==1)
                return FragmentServices.newInstance(position + 1);
            else
                return FragmentExtras.newInstance(position + 1);
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
