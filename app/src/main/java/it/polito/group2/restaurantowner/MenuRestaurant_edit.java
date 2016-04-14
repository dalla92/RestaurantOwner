package it.polito.group2.restaurantowner;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class MenuRestaurant_edit extends AppCompatActivity {
    public SectionsPagerAdapter mSectionsPagerAdapter;
    public ViewPager mViewPager;
    public static AppBarLayout appbar;
    public ArrayList<String> parentItem = new ArrayList<String>();
    public ArrayList<Addition> childItems = new ArrayList<Addition>();
    public ListView additions_list_view;
    public ListView categories_list_view;
    public ArrayList<Addition> additionList;
    public ArrayList<Addition> categoryList;
    public static Context context;
    public int index_addition;
    public int index_category;
    public static View rootView;
    public static String parentAddition;
    public static String parentCategory;
    public static ArrayList<Addition> childAdditions = new ArrayList<Addition>();
    public static ArrayList<Addition> childCategories = new ArrayList<Addition>();
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static ExpandableListView additions;
    public static ExpandableListView categories;
    public static it.polito.group2.restaurantowner.MenuRestaurant_edit.FragmentOtherInfo.MyExpandableAdapter additions_adapter;
    public static it.polito.group2.restaurantowner.MenuRestaurant_edit.FragmentOtherInfo.MyExpandableAdapter categories_adapter;
    public static String restaurant_id = "0";
    public static String meal_name = "0";
    public static String photouri = null;
    public static int PICK_IMAGE = 0;
    public static int REQUEST_TAKE_PHOTO = 1;
    private static Set<Meal> meals_read = new HashSet<>();
    private static Set<Addition> meals_additions_read = new HashSet<>();
    private static Set<Addition> meals_categories_read = new HashSet<>();
    private static ArrayList<Meal> meals = new ArrayList<>();
    private static ArrayList<Addition> meals_additions = new ArrayList<>();
    private static ArrayList<Addition> meals_categories = new ArrayList<>();
    public static Meal current_meal = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_restaurant_edit);

        if (false){ //savedInstanceState != null) {
            //Restore the fragment's instance
            /*
            mSectionsPagerAdapter = getSupportFragmentManager().getFragment(savedInstanceState, "main_fragment");
            mSectionsPagerAdapter.setItem(1) = getSupportFragmentManager().getFragment(savedInstanceState, "other_fragment");
            */
        }
        else {
            //retrieve data
            Bundle b = getIntent().getExtras();
            meal_name = b.getString("meal_name");
            restaurant_id = b.getString("restaurant_id");
            try {
                readJSONMeList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            
            //retrieve current_meal
            for(Meal m : meals){
                if(m.getMeal_name().equals(meal_name)){
                    current_meal = m;
                    break;
                }
            }
            
            //toolbar
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            appbar = (AppBarLayout) findViewById(R.id.appbar);

            //swipe views
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_restaurant, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState, "main_fragment", mSectionsPagerAdapter.getItem(0));
        getSupportFragmentManager().putFragment(outState, "other_fragment", mSectionsPagerAdapter.getItem(1));
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            saveJSONMeList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            readJSONMeList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //take a photo result
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //view photo
            ImageView image = (ImageView) findViewById(R.id.imageView);
            setPic(image);
            //add photo to gallery
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(photouri); //here is passed the mCurrentPhotoPath
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        }
        //choose a photo result
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            photouri = imageUri.toString();
            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(imageUri);
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageBitmap(BitmapFactory.decodeStream(imageStream));
            } catch (FileNotFoundException e) {
                // Handle the error
            } finally {
                if (imageStream != null) {
                    try {
                        imageStream.close();
                    } catch (IOException e) {
                        // Ignore the exception
                    }
                }
            }
            current_meal.setMeal_photo(photouri);
            try{
                saveJSONMeList();
            }
            catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    private void setPic(ImageView mImageView) {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photouri, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        // Determine how much to scale down the image
        int scaleFactor = 1;
        if(targetW!=0 && targetH!=0) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(photouri, bmOptions);
        mImageView.setImageBitmap(bitmap);
        current_meal.setMeal_photo(photouri);
        try{
            saveJSONMeList();
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

    public void readJSONMeList() throws JSONException {
        //mealList.json
        meals_read = new HashSet<>();
        meals_additions_read = new HashSet<>();
        meals_categories_read = new HashSet<>();
        Log.d("ccc", "CALLED READ");
        String json = null;
        FileInputStream fis = null;
        String FILENAME = "mealList.json";
        try {
            fis = openFileInput(FILENAME);
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
            Log.d("ccc", "READ");
            if(me.getRestaurantId().equals(restaurant_id))
                meals_read.add(me);
        }
        //mealAdditions.json
        String json2 = null;
        FileInputStream fis2 = null;
        String FILENAME2 = "mealAddition.json";
        try {
            fis2 = openFileInput(FILENAME2);
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
            Addition ad = new Addition();
            if (jsonObject2.optString("RestaurantId").equals(restaurant_id)) {
                ad.setRestaurant_id(jsonObject2.optString("RestaurantId"));
                ad.setMeal_id(jsonObject2.optString("MealId"));
                ad.setName(jsonObject2.optString("AdditionName"));
                ad.setSelected(jsonObject2.getBoolean("AdditionSelected"));
                ad.setPrice(jsonObject2.optDouble("AdditionPrice"));
                meals_additions_read.add(ad);
            }
        }
        //mealCategories.json
        String json3 = null;
        FileInputStream fis3 = null;
        String FILENAME3 = "mealCategory.json";
        try {
            fis3 = openFileInput(FILENAME3);
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
            Addition ad = new Addition();
            if (jsonObject3.optString("RestaurantId").equals(restaurant_id)) {
                ad.setRestaurant_id(jsonObject3.optString("RestaurantId"));
                ad.setMeal_id(jsonObject3.optString("MealId"));
                ad.setName(jsonObject3.optString("CategoryName"));
                ad.setSelected(jsonObject3.getBoolean("CategorySelected"));
                ad.setPrice(0);
                //ad.setPrice(jsonObject3.optDouble("CategoryPrice"));
                meals_categories_read.add(ad);
            }
        }

        //remove duplicates
        remove_duplicates();
    }

    public void remove_duplicates(){
        for( Meal m : meals_read) {
            meals.add(m);
        }
        for( Addition a : meals_additions_read) {
            meals_additions.add(a);
        }
        for( Addition a : meals_categories_read) {
            meals_categories.add(a);
        }
    }

    public void saveJSONMeList() throws JSONException {
        //meals writing
        String FILENAME = "mealList.json";
        JSONArray jarray = new JSONArray();
        for (Meal me : meals_read) {
            JSONObject jres = new JSONObject();
            jres.put("RestaurantId", me.getRestaurantId());
            jres.put("MealId", me.getMealId());
            jres.put("MealPhoto", me.getMeal_photo());
            jres.put("MealName", me.getMeal_name());
            jres.put("MealPrice", me.getMeal_price());
            jres.put("MealType1", me.getType1());
            jres.put("MealType2", me.getType2());
            jres.put("MealAvailable", me.isAvailable());
            jres.put("MealTakeAway", me.isTake_away());
            jres.put("MealCookingTime", me.getCooking_time());
            jres.put("MealDescription", me.getDescription());
            Log.d("ccc", "WRITE");
            jarray.put(jres);
        }
        JSONObject resObj = new JSONObject();
        resObj.put("Meals", jarray);
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(resObj.toString().getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //additions writing
        String FILENAME2 = "mealAddition.json";
        JSONArray jarray2 = new JSONArray();
        for (Meal me : meals_read) {
            for (Addition ad : me.getMeal_additions()) {
                JSONObject jres2 = new JSONObject();
                jres2.put("RestaurantId", ad.getRestaurant_id());
                jres2.put("MealId", ad.getMeal_id());
                jres2.put("AdditionName", ad.getName());
                jres2.put("AdditionSelected", ad.isSelected());
                jres2.put("AdditionPrice", ad.getPrice());
                jarray2.put(jres2);
            }
        }
        JSONObject resObj2 = new JSONObject();
        resObj2.put("MealsAdditions", jarray2);
        FileOutputStream fos2 = null;
        try {
            fos2 = openFileOutput(FILENAME2, Context.MODE_PRIVATE);
            fos2.write(resObj2.toString().getBytes());
            fos2.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //categories writing
        String FILENAME3 = "mealCategory.json";
        JSONArray jarray3 = new JSONArray();
        for (Meal me : meals) {
            for (Addition ad : me.getMeal_categories()) {
                JSONObject jres3 = new JSONObject();
                jres3.put("RestaurantId", ad.getRestaurant_id());
                jres3.put("MealId", ad.getMeal_id());
                jres3.put("CategoryName", ad.getName());
                jres3.put("CategorySelected", ad.isSelected());
                jres3.put("CategoryPrice", 0);
                //jres3.put("Price", ad.getPrice());
                jarray3.put(jres3);
            }
            JSONObject resObj3 = new JSONObject();
            resObj3.put("MealsCategories", jarray3);
            FileOutputStream fos3 = null;
            try {
                fos3 = openFileOutput(FILENAME3, Context.MODE_PRIVATE);
                fos3.write(resObj3.toString().getBytes());
                fos3.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    ////////////////////////FragmentMainInfo///////////////////////////////////////////
    public static class FragmentMainInfo extends Fragment  { //implements AdapterView.OnItemSelectedListener
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
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

            //feed spinner3
            final Spinner spinner3 = (Spinner) rootView.findViewById(R.id.spinner3);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.meal_types_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner3.setAdapter(adapter);
            spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    current_meal.setType1(spinner3.getSelectedItem().toString());
                    try {
                        saveJSONMeList();
                    }
                    catch(JSONException e){
                        e.printStackTrace();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                }
            });
            //feed spinner4
            final Spinner spinner4 = (Spinner) rootView.findViewById(R.id.spinner4);
            ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(),
                    R.array.meal_types_array, android.R.layout.simple_spinner_item);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner4.setAdapter(adapter);
            spinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    current_meal.setType1(spinner4.getSelectedItem().toString());
                    try {
                        saveJSONMeList();
                    }
                    catch(JSONException e){
                        e.printStackTrace();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                }
            });

            //catch other changes and save them
            final EditText edit_meal_name = (EditText) rootView.findViewById(R.id.edit_meal_name);
            final EditText edit_meal_price = (EditText) rootView.findViewById(R.id.edit_meal_price);
            final CheckBox check_take_away = (CheckBox) rootView.findViewById(R.id.check_take_away);
            edit_meal_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus) { //when focus is lost only
                        current_meal.setMeal_name(edit_meal_name.getText().toString());
                        try {
                            saveJSONMeList();
                        }
                        catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                }
            });
            edit_meal_price.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus) { //when focus is lost only
                        current_meal.setMeal_price(Double.parseDouble(edit_meal_price.getText().toString()));
                        try {
                            saveJSONMeList();
                        }
                        catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                }
            });
            check_take_away.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    current_meal.setMeal_price(Double.parseDouble(edit_meal_price.getText().toString()));
                    try {
                        saveJSONMeList();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            Button button2 = (Button) rootView.findViewById(R.id.button_choose_photo2);
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //choose a photo
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                }
            });
            Button button1 = (Button) rootView.findViewById(R.id.button_take_photo2);
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //take the photo
                    dispatchTakePictureIntent();
                }
            });

            return rootView;
        }

        @Override
        public void onSaveInstanceState(Bundle state) {
            super.onSaveInstanceState(state);
            EditText edit_meal_name = (EditText) rootView.findViewById(R.id.edit_meal_name);
            EditText edit_meal_price = (EditText) rootView.findViewById(R.id.edit_meal_price);
            CheckBox check_take_away = (CheckBox) rootView.findViewById(R.id.check_take_away);
            Spinner spinner3 = (Spinner) rootView.findViewById(R.id.spinner3);
            Spinner spinner4 = (Spinner) rootView.findViewById(R.id.spinner4);
            state.putString("meal_name", edit_meal_name.getText().toString());
            state.putString("meal_price", edit_meal_price.getText().toString());
            state.putBoolean("meal_take_away", check_take_away.isSelected());
            state.putString("meal_type1", spinner3.getSelectedItem().toString());
            state.putString("meal_type2", spinner4.getSelectedItem().toString());
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            if(savedInstanceState!=null) {
                EditText edit_meal_name = (EditText) rootView.findViewById(R.id.edit_meal_name);
                EditText edit_meal_price = (EditText) rootView.findViewById(R.id.edit_meal_price);
                CheckBox check_take_away = (CheckBox) rootView.findViewById(R.id.check_take_away);
                Spinner spinner3 = (Spinner) rootView.findViewById(R.id.spinner3);
                Spinner spinner4 = (Spinner) rootView.findViewById(R.id.spinner4);
                edit_meal_name.setText(savedInstanceState.getString("meal_name"));
                edit_meal_price.setText(savedInstanceState.getString("meal_price"));
                check_take_away.setSelected(savedInstanceState.getBoolean("meal_take_away"));
                if(savedInstanceState.getString("meal_type1").equals("Celiac"))
                    spinner3.setSelection(0);
                if(savedInstanceState.getString("meal_type1").equals("Vegan"))
                    spinner3.setSelection(1);
                if(savedInstanceState.getString("meal_type1").equals("Vegetarian"))
                    spinner3.setSelection(2);
                if(savedInstanceState.getString("meal_type2").equals("Celiac"))
                    spinner4.setSelection(0);
                if(savedInstanceState.getString("meal_type2").equals("Vegan"))
                    spinner4.setSelection(1);
                if(savedInstanceState.getString("meal_type2").equals("Vegetarian"))
                    spinner4.setSelection(2);
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            try {
                saveJSONMeList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            try {
                readJSONMeList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private File createImageFile() throws IOException {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()); //util or sql import?
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            photouri = "file:" + image.getAbsolutePath();
            current_meal.setMeal_photo(photouri);
            try{
                saveJSONMeList();
            }
            catch(JSONException e){
                e.printStackTrace();
            }
            return image;
        }

        private void dispatchTakePictureIntent() {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                }
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        }

        public void readJSONMeList() throws JSONException {
            //mealList.json
            meals_read = new HashSet<>();
            meals_additions_read = new HashSet<>();
            meals_categories_read = new HashSet<>();
            Log.d("ccc", "CALLED READ");
            String json = null;
            FileInputStream fis = null;
            String FILENAME = "mealList.json";
            try {
                fis = getActivity().openFileInput(FILENAME);
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
                Log.d("ccc", "READ");
                if(me.getRestaurantId().equals(restaurant_id))
                    meals_read.add(me);
            }
            //mealAdditions.json
            String json2 = null;
            FileInputStream fis2 = null;
            String FILENAME2 = "mealAddition.json";
            try {
                fis2 = getActivity().openFileInput(FILENAME2);
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
                Addition ad = new Addition();
                if (jsonObject2.optString("RestaurantId").equals(restaurant_id)) {
                    ad.setRestaurant_id(jsonObject2.optString("RestaurantId"));
                    ad.setMeal_id(jsonObject2.optString("MealId"));
                    ad.setName(jsonObject2.optString("AdditionName"));
                    ad.setSelected(jsonObject2.getBoolean("AdditionSelected"));
                    ad.setPrice(jsonObject2.optDouble("AdditionPrice"));
                    meals_additions_read.add(ad);
                }
            }
            //mealCategories.json
            String json3 = null;
            FileInputStream fis3 = null;
            String FILENAME3 = "mealCategory.json";
            try {
                fis3 = getActivity().openFileInput(FILENAME3);
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
                Addition ad = new Addition();
                if (jsonObject3.optString("RestaurantId").equals(restaurant_id)) {
                    ad.setRestaurant_id(jsonObject3.optString("RestaurantId"));
                    ad.setMeal_id(jsonObject3.optString("MealId"));
                    ad.setName(jsonObject3.optString("CategoryName"));
                    ad.setSelected(jsonObject3.getBoolean("CategorySelected"));
                    ad.setPrice(0);
                    //ad.setPrice(jsonObject3.optDouble("CategoryPrice"));
                    meals_categories_read.add(ad);
                }
            }

            //remove duplicates
            remove_duplicates();
        }

        public void remove_duplicates(){
            for( Meal m : meals_read) {
                meals.add(m);
            }
            for( Addition a : meals_additions_read) {
                meals_additions.add(a);
            }
            for( Addition a : meals_categories_read) {
                meals_categories.add(a);
            }
        }

        public void saveJSONMeList() throws JSONException {
            //meals writing
            String FILENAME = "mealList.json";
            JSONArray jarray = new JSONArray();
            for (Meal me : meals_read) {
                JSONObject jres = new JSONObject();
                jres.put("RestaurantId", me.getRestaurantId());
                jres.put("MealId", me.getMealId());
                jres.put("MealPhoto", me.getMeal_photo());
                jres.put("MealName", me.getMeal_name());
                jres.put("MealPrice", me.getMeal_price());
                jres.put("MealType1", me.getType1());
                jres.put("MealType2", me.getType2());
                jres.put("MealAvailable", me.isAvailable());
                jres.put("MealTakeAway", me.isTake_away());
                jres.put("MealCookingTime", me.getCooking_time());
                jres.put("MealDescription", me.getDescription());
                Log.d("ccc", "WRITE");
                jarray.put(jres);
            }
            JSONObject resObj = new JSONObject();
            resObj.put("Meals", jarray);
            FileOutputStream fos = null;
            try {
                fos = getActivity().openFileOutput(FILENAME, Context.MODE_PRIVATE);
                fos.write(resObj.toString().getBytes());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //additions writing
            String FILENAME2 = "mealAddition.json";
            JSONArray jarray2 = new JSONArray();
            for (Meal me : meals_read) {
                for (Addition ad : me.getMeal_additions()) {
                    JSONObject jres2 = new JSONObject();
                    jres2.put("RestaurantId", ad.getRestaurant_id());
                    jres2.put("MealId", ad.getMeal_id());
                    jres2.put("AdditionName", ad.getName());
                    jres2.put("AdditionSelected", ad.isSelected());
                    jres2.put("AdditionPrice", ad.getPrice());
                    jarray2.put(jres2);
                }
            }
            JSONObject resObj2 = new JSONObject();
            resObj2.put("MealsAdditions", jarray2);
            FileOutputStream fos2 = null;
            try {
                fos2 = getActivity().openFileOutput(FILENAME2, Context.MODE_PRIVATE);
                fos2.write(resObj2.toString().getBytes());
                fos2.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //categories writing
            String FILENAME3 = "mealCategory.json";
            JSONArray jarray3 = new JSONArray();
            for (Meal me : meals) {
                for (Addition ad : me.getMeal_categories()) {
                    JSONObject jres3 = new JSONObject();
                    jres3.put("RestaurantId", ad.getRestaurant_id());
                    jres3.put("MealId", ad.getMeal_id());
                    jres3.put("CategoryName", ad.getName());
                    jres3.put("CategorySelected", ad.isSelected());
                    jres3.put("CategoryPrice", 0);
                    //jres3.put("Price", ad.getPrice());
                    jarray3.put(jres3);
                }
                JSONObject resObj3 = new JSONObject();
                resObj3.put("MealsCategories", jarray3);
                FileOutputStream fos3 = null;
                try {
                    fos3 = getActivity().openFileOutput(FILENAME3, Context.MODE_PRIVATE);
                    fos3.write(resObj3.toString().getBytes());
                    fos3.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    ////////////////////////FragmentMainInfo///////////////////////////////////////////


    ////////////////////////FragmentOtherInfo//////////////////////////////////////////
    public static class FragmentOtherInfo extends Fragment{
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
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_other_info_meal, container, false);

            if(restaurant_id!=null && !restaurant_id.equals("0")) {
                EditText description = (EditText) rootView.findViewById(R.id.edit_meal_description);
                description.setText(current_meal.getDescription());
            }
            //numberPicker implementation
            NumberPicker np = (NumberPicker) rootView.findViewById(R.id.numberPicker);
            np.setMinValue(1);
            np.setMaxValue(20);
            np.setWrapSelectorWheel(false);
            np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    current_meal.setCooking_time(newVal);
                    try {
                        saveJSONMeList();
                    }
                    catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            });

            //expandable additions
            parentAddition = "Meal additions";
            //setChildData(childAdditions);
            childAdditions.add(new Addition("0","0","rucola", 0, false));
            childAdditions.add(new Addition("0","0","parmigiano", 0, false));
            childAdditions.add(new Addition("0","0","tonno", 0, false));
            additions = (ExpandableListView) rootView.findViewById(R.id.additions_list);
            additions_adapter = new MyExpandableAdapter(parentAddition, childAdditions);
            additions.setAdapter(additions_adapter);
            additions.setDividerHeight(5);
            additions.setGroupIndicator(null);
            additions.setClickable(true);

            //expandable categories
            //setGroupParents(parentAddition);
            parentCategory = "Meal categories";
            //setChildData(childAdditions);
            childCategories.add(new Addition("0","0","suino", 0, false));
            childCategories.add(new Addition("0","0","bovino", 0, false));
            childCategories.add(new Addition("0","0","equino", 0, false));
            categories = (ExpandableListView) rootView.findViewById(R.id.categories_list);
            categories_adapter = new MyExpandableAdapter(parentCategory, childCategories);
            categories.setAdapter(categories_adapter);
            categories.setGroupIndicator(null);
            categories.setClickable(true);

            final ImageView addition_add_button = (ImageView) rootView.findViewById(R.id.addition_add);
            addition_add_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    childAdditions = additions_adapter.getChildItems();
                    childAdditions.add(new Addition("0","0", "New addition", 0, false));
                    additions_adapter.setChildItems(childAdditions);
                    current_meal.setMeal_additions(childAdditions);
                    try {
                        saveJSONMeList();
                    }
                    catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            });
            final ImageView category_add_button = (ImageView) rootView.findViewById(R.id.category_add);
            category_add_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    childCategories = categories_adapter.getChildItems();
                    childCategories.add(new Addition("0","0","New category", 0, false));
                    categories_adapter.setChildItems(childCategories);
                    current_meal.setMeal_additions(childCategories);
                    try {
                        saveJSONMeList();
                    }
                    catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            });

            return rootView;
        }

        @Override
        public void onSaveInstanceState(Bundle state) {
            super.onSaveInstanceState(state);
            NumberPicker np = (NumberPicker) rootView.findViewById(R.id.numberPicker);
            EditText description = (EditText) rootView.findViewById(R.id.edit_meal_description);
            state.putString("meal_description", description.getText().toString());
            state.putInt("meal_cooking_time", np.getValue());
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            if(savedInstanceState!=null) {
                NumberPicker np = (NumberPicker) rootView.findViewById(R.id.numberPicker);
                EditText description = (EditText) rootView.findViewById(R.id.edit_meal_description);
                np.setValue(savedInstanceState.getInt("meal_cooking_time"));
                description.setText(savedInstanceState.getString("meal_description"));
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            try {
                saveJSONMeList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            try {
                readJSONMeList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

         public class MyExpandableAdapter extends BaseExpandableListAdapter {
            private Activity activity;
            private ArrayList<Addition> childItems;
            private LayoutInflater inflater;
            private String parentItem;
            //private ArrayList<String> child;
            EditText userInput_price;
            View promptsView;

             public MyExpandableAdapter(String parent, ArrayList<Addition> children) {
                this.parentItem = parent;
                this.childItems = children;
                inflater = LayoutInflater.from(getActivity());
            }

             public ArrayList<Addition> getChildItems(){
                 return this.childItems;
             }

             public void setChildItems(ArrayList<Addition> new_list){
                 this.childItems = new_list;
                 notifyDataSetChanged();
                 notifyDataSetInvalidated();
             }

             @Override
            public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                //child = (ArrayList<String>) childItems.get(groupPosition);
                CheckBox checkbox_text = null;
                EditText edittex_text = null;
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.possible_additions_layout, null);
                }
                 if(parentItem.equals("Meal categories")){
                     convertView.findViewById(R.id.edit_addition_price).setVisibility(View.INVISIBLE);
                 }
                checkbox_text = (CheckBox) convertView.findViewById(R.id.meal_addition);
                checkbox_text.setText(childItems.get(childPosition).getName());
                 if(parentItem.equals("Meal additions")) {
                     edittex_text = (EditText) convertView.findViewById(R.id.edit_addition_price);
                     edittex_text.setText( String.valueOf(childItems.get(childPosition).getPrice()));
                 }
                convertView.findViewById(R.id.addition_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //get the row the clicked button is in
                        //getParent().getParentChildren().get(0).
                        childItems.remove(childPosition);
                        notifyDataSetChanged();
                        notifyDataSetInvalidated();
                        if(parentItem.equals("Meal additions"))
                            current_meal.setMeal_additions(childItems);
                        else
                            current_meal.setMeal_categories(childItems);
                        try {
                            saveJSONMeList();
                        }
                        catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                });

                 convertView.findViewById(R.id.addition_edit).setOnClickListener(new View.OnClickListener() {
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
                                                 if(parentItem.equals("Meal additions"))
                                                     current_meal.setMeal_additions(childItems);
                                                 else
                                                     current_meal.setMeal_categories(childItems);
                                                 try {
                                                     saveJSONMeList();
                                                 }
                                                 catch(JSONException e){
                                                     e.printStackTrace();
                                                 }
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

        public void readJSONMeList() throws JSONException {
            //mealList.json
            meals_read = new HashSet<>();
            meals_additions_read = new HashSet<>();
            meals_categories_read = new HashSet<>();
            Log.d("ccc", "CALLED READ");
            String json = null;
            FileInputStream fis = null;
            String FILENAME = "mealList.json";
            try {
                fis = getActivity().openFileInput(FILENAME);
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
                Log.d("ccc", "READ");
                if(me.getRestaurantId().equals(restaurant_id))
                    meals_read.add(me);
            }
            //mealAdditions.json
            String json2 = null;
            FileInputStream fis2 = null;
            String FILENAME2 = "mealAddition.json";
            try {
                fis2 = getActivity().openFileInput(FILENAME2);
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
                Addition ad = new Addition();
                if (jsonObject2.optString("RestaurantId").equals(restaurant_id)) {
                    ad.setRestaurant_id(jsonObject2.optString("RestaurantId"));
                    ad.setMeal_id(jsonObject2.optString("MealId"));
                    ad.setName(jsonObject2.optString("AdditionName"));
                    ad.setSelected(jsonObject2.getBoolean("AdditionSelected"));
                    ad.setPrice(jsonObject2.optDouble("AdditionPrice"));
                    meals_additions_read.add(ad);
                }
            }
            //mealCategories.json
            String json3 = null;
            FileInputStream fis3 = null;
            String FILENAME3 = "mealCategory.json";
            try {
                fis3 = getActivity().openFileInput(FILENAME3);
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
                Addition ad = new Addition();
                if (jsonObject3.optString("RestaurantId").equals(restaurant_id)) {
                    ad.setRestaurant_id(jsonObject3.optString("RestaurantId"));
                    ad.setMeal_id(jsonObject3.optString("MealId"));
                    ad.setName(jsonObject3.optString("CategoryName"));
                    ad.setSelected(jsonObject3.getBoolean("CategorySelected"));
                    ad.setPrice(0);
                    //ad.setPrice(jsonObject3.optDouble("CategoryPrice"));
                    meals_categories_read.add(ad);
                }
            }

            //remove duplicates
            remove_duplicates();
        }

        public void remove_duplicates(){
            for( Meal m : meals_read) {
                meals.add(m);
            }
            for( Addition a : meals_additions_read) {
                meals_additions.add(a);
            }
            for( Addition a : meals_categories_read) {
                meals_categories.add(a);
            }
        }

        public void saveJSONMeList() throws JSONException {
            //meals writing
            String FILENAME = "mealList.json";
            JSONArray jarray = new JSONArray();
            for (Meal me : meals_read) {
                JSONObject jres = new JSONObject();
                jres.put("RestaurantId", me.getRestaurantId());
                jres.put("MealId", me.getMealId());
                jres.put("MealPhoto", me.getMeal_photo());
                jres.put("MealName", me.getMeal_name());
                jres.put("MealPrice", me.getMeal_price());
                jres.put("MealType1", me.getType1());
                jres.put("MealType2", me.getType2());
                jres.put("MealAvailable", me.isAvailable());
                jres.put("MealTakeAway", me.isTake_away());
                jres.put("MealCookingTime", me.getCooking_time());
                jres.put("MealDescription", me.getDescription());
                Log.d("ccc", "WRITE");
                jarray.put(jres);
            }
            JSONObject resObj = new JSONObject();
            resObj.put("Meals", jarray);
            FileOutputStream fos = null;
            try {
                fos = getActivity().openFileOutput(FILENAME, Context.MODE_PRIVATE);
                fos.write(resObj.toString().getBytes());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //additions writing
            String FILENAME2 = "mealAddition.json";
            JSONArray jarray2 = new JSONArray();
            for (Meal me : meals_read) {
                for (Addition ad : me.getMeal_additions()) {
                    JSONObject jres2 = new JSONObject();
                    jres2.put("RestaurantId", ad.getRestaurant_id());
                    jres2.put("MealId", ad.getMeal_id());
                    jres2.put("AdditionName", ad.getName());
                    jres2.put("AdditionSelected", ad.isSelected());
                    jres2.put("AdditionPrice", ad.getPrice());
                    jarray2.put(jres2);
                }
            }
            JSONObject resObj2 = new JSONObject();
            resObj2.put("MealsAdditions", jarray2);
            FileOutputStream fos2 = null;
            try {
                fos2 = getActivity().openFileOutput(FILENAME2, Context.MODE_PRIVATE);
                fos2.write(resObj2.toString().getBytes());
                fos2.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //categories writing
            String FILENAME3 = "mealCategory.json";
            JSONArray jarray3 = new JSONArray();
            for (Meal me : meals) {
                for (Addition ad : me.getMeal_categories()) {
                    JSONObject jres3 = new JSONObject();
                    jres3.put("RestaurantId", ad.getRestaurant_id());
                    jres3.put("MealId", ad.getMeal_id());
                    jres3.put("CategoryName", ad.getName());
                    jres3.put("CategorySelected", ad.isSelected());
                    jres3.put("CategoryPrice", 0);
                    //jres3.put("Price", ad.getPrice());
                    jarray3.put(jres3);
                }
                JSONObject resObj3 = new JSONObject();
                resObj3.put("MealsCategories", jarray3);
                FileOutputStream fos3 = null;
                try {
                    fos3 = getActivity().openFileOutput(FILENAME3, Context.MODE_PRIVATE);
                    fos3.write(resObj3.toString().getBytes());
                    fos3.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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


