package it.polito.group2.restaurantowner.owner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.JSONUtil;
import it.polito.group2.restaurantowner.firebasedata.MealAddition;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.MealCategory;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
import it.polito.group2.restaurantowner.firebasedata.Review;

/**
 * Created by Alessio on 16/04/2016.
 */
public class MenuRestaurant_edit extends AppCompatActivity implements FragmentMainInfo.onMainInfoPass, FragmentOtherInfo.onOtherInfoPass{
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Meal current_meal;
    private StorageReference photo_reference;
    private StorageReference user_storage_reference;
    private Context context;
    private String photouri;
    private FirebaseDatabase firebase;
    private static final int PRICE_BOUNDARY_1 = 5;
    private static final int PRICE_BOUNDARY_2 = 10;
    private static final int PRICE_BOUNDARY_3 = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);

        context = this;

        //get current_meal
        Bundle b = getIntent().getExtras();
        if (getIntent().hasExtra("meal"))
            current_meal = (Meal) b.get("meal");
        else {
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    Restaurant_page.class);
            startActivity(intent1);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_restaurant, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            mSectionsPagerAdapter.saveDataFromFragments();
            if(current_meal.getMeal_name().equals(""))
                Toast.makeText(this,"Please insert meal name to continue", Toast.LENGTH_SHORT).show();
            else {
                Intent intent = new Intent();
                intent.putExtra("meal", (Serializable) current_meal);
                setResult(RESULT_OK, intent);
                finish();//finishing activity
                return true;
            }
        }
        if (id == android.R.id.home) {

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMainInfoPass(String meal_name, double meal_price, String  photouri,  boolean is_vegan, boolean is_vegetarian, boolean is_celiac, String category, boolean take_away) {
        current_meal.setMeal_name(meal_name);
        current_meal.setMeal_price(meal_price);
        //TODO upload both thumbnail and full size pictures
        //current_meal.setMeal_photo_firebase_URL(photouri);
        this.photouri = photouri;
        current_meal.setMealGlutenFree(is_celiac);
        current_meal.setMealVegan(is_vegan);
        current_meal.setMealVegetarian(is_vegetarian);
        current_meal.setMeal_category(category);
        current_meal.setMealTakeAway(take_away);

        //update restaurant price range
        firebase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebase.getReferenceFromUrl("https://have-break-9713d.firebaseio.com/meals/" + current_meal.getRestaurant_id());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                long total_meals_number = snapshot.getChildrenCount();
                double total_meals_price = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Meal m = (Meal) dataSnapshot.getValue(Meal.class);
                    total_meals_price += m.getMeal_price();
                }
                int new_price_range = calculate_range(total_meals_number, total_meals_price);
                DatabaseReference ref2 = firebase.getReferenceFromUrl("https://have-break-9713d.firebaseio.com/restaurants/" + current_meal.getRestaurant_id() + "/restaurant_price_range");
                ref2.setValue(new_price_range);
                DatabaseReference ref3 = firebase.getReferenceFromUrl("https://have-break-9713d.firebaseio.com/restaurants_previews/" + current_meal.getRestaurant_id() + "/restaurant_price_range");
                ref3.setValue(new_price_range);
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
    }

    public static int calculate_range(long total_meals_number, double total_meals_price) {
        if (total_meals_number == 0 || total_meals_price == 0)
            return 1;

        double ratio = 0;

        ratio = total_meals_price / total_meals_number;

        if (ratio <= PRICE_BOUNDARY_1)
            return 1;
        if (ratio > PRICE_BOUNDARY_1 && ratio < PRICE_BOUNDARY_2)
            return 2;
        if (ratio > PRICE_BOUNDARY_2 && ratio < PRICE_BOUNDARY_3)
            return 3;
        return 4;
    }


    @Override
    public void onOtherInfoPass(String meal_description, int cooking_time, ArrayList<MealAddition> mealAdditions, ArrayList<MealCategory> tags) {
        current_meal.setMeal_description(meal_description);
        current_meal.setMeal_cooking_time(cooking_time);
        current_meal.setMeal_additions(mealAdditions);
        current_meal.setMeal_tags(tags);

        //save photo
        ImageView image = (ImageView) findViewById(R.id.imageView);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        user_storage_reference = storage.getReferenceFromUrl("gs://have-break-9713d.appspot.com");
        // Create a child reference
        // imagesRef now points to "images"
        photo_reference = user_storage_reference.child("images/meals/" + current_meal.getMeal_id());
        File f = new File(photouri);
        Uri imageUri = Uri.fromFile(f);
        //upload
        UploadTask uploadTask = user_storage_reference.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            //public void onFailure(@NonNull Throwable throwable) {
            public void onFailure(Exception e) {
                e.printStackTrace();
                Log.d("my_ex", e.getMessage());
                Toast failure_message = Toast.makeText(context, "The upload is failed", Toast.LENGTH_LONG);
                failure_message.show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                current_meal.setMeal_photo_firebase_URL(downloadUrl.toString());
                String meal_key = current_meal.getMeal_id();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://have-break-9713d.firebaseio.com/meals/" + meal_key);
                ref.setValue(null);
                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://have-break-9713d.firebaseio.com/meals/");
                DatabaseReference ref3 = ref2.push();
                current_meal.setMeal_id(ref3.getKey());
                ref3.setValue(current_meal);

                //TODO save also thumbnail
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = 100.0 * (taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                Toast progress_message = Toast.makeText(context, "Upload is " + progress + "% done", Toast.LENGTH_LONG);
                progress_message.show();
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                Toast pause_message = Toast.makeText(context, "Upload is has been paused", Toast.LENGTH_LONG);
                pause_message.show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // If there's an upload in progress, save the reference so you can query it later
        if (photo_reference != null) {
            outState.putString("reference", photo_reference.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // If there was an upload in progress, get its reference and create a new StorageReference
        final String stringRef = savedInstanceState.getString("reference");
        if (stringRef == null) {
            return;
        }
        photo_reference = FirebaseStorage.getInstance().getReferenceFromUrl(stringRef);

        // Find all UploadTasks under this StorageReference (in this example, there should be one)
        List<UploadTask> tasks = photo_reference.getActiveUploadTasks();
        if (tasks.size() > 0) {
            // Get the task monitoring the upload
            UploadTask task = tasks.get(0);

            // Add new listeners to the task using an Activity scope
            task.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //
                }
            });
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private FragmentMainInfo fm;
        private FragmentOtherInfo fo;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0) {
                fm = FragmentMainInfo.newInstance(current_meal);
                return fm;
            }
            else {
                fo = FragmentOtherInfo.newInstance(current_meal, MenuRestaurant_edit.this); //,AddRestaurantActivity.this
                return fo;
            }
        }

        // Here we can finally safely save a reference to the created
        // Fragment
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            // save the appropriate reference depending on position
            switch (position) {
                case 0:
                    fm = (FragmentMainInfo) createdFragment;
                    break;
                case 1:
                    fo = (FragmentOtherInfo) createdFragment;
                    break;
            }
            return createdFragment;
        }

        public void saveDataFromFragments() {
            // do work on the referenced Fragments, but first check if they even exist yet, otherwise you'll get an NPE.
            if (fm != null) {
                fm.passData();
            }
            if (fo != null) {
                fo.passData();
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
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