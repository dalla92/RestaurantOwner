package it.polito.group2.restaurantowner;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
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

import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class MenuRestaurant_edit extends AppCompatActivity {

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

    static AppBarLayout appbar;

    private ArrayList<String> parentItem = new ArrayList<String>();
    private ArrayList<Addition> childItems = new ArrayList<Addition>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_restaurant_edit);

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appbar = (AppBarLayout) findViewById(R.id.appbar);

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
            //scroll behaviour
            final AppBarLayout appbar = (AppBarLayout) getActivity().findViewById(R.id.appbar);
            ScrollView scroll = (ScrollView) rootView.findViewById(R.id.parent_scroll);
            scroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    appbar.setExpanded(false);
                }
            });
            //feed spinner
            Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner3);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
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
        ArrayList<Addition> additionList;
        ArrayList<Addition> categoryList;
        private Context context;
        int index_addition;
        int index_category;
        View rootView;
        private String parentAddition;
        private String parentCategory;
        private ArrayList<Addition> childAdditions = new ArrayList<Addition>();
        private ArrayList<Addition> childCategories = new ArrayList<Addition>();

        private static final String ARG_SECTION_NUMBER = "section_number";
        ExpandableListView additions;
        ExpandableListView categories;


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
            rootView = inflater.inflate(R.layout.fragment_other_info_meal, container, false);
            this.context = rootView.getContext();
            //numberPicker implementation
            NumberPicker np = (NumberPicker) rootView.findViewById(R.id.numberPicker);
            np.setMinValue(1);
            np.setMaxValue(15);
            np.setWrapSelectorWheel(false);
            np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    //
                }
            });
            /*
            //scroll behaviour
            ScrollView scroll = (ScrollView) rootView.findViewById(R.id.parent_scroll);
            scroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    appbar.setExpanded(false);
                }
            });
            */
            /*
            //list view of additions
            // Defined Array values to show in ListView
            additionList = new ArrayList<Addition>();
            Addition a = new Addition("carne", true);
            additionList.add(a);
            a = new Addition("equino", false);
            additionList.add(a);
            a = new Addition("bovino", true);
            additionList.add(a);
            a = new Addition("suino", true);
            additionList.add(a);
            //ArrayList<Addition> additions_lst = new ArrayList<Addition>(Arrays.asList(additionList));
            // Define a new Adapter: Context, Layout for the row, ID of the TextView to which the data is written, Array of data
            additions_adapter = new MyCustomAdapterAddition(rootView.getContext(),
                    R.layout.fragment_other_info_meal, additionList);
            final ListView listadditionView = (ListView) rootView.findViewById(R.id.additions_list);
            // Assign adapter to ListView
            listadditionView.setAdapter(additions_adapter);
             //list view of categories
            // Defined Array values to show in ListView
            final ArrayList<Addition> categoryList = new ArrayList<Addition>();
            Addition b = new Addition("tonno", true);
            categoryList.add(b);
            b = new Addition("parmigiano", false);
            categoryList.add(b);
            b = new Addition("rucola", true);
            categoryList.add(b);
            b = new Addition("origano", true);
            categoryList.add(b);
            b = new Addition("patatine", false);
            categoryList.add(b);
            //ArrayList<Addition> categories_lst = new ArrayList<Addition>(Arrays.asList(categoryList));
            // Define a new Adapter: Context, Layout for the row, ID of the TextView to which the data is written, Array of data
            categories_adapter = new MyCustomAdapterCategory(rootView.getContext(),
                    R.layout.fragment_other_info_meal, categoryList);
            final ListView listcategoryView = (ListView) rootView.findViewById(R.id.categories_list);
            // Assign adapter to ListView
            listcategoryView.setAdapter(categories_adapter);
            */

            /*
            //one expandable addition list for both lists implementation
            ExpandableListView expandableList = (ExpandableListView) rootView.findViewById(R.id.additions_and_categories_list); // you can use (ExpandableListView) findViewById(R.id.list)
            expandableList.setDividerHeight(2);
            expandableList.setGroupIndicator(null);
            expandableList.setClickable(true);
            setGroupParents();
            setChildData();
            expandableList.setAdapter(new MyExpandableAdapter(parentItem, childItems));
            MyExpandableAdapter adapter = new MyExpandableAdapter(parentItem, childItems);
            adapter.setInflater((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE), rootView.appl); //second parameter was this
            //adapter.setInflater((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE), getActivity()); //second parameter was this
            //adapter.setInflater((LayoutInflater) rootView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE), getActivity()); //second parameter was this
            //adapter.setInflater((LayoutInflater) rootView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE), getActivity()); //second parameter was this
            expandableList.setAdapter(adapter);
            //expandableList.setOnChildClickListener(this);
            */

            //expandable additions
            //setGroupParents(parentAddition);

            parentAddition = "Meal additions";
            //setChildData(childAdditions);
            childAdditions.add(new Addition("equino", false));
            childAdditions.add(new Addition("bovino", false));
            childAdditions.add(new Addition("suino", false));
            additions = (ExpandableListView) rootView.findViewById(R.id.additions_list);
            additions.setAdapter(new MyExpandableAdapter(parentAddition, childAdditions));
            additions.setDividerHeight(5);
            additions.setGroupIndicator(null);
            additions.setClickable(true);

            //expandable categories
            //setGroupParents(parentAddition);
            parentCategory = "Meal categories";
            //setChildData(childAdditions);
            childCategories.add(new Addition("rucola", false));
            childCategories.add(new Addition("parmigiano", false));
            childCategories.add(new Addition("tonno", false));
            categories = (ExpandableListView) rootView.findViewById(R.id.categories_list);
            categories.setAdapter(new MyExpandableAdapter(parentCategory, childCategories));
            //categories.setDividerHeight(2);
            categories.setGroupIndicator(null);
            categories.setClickable(true);

            return rootView;
        }

         public class MyExpandableAdapter extends BaseExpandableListAdapter {

            private Activity activity;
            private ArrayList<Addition> childItems;
            private LayoutInflater inflater;
            private String parentItem;
            //private ArrayList<String> child;

            public MyExpandableAdapter(String parent, ArrayList<Addition> children) {
                this.parentItem = parent;
                this.childItems = children;
                inflater = LayoutInflater.from(getActivity());
            }

            @Override
            public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                //child = (ArrayList<String>) childItems.get(groupPosition);
                CheckBox checkbox_text = null;
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.possible_additions_layout, null);
                }
                checkbox_text = (CheckBox) convertView.findViewById(R.id.meal_addition);
                checkbox_text.setText(childItems.get(childPosition).getName());
                //CheckBox parent_node = (CheckBox) parent.findViewById(R.id.meal_addition);
                //String parent_node_text = parent_node.getText().toString();
                //Log.d("parent_node", parent_node_text);
                convertView.findViewById(R.id.addition_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //get the row the clicked button is in
                        //getParent().getParentChildren().get(0).
                        childItems.remove(childPosition);
                        notifyDataSetChanged();
                        notifyDataSetInvalidated();
                        Log.d("myClickHandlerDelete", "You want to delete");
                    }
                });
            /*
            if(parent_node_text.equals("Meal additions")) {
                convertView.findViewById(R.id.addition_edit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //get the row the clicked button is in
                        LinearLayout vwParentRow = (LinearLayout) v.getParent();
                        TextView child = (TextView) vwParentRow.getChildAt(2);
                        String addition_name = child.getText().toString();
                        Log.d("myClickHandlerAddition", "You want to modify " + addition_name);
                        Intent intent = new Intent(
                                rootView.getContext(),
                                MenuRestaurant_edit.class);
                        startActivity(intent);
                    }
                });
                convertView.findViewById(R.id.addition_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //get the row the clicked button is in
                        LinearLayout vwParentRow = (LinearLayout) v.getParent();
                        CheckBox child = (CheckBox) vwParentRow.getChildAt(1);
                        final String addition_name = child.getText().toString();
                        Log.d("myClickHandlerAddition", "You want to delete " + addition_name);
                        for (index_addition = 0; index_addition < additionList.size(); index_addition++) {
                            if (additionList.get(index_addition).getName().equals(addition_name))
                                break;
                        }
                        //dialog box
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                                /*View parentRow = (View) v.getParent();
                                                ListView listView = (ListView) parentRow.getParent();
                                                int position = listView.getPositionForView(parentRow);
                                                Addition toRemove = adapter.getItem(position);
                                        Log.d("myClickHandlerAddition", "You are removing " + additionList.get(index_addition).getName());
                                        ( (MyExpandableAdapter)lv.getParent().get(0) ).getParentChildren().add(childre.get(index_addition));
                                        additions_adapter.remove(additionList.get(index_addition));
                                        additions_adapter.notifyDataSetChanged();
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setMessage("Are you sure that you want to delete " + addition_name + "?").setPositiveButton("Yes, sure", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    }
                });
            }
        else if(parent_node_text.equals("Meal categories")){{
                    convertView.findViewById(R.id.category_edit).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //get the row the clicked button is in
                            LinearLayout vwParentRow = (LinearLayout) v.getParent();
                            CheckBox child = (CheckBox) vwParentRow.getChildAt(2);
                            String category_name = child.getText().toString();
                            Log.d("myClickHandlerCategory", "You want to modify " + category_name);
                            Intent intent = new Intent(
                                    rootView.getContext(),
                                    MenuRestaurant_edit.class);
                            startActivity(intent);
                        }
                    });
                    convertView.findViewById(R.id.category_delete).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //get the row the clicked button is in
                            LinearLayout vwParentRow = (LinearLayout) v.getParent();
                            CheckBox child = (CheckBox) vwParentRow.getChildAt(1);
                            final String category_name = child.getText().toString();
                            Log.d("myClickHandlerCategory", "You want to delete " + category_name);
                            for (index_category = 0; index_category < categoryList.size(); index_category++) {
                                if (categoryList.get(index_category).getName().equals(category_name))
                                    break;
                            }
                            //dialog box
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                        /*View parentRow = (View) v.getParent();
                        ListView listView = (ListView) parentRow.getParent();
                        int position = listView.getPositionForView(parentRow);
                        Addition toRemove = adapter.getItem(position);
                                            Log.d("myClickHandlerCategory", "You are removing " + categoryList.get(index_category).getName());
                                            categories_adapter.remove(categoryList.get(index_category));
                                            categories_adapter.notifyDataSetChanged();
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            break;
                                    }
                                }
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                            builder.setMessage("Are you sure that you want to delete " + category_name + "?").setPositiveButton("Yes, sure", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();
                        }
                    });

                }
                }
                */

                return convertView;
            }

            @Override
            public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    convertView = inflater.inflate
                            (R.layout.row, null);
                }

                ((CheckedTextView) convertView).setText(parentItem);
                ((CheckedTextView) convertView).setChecked(isExpanded);

                return convertView;
            }

            @Override
            public Addition getChild(int groupPosition, int childPosition) {
                return null;
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                return 0;
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                //return ((ArrayList<Addition>) childItems.get(groupPosition)).size();
                return childItems.size();
            }

            @Override
            public Addition getGroup(int groupPosition) {
                return null;
            }

            @Override
            public int getGroupCount() {
                return 1;
            }

            @Override
            public void onGroupCollapsed(int groupPosition) {
                super.onGroupCollapsed(groupPosition);
            }

            @Override
            public void onGroupExpanded(int groupPosition) {
                super.onGroupExpanded(groupPosition);
            }

            @Override
            public long getGroupId(int groupPosition) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public boolean isChildSelectable(int groupPosition, int childPosition) {
                return false;
            }

        }

    }


        /*
        //additions part
        public void myClickHandler_add_addition(View v) {
            //get the row the clicked button is in
            Log.d("myClickHandler", "You want to add a new addition");
            Addition a = new Addition("New empty addition", true);
            additions_adapter.insert(a, 0); //insert at the top
            additions_adapter.notifyDataSetChanged();
        }

       //categories part
        public void myClickHandler_add_category(View v) {
            //get the row the clicked button is in
            Log.d("myClickHandler", "You want to add a new category");
            Addition a = new Addition("New empty category", true);
            categories_adapter.insert(a, 0); //insert at the top
            categories_adapter.notifyDataSetChanged();
        }
    */
////////////////////////FragmentOtherInfo//////////////////////////////////////////


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
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

