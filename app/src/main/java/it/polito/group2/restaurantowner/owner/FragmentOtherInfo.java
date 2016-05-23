package it.polito.group2.restaurantowner.owner;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.UUID;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.MealAddition;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.MealCategory;

/**
 * Created by Alessio on 16/04/2016.
 */
public class FragmentOtherInfo extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    String selectedCategory = null;
    onOtherInfoPass dataPasser;
    //ArrayList<MealAddition> additions = new ArrayList<MealAddition>();
    //ArrayList<MealAddition> categories = new ArrayList<MealAddition>();
    public View rootView = null;
    private EditText meal_description;
    private NumberPicker cooking_time;
    public int time = 0;
    public static String parentAddition;
    public static String parentCategory;
    public static ArrayList<MealAddition> childMealAdditions = new ArrayList<MealAddition>();
    public static ArrayList<MealAddition> childCategories = new ArrayList<MealAddition>();
    public static MyExpandableAdapter additions_adapter;
    public static MyExpandableAdapter categories_adapter;
    public static ExpandableListView additions;
    public static ExpandableListView categories;
    public static Context context;
    public static String meal_id;
    public static Meal current_meal;
    public static FragmentOtherInfo fragment;

    public FragmentOtherInfo() {
    }

    public static FragmentOtherInfo newInstance(Meal m, Context c) {
        fragment = new FragmentOtherInfo();
        Bundle args = new Bundle();
        args.putString("meal_description", m.getMeal_description());
        args.putInt("cooking_time", m.getMeal_cooking_time());
        context = c;
        current_meal = m;
        meal_id = m.getMeal_id();
        /*
        try{
            readJSONMeList();
        }
        catch(JSONException e){
            Log.d("aaa", "Exception in reading");
        }
        */
        fragment.setArguments(args);
        return fragment;
    }

    /*
    public void onSaveInstanceState(Bundle outState){
        getFragmentManager().putFragment(outState,"myfragment",fragment);
    }
    public void onRestoreInstanceState(Bundle inState){
        fragment = (FragmentOtherInfo) getFragmentManager().getFragment(inState,"myfragment");
    }
    */

    public void passData() {
        ArrayList<MealCategory> childMealCategories = new ArrayList<MealCategory>();
        for(MealAddition ma : childCategories){
            MealCategory mc = new MealCategory();
            mc.setMeal_category_id(ma.getMeal_addition_id());
            mc.setMeal_category_name(ma.getMeal_addition_name());
            childMealCategories.add(mc);
        }
        dataPasser.onOtherInfoPass(meal_description.getText().toString(), time, childMealAdditions, childMealCategories);
    }

    public interface onOtherInfoPass {
        public void onOtherInfoPass(String meal_description, int cooking_time, ArrayList<MealAddition> mealAdditions, ArrayList<MealCategory> categories);
    }


    @Override
    public void onAttach(Activity a) {
        super.onAttach(a);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            dataPasser = (onOtherInfoPass) a;
        } catch (ClassCastException e) {
            throw new ClassCastException(a.toString()
                    + " must implement onOtherInfoPass");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_other_info_meal, container, false);

        meal_description = (EditText) rootView.findViewById(R.id.edit_meal_description);
        NumberPicker np = (NumberPicker) rootView.findViewById(R.id.numberPicker);
        np.setMinValue(1);
        np.setMaxValue(30);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                time = newVal;
            }
        });
        //expandable additions
        childMealAdditions = current_meal.getMeal_additions();
        parentAddition = "Meal additions";
        additions = (ExpandableListView) rootView.findViewById(R.id.additions_list);
        additions_adapter = new MyExpandableAdapter(parentAddition, childMealAdditions);
        additions.setAdapter(additions_adapter);
        additions.setDividerHeight(5);
        additions.setGroupIndicator(null);
        additions.setClickable(true);
        //expandable categorie
        if(current_meal.getMeal_tags()!=null)
            for(MealCategory mc : current_meal.getMeal_tags()){
                MealAddition ma = new MealAddition();
                ma.setMeal_addition_name(mc.getMeal_category_name());
                childCategories.add(ma);
            }
        parentCategory = "Meal categories";
        categories = (ExpandableListView) rootView.findViewById(R.id.categories_list);
        categories_adapter = new MyExpandableAdapter(parentCategory, childCategories);
        categories.setAdapter(categories_adapter);
        categories.setDividerHeight(5);
        categories.setGroupIndicator(null);
        categories.setClickable(true);

        final ImageView addition_add_button = (ImageView) rootView.findViewById(R.id.addition_add);
        addition_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                childMealAdditions = additions_adapter.getChildItems();
                MealAddition ma = new MealAddition();
                ma.setMeal_addition_name(getResources().getText(R.string.meal_add_new_addition).toString());
                ma.setMeal_addition_price(0.0);
                ma.setAdditionSelected(false);
                ma.setMeal_addition_id(UUID.randomUUID().toString());
                if(childMealAdditions==null)
                    childMealAdditions = new ArrayList<MealAddition>();
                childMealAdditions.add(ma);
                additions_adapter.setChildItems(childMealAdditions);
                additions.expandGroup(0);

            }
        });
        final ImageView category_add_button = (ImageView) rootView.findViewById(R.id.category_add);
        category_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                childCategories = categories_adapter.getChildItems();
                MealAddition ma = new MealAddition();
                ma.setMeal_addition_name(getResources().getText(R.string.meal_add_new_category).toString());
                ma.setMeal_addition_id(UUID.randomUUID().toString());
                childCategories.add(ma);
                categories_adapter.setChildItems(childCategories);
                categories.expandGroup(0);
            }
        });

        if (!getArguments().isEmpty()) {
            if(getArguments().getString("meal_description")!=null)
                meal_description.setText(getArguments().getString("meal_description"));
            if (getArguments().getInt("cooking_time")!=0)
                np.setValue(getArguments().getInt("cooking_time"));
        }

        return rootView;
    }



    public class MyExpandableAdapter extends BaseExpandableListAdapter {
        private Activity activity;
        private ArrayList<MealAddition> childItems;
        private LayoutInflater inflater;
        private String parentItem;
        //private ArrayList<String> child;
        EditText userInput_price;
        View promptsView;

        public MyExpandableAdapter(String parent, ArrayList<MealAddition> children) {
            this.parentItem = parent;
            this.childItems = children;
            inflater = LayoutInflater.from(context);
        }

        public ArrayList<MealAddition> getChildItems(){
            return this.childItems;
        }

        public void setChildItems(ArrayList<MealAddition> new_list){
            this.childItems = new_list;
            notifyDataSetChanged();
            notifyDataSetInvalidated();
        }
        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            //child = (ArrayList<String>) childItems.get(groupPosition);
            CheckBox checkbox_text = null;
            TextView textview = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.possible_additions_layout, null);
            }
            if(parentItem.equals("Meal categories")){
                convertView.findViewById(R.id.edit_addition_price).setVisibility(View.INVISIBLE);
            }
            checkbox_text = (CheckBox) convertView.findViewById(R.id.meal_addition);
            checkbox_text.setText(childItems.get(childPosition).getMeal_addition_name());
            if(parentItem.equals("Meal additions")) {
                textview = (TextView) convertView.findViewById(R.id.edit_addition_price);
                textview.setText( String.valueOf(childItems.get(childPosition).getMeal_addition_price()));
            }
            convertView.findViewById(R.id.addition_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //get the row the clicked button is in
                    //getParent().getParentChildren().get(0).
                    childItems.remove(childPosition);
                    notifyDataSetChanged();
                    notifyDataSetInvalidated();
                    /*
                        if (parentItem.equals("Meal additions"))
                            current_meal.setMeal_Meal_additions(childItems);
                        else
                            current_meal.setMeal_categories(childItems);
                    */
                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater li = LayoutInflater.from(context);
                    promptsView = null;
                    if (parentItem.equals("Meal additions")) {
                        promptsView = li.inflate(R.layout.addition_prompt, null);
                    } else {
                        promptsView = li.inflate(R.layout.category_prompt, null);
                    }
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            context);
                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);
                    final EditText userInput_name = (EditText) promptsView
                            .findViewById(R.id.new_name);
                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            childItems.get(childPosition).setMeal_addition_name(userInput_name.getText().toString());
                                            if (parentItem.equals("Meal additions")) {
                                                userInput_price = (EditText) promptsView
                                                        .findViewById(R.id.new_price);
                                                if(!userInput_price.getText().toString().trim().equals(""))
                                                    childItems.get(childPosition).setMeal_addition_price(Double.parseDouble(userInput_price.getText().toString()));
                                            }
                                            /*
                                                if (parentItem.equals("Meal additions"))
                                                    current_meal.setMeal_Meal_additions(childItems);
                                                else
                                                    current_meal.setMeal_categories(childItems);
                                            */
                                            notifyDataSetChanged();
                                            notifyDataSetInvalidated();
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                }
            });

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
        public MealAddition getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            //return ((ArrayList<MealAddition>) childItems.get(groupPosition)).size();
            if(childItems!=null)
                return childItems.size();
            else
                return 0;
        }

        @Override
        public MealAddition getGroup(int groupPosition) {
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