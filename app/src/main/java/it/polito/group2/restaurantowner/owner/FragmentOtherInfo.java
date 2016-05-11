package it.polito.group2.restaurantowner.owner;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.MealAddition;
import it.polito.group2.restaurantowner.data.Meal;

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
        args.putString("meal_description", m.getDescription());
        args.putInt("cooking_time", m.getCooking_time());
        context = c;
        current_meal = m;
        meal_id = m.getMealId();
        try{
            readJSONMeList();
        }
        catch(JSONException e){
            Log.d("aaa", "Exception in reading");
        }
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
        dataPasser.onOtherInfoPass(meal_description.getText().toString(), time, childMealAdditions, childCategories);
    }

    public interface onOtherInfoPass {
        public void onOtherInfoPass(String meal_description, int cooking_time, ArrayList<MealAddition> mealAdditions, ArrayList<MealAddition> categories);
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
        childMealAdditions = current_meal.getMeal_Meal_additions();
        parentAddition = "Meal additions";
        additions = (ExpandableListView) rootView.findViewById(R.id.additions_list);
        additions_adapter = new MyExpandableAdapter(parentAddition, childMealAdditions);
        additions.setAdapter(additions_adapter);
        additions.setDividerHeight(5);
        additions.setGroupIndicator(null);
        additions.setClickable(true);
        //expandable categories
        childCategories = current_meal.getMeal_categories();
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
                childMealAdditions.add(new MealAddition(current_meal.getRestaurantId(),current_meal.getMealId(), getResources().getText(R.string.meal_add_new_addition).toString(), 0, false));
                additions_adapter.setChildItems(childMealAdditions);
                additions.expandGroup(0);

            }
        });
        final ImageView category_add_button = (ImageView) rootView.findViewById(R.id.category_add);
        category_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                childCategories = categories_adapter.getChildItems();
                childCategories.add(new MealAddition(current_meal.getRestaurantId(),current_meal.getMealId(),getResources().getText(R.string.meal_add_new_category).toString(), 0, false));
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
            checkbox_text.setText(childItems.get(childPosition).getName());
            if(parentItem.equals("Meal additions")) {
                textview = (TextView) convertView.findViewById(R.id.edit_addition_price);
                textview.setText( String.valueOf(childItems.get(childPosition).getPrice()));
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
                                            childItems.get(childPosition).setName(userInput_name.getText().toString());
                                            if (parentItem.equals("Meal additions")) {
                                                userInput_price = (EditText) promptsView
                                                        .findViewById(R.id.new_price);
                                                if(!userInput_price.getText().toString().trim().equals(""))
                                                    childItems.get(childPosition).setPrice(Double.parseDouble(userInput_price.getText().toString()));
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
            return childItems.size();
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

    public static void readJSONMeList() throws JSONException {
        /*
        Log.d("aaa", "CALLED READ");
        //mealList.json
        meals = new ArrayList<>();
        meals_additions = new ArrayList<>();
        meals_categories = new ArrayList<>();
        String json = null;
        FileInputStream fis = null;
        String FILENAME = "mealList.json";
        try {
            fis = context.openFileInput(FILENAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            json = new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jobj = new JSONObject(json);
        JSONArray jsonArray = jobj.optJSONArray("Meals");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Meal me = new Meal();
            Log.d("aaa", "TRYING TO READ ONE MEAL");
            me.setRestaurantId(jsonObject.optString("RestaurantId"));
            me.setMealId(jsonObject.optString("MealId"));
            me.setMeal_photo(jsonObject.optString("MealPhoto"));
            me.setMeal_name(jsonObject.optString("MealName"));
            me.setMeal_price(jsonObject.optDouble("MealPrice"));
            me.setType1(jsonObject.optString("MealType1"));
            me.setType2(jsonObject.optString("MealType2"));
            me.setAvailable(jsonObject.getBoolean("MealAvailable"));
            me.setTake_away(jsonObject.getBoolean("MealTakeAway"));
            me.setCooking_time(jsonObject.optInt("MealCookingTime"));
            me.setDescription(jsonObject.getString("MealDescription"));
            if(me.getRestaurantId().equals(restaurant_id))
                meals.add(me);
        }
        */
        childMealAdditions = new ArrayList<>();
        childCategories = new ArrayList<>();
        //mealAdditions.json
        String json2 = null;
        FileInputStream fis2 = null;
        String FILENAME2 = "mealAddition.json";
        try {
            fis2 = context.openFileInput(FILENAME2);
            int size2 = fis2.available();
            byte[] buffer2 = new byte[size2];
            fis2.read(buffer2);
            fis2.close();
            json2 = new String(buffer2, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jobj2 = new JSONObject(json2);
        JSONArray jsonArray2 = jobj2.optJSONArray("MealsAdditions");
        for (int i = 0; i < jsonArray2.length(); i++) {
            JSONObject jsonObject2 = jsonArray2.getJSONObject(i);
            MealAddition ad = new MealAddition();
            if (jsonObject2.optString("MealId").equals(meal_id)) {
                ad.setRestaurant_id(jsonObject2.optString("RestaurantId"));
                ad.setmeal_id(jsonObject2.optString("MealId"));
                ad.setName(jsonObject2.optString("AdditionName"));
                ad.setSelected(jsonObject2.getBoolean("AdditionSelected"));
                ad.setPrice(jsonObject2.optDouble("AdditionPrice"));
                childMealAdditions.add(ad);
            }
        }
        //mealCategories.json
        String json3 = null;
        FileInputStream fis3 = null;
        String FILENAME3 = "mealCategory.json";
        try {
            fis3 = context.openFileInput(FILENAME3);
            int size3 = fis3.available();
            byte[] buffer3 = new byte[size3];
            fis3.read(buffer3);
            fis3.close();
            json3 = new String(buffer3, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jobj3 = new JSONObject(json3);
        JSONArray jsonArray3 = jobj3.optJSONArray("MealsCategories");
        for (int i = 0; i < jsonArray3.length(); i++) {
            JSONObject jsonObject3 = jsonArray3.getJSONObject(i);
            MealAddition ad = new MealAddition();
            if (jsonObject3.optString("MealId").equals(meal_id)) {
                ad.setRestaurant_id(jsonObject3.optString("RestaurantId"));
                ad.setmeal_id(jsonObject3.optString("MealId"));
                ad.setName(jsonObject3.optString("CategoryName"));
                ad.setSelected(jsonObject3.getBoolean("CategorySelected"));
                ad.setPrice(0);
                //ad.setPrice(jsonObject3.optDouble("CategoryPrice"));
                childCategories.add(ad);
            }
        }
    }
}