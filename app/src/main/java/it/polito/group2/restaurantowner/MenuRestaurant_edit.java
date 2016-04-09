package it.polito.group2.restaurantowner;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
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
import android.util.Log;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class MenuRestaurant_edit extends AppCompatActivity{

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
        setContentView(R.layout.activity_menu_restaurant_edit);

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //swipe views
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

////////////////////////FragmentMainInfo///////////////////////////////////////////
    public static class FragmentMainInfo extends Fragment implements AdapterView.OnItemSelectedListener {

        private static final String ARG_SECTION_NUMBER = "section_number";
        String selectedCategory = null;

        public FragmentMainInfo() {
        }

        public static FragmentMainInfo newInstance(int sectionNumber) { // Returns a new instance of this fragment for the given section number.
            FragmentMainInfo fragment = new FragmentMainInfo();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_info_meal, container, false);

            //feed spinner
            Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner3);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( getActivity(),
                    R.array.meal_types_array, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);

            return rootView;
        }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    }
////////////////////////FragmentMainInfo///////////////////////////////////////////



////////////////////////FragmentOtherInfo//////////////////////////////////////////
    public static class FragmentOtherInfo extends Fragment{

    private ListView additions_list_view;
    private ListView categories_list_view;
    private MyCustomAdapter categories_adapter;
    private MyCustomAdapter additions_adapter;

        private static final String ARG_SECTION_NUMBER = "section_number";
        public FragmentOtherInfo() {
        }

        public static FragmentOtherInfo newInstance(int sectionNumber) {
            FragmentOtherInfo fragment = new FragmentOtherInfo();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_other_info_meal, container, false);

            //list view of additions
            // Defined Array values to show in ListView
            ArrayList<Addition> additionList = new ArrayList<Addition>();
            Addition a = new Addition("carne",true);
            additionList.add(a);
            a = new Addition("equino",false);
            additionList.add(a);
            a = new Addition("bovino",true);
            additionList.add(a);
            a = new Addition("suino",true);
            additionList.add(a);
            //ArrayList<Addition> additions_lst = new ArrayList<Addition>(Arrays.asList(additionList));
            // Define a new Adapter: Context, Layout for the row, ID of the TextView to which the data is written, Array of data
            additions_adapter = new MyCustomAdapter(rootView.getContext(),
                    R.layout.fragment_other_info_meal, additionList);
            final ListView listadditionView = (ListView) rootView.findViewById(R.id.additions_list);
            // Assign adapter to ListView
            listadditionView.setAdapter(
                    additions_adapter);
            // ListView Item Click Listener
            listadditionView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // ListView Clicked item index
                    int itemPosition = position;
                    // ListView Clicked item value
                    String itemValue = (String) listadditionView.getItemAtPosition(position);
                    String meal_addition = itemValue;
                    //TextView textview = (TextView) view.findViewById(R.id.meal_name);
                    Log.d("debug_onclick", "You clicked " + meal_addition);
                    //Log.d("debug_onclick", "You want to modify " + meal_name + " at Position :" + itemPosition + "  ListItem : " + itemValue);
                }

            });


            //list view of categories
            // Defined Array values to show in ListView
            ArrayList<Addition> categoryList = new ArrayList<Addition>();
            Addition b = new Addition("tonno",true);
            categoryList.add(b);
            b = new Addition("parmigiano",false);
            categoryList.add(b);
            b = new Addition("rucola",true);
            categoryList.add(b);
            b = new Addition("origano",true);
            categoryList.add(b);
            b = new Addition("patatine",false);
            categoryList.add(b);
            //ArrayList<Addition> categories_lst = new ArrayList<Addition>(Arrays.asList(categoryList));
            // Define a new Adapter: Context, Layout for the row, ID of the TextView to which the data is written, Array of data
            categories_adapter = new MyCustomAdapter(rootView.getContext(),
                    R.layout.fragment_other_info_meal, categoryList);
            final ListView listcategoryView = (ListView) rootView.findViewById(R.id.categories_list);
            // Assign adapter to ListView
            listcategoryView.setAdapter(categories_adapter);
            // ListView Item Click Listener
            listcategoryView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // ListView Clicked item index
                    int itemPosition = position;
                    // ListView Clicked item value
                    String itemValue = (String) listcategoryView.getItemAtPosition(position);
                    //TextView textview = (TextView) view.findViewById(R.id.meal_name);
                    String meal_category = itemValue;
                    Log.d("debug_onclick", "You clicked " + meal_category);
                    //Log.d("debug_onclick", "You want to modify " + meal_name + " at Position :" + itemPosition + "  ListItem : " + itemValue);
                }

            });
            
            return rootView;
        }

    private class MyCustomAdapter extends ArrayAdapter<Addition> {
        private ArrayList<Addition> additionList;
        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Addition> additionList) {
            super(context, textViewResourceId, additionList);
            this.additionList = new ArrayList<Addition>();
            this.additionList.addAll(additionList);
        }
        private class ViewHolder {
            //TextView code;
            CheckBox name;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.possible_additions_layout, null);
                holder = new ViewHolder();
                //holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.name = (CheckBox) convertView.findViewById(R.id.meal_addition);
                convertView.setTag(holder);
                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        //Addition Addition = (Addition) cb.getTag();
                        Toast.makeText(getActivity(),
                                "Clicked on Checkbox: " + cb.getText() +
                                        " is " + cb.isChecked(),
                                Toast.LENGTH_LONG).show();
                        //Addition.setSelected(cb.isChecked());
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            Addition addition = additionList.get(position);
            //holder.code.setText(" (" +  Addition.getCode() + ")");
            holder.name.setText(addition.getName());
            holder.name.setChecked(addition.isSelected());
            holder.name.setTag(addition);
            return convertView;
        }
    }

}
////////////////////////FragmentOtherInfo//////////////////////////////////////////



    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0)
                return FragmentMainInfo.newInstance(position + 1);
            else
                return FragmentOtherInfo.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 2; // show 2 total pages
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.addres_section_main_info);
                case 1:
                    return getString(R.string.addres_section_other_info);
            }
            return null;
        }
    }



    /*
    private void checkButtonClick() {


        Button myButton = (Button) findViewById(R.id.findSelected);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");

                ArrayList<Addition> additionList = additions_adapter.additionList;
                for(int i=0;i<additionList.size();i++){
                    Addition Addition = additionList.get(i);
                    if(Addition.isSelected()){
                        responseText.append("\n" + Addition.getName());
                    }
                }

                Toast.makeText(getApplicationContext(),
                        responseText, Toast.LENGTH_LONG).show();

            }
        });

    }
    */

}
