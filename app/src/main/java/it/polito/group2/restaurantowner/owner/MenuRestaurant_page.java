package it.polito.group2.restaurantowner.owner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.graphics.Rect;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import java.io.File;
import java.util.UUID;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.MealAddition;
import it.polito.group2.restaurantowner.data.JSONUtil;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
import it.polito.group2.restaurantowner.firebasedata.Review;
import it.polito.group2.restaurantowner.gallery.GalleryViewActivity;
import it.polito.group2.restaurantowner.owner.offer.OfferListActivity;

public class MenuRestaurant_page extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Menu menu;
    private ListView listView;
    private Adapter_Meals adapter;
    private String restaurant_id;
    private ArrayList<Meal> meals = new ArrayList<>();
    private ArrayList<MealAddition> meals_additions = new ArrayList<>();
    private ArrayList<MealAddition> meals_categories = new ArrayList<>();
    public RecyclerView rv;
    public int index_position;
    private Meal meal_to_delete;
    private Meal meal_to_edit;
    public final int MODIFY_MEAL = 1;
    public int MODIFY_INFO = 4;
    public Swipe_Detector s_d = null;
    private RecyclerView  mRecyclerView;
    public Restaurant current_restaurant;
    public Context context;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_restaurant_page);

        context = this;

        //get the right restaurant
        if(restaurant_id==null)
            restaurant_id = "fake_restaurant_id";

        //get and fill related data
        get_data_from_firebase();

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager mLayoutManager = null;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager = new GridLayoutManager(this, 1);
            mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(1,5,true));
        }
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLayoutManager = new GridLayoutManager(this, 2);
            mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2,5,true));
        }
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        final String meal_key = meals.get(position).getMeal_id();
                        Firebase ref = new Firebase("https://have-break.firebaseio.com/meals/");
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                for (DataSnapshot meSnapshot : snapshot.getChildren()) {
                                    Meal snap_meal = meSnapshot.getValue(Meal.class);
                                    String snap_restaurant_id = snap_meal.getRestaurant_id();
                                    if (snap_restaurant_id.equals(restaurant_id)) {
                                        if (meal_key.equals(snap_meal.getMeal_id())) {
                                            meal_to_edit = snap_meal;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                System.out.println("The read failed: " + firebaseError.getMessage());
                            }
                        });
                        Intent intent1 = new Intent(
                                getApplicationContext(),
                                MenuRestaurant_edit.class);
                        if (meal_to_edit != null) {
                            intent1.putExtra("meal", meal_to_edit);
                            startActivityForResult(intent1, MODIFY_MEAL);
                        }
                    }
                }));
        adapter = new Adapter_Meals(this, 0, meals, restaurant_id);
        mRecyclerView.setAdapter(adapter);
        //delete with swipe
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter, true, meals, "https://have-break.firebaseio.com/meals/");
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    /*
    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        try {
            saveJSONMeList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        try {
            readJSONMeList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    */

    private void progress_dialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Its loading....");
        progressDialog.setTitle("");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    private void get_data_from_firebase(){
        progress_dialog();

        //my_restaurant
        Firebase ref = new Firebase("https://have-break.firebaseio.com/meals/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot meSnapshot: snapshot.getChildren()) {
                    Meal snap_meal = meSnapshot.getValue(Meal.class);
                    String snap_restaurant_id = snap_meal.getRestaurant_id();
                    if(snap_restaurant_id.equals(restaurant_id)){
                        meals.add(snap_meal);
                    }
                }
                /*The owner is the only one that can edit, so I should not put more synchronization, but in case put something like this:
                       for(Review r_temp : reviews){
                            if(r_temp.getReview_id().equals(snap_review.getReview_id())){
                                reviews.remove(r_temp);
                                break;
                            }
                        }
                        reviews.add(snap_review);
                        adapter.notifyDataSetChanged();
            */
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        progressDialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main_restaurant, menu);
        //this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(id==R.id.action_my_restaurants){
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    MainActivity.class);
            startActivity(intent1);
            return true;
        } else if(id==R.id.action_gallery) {
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    GalleryViewActivity.class);
            Bundle b = new Bundle();
            b.putString("restaurant_id", restaurant_id);
            intent1.putExtras(b);
            startActivity(intent1);
            return true;
        } else if(id==R.id.action_menu) {
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    MenuRestaurant_page.class);
            Bundle b = new Bundle();
            b.putString("restaurant_id", restaurant_id);
            intent1.putExtras(b);
            startActivity(intent1);
            return true;
        } else if(id==R.id.action_offers) {
            Intent intent2 = new Intent(
                    getApplicationContext(),
                    OfferListActivity.class);
            Bundle b2 = new Bundle();
            b2.putString("restaurant_id", restaurant_id);
            intent2.putExtras(b2);
            startActivity(intent2);
            return true;
        } else if(id==R.id.action_reservations){
            Intent intent3 = new Intent(
                    getApplicationContext(),
                    ReservationActivity.class);
            Bundle b3 = new Bundle();
            b3.putString("restaurant_id", restaurant_id);
            intent3.putExtras(b3);
            startActivity(intent3);
            return true;
        } else if(id==R.id.action_reviews){
            Intent intent4 = new Intent(
                    getApplicationContext(),
                    ReviewsActivity.class); //here Filippo must insert his class name
            Bundle b4 = new Bundle();
            b4.putString("restaurant_id", restaurant_id);
            intent4.putExtras(b4);
            startActivity(intent4);
            return true;
        } else if(id==R.id.action_statistics){
            Intent intent5 = new Intent(
                    getApplicationContext(),
                    StatisticsActivity.class); //here Filippo must insert his class name
            Bundle b5 = new Bundle();
            b5.putString("restaurant_id", restaurant_id);
            intent5.putExtras(b5);
            startActivity(intent5);
            return true;
        } else if(id==R.id.action_edit){
            Intent intent6 = new Intent(
                    getApplicationContext(),
                    AddRestaurantActivity.class);
            intent6.putExtra("Restaurant", current_restaurant);
            final AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
            appbar.setExpanded(false);
            startActivityForResult(intent6, MODIFY_INFO);
            return true;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void myClickHandler_add(View v) {
        Meal m = new Meal();
        m.setMeal_name("");
        m.setMeal_price(0.0);
        m.setIs_meal_availabile(false);
        m.setMeal_cooking_time(0);
        m.setMeal_id(UUID.randomUUID().toString());
        m.setRestaurant_id(restaurant_id);
        m.setMeal_description("");
        m.setIs_meal_vegetarian(false);
        m.setIs_meal_vegan(false);
        m.setIs_meal_celiac(false);
        m.setIs_meal_take_away(false);
        m.setMeal_photo_firebase_URL("");
        m.setMeal_thumbnail("");
        meals.add(0, m);  //insert at the top
        adapter.notifyDataSetChanged();
        Firebase ref = new Firebase("https://have-break.firebaseio.com/meals/");
        Firebase ref_pushed = ref.push();
        ref_pushed.setValue(m);
        String m_key = ref_pushed.getKey();
        Firebase ref2 = new Firebase("https://have-break.firebaseio.com/meals/"+m_key);
        ref2.child("meal_id").setValue(m_key);
    }

    public void myClickHandler_enlarge(View v) {
        ImageView imageview = (ImageView) v.findViewById(R.id.meal_image);
        LinearLayout ll = (LinearLayout) v.getParent();
        TextView child = (TextView) ll.findViewById(R.id.meal_name);
        String meal_name = child.getText().toString();
        int i = 0;
        for (; i < meals.size(); i++) {
            if (meals.get(i).getMeal_name().equals(meal_name)) {
                if (meals.get(i).getMeal_photo_firebase_URL() != null && !meals.get(i).getMeal_photo_firebase_URL().equals((""))) {
                    Intent intent = new Intent(
                            getApplicationContext(),
                            Enlarged_image.class);
                    Bundle b = new Bundle();
                    b.putString("photouri", meals.get(i).getMeal_photo_firebase_URL());
                    intent.putExtras(b);
                    startActivity(intent);
                    break;
                }
            }
        }
    }

    public void myClickHandler_remove(View v) {
        LinearLayout vwParentRow = (LinearLayout) v.getParent();
        TextView child = (TextView) vwParentRow.getChildAt(2);
        final String meal_name = child.getText().toString();
        meal_to_delete = null;
        //TODO find meal_to_delete into db

        //dialog box
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        meals.remove(meal_to_delete);
                        //adapter.remove(meal_to_delete);
                        //TODO remove meal from db
                        adapter.notifyDataSetChanged();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setMessage("Are you sure that you want to delete " + meal_name + "?").setPositiveButton("Yes, sure", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MODIFY_MEAL) {
            if (resultCode == RESULT_OK) {
                Meal m = (Meal) data.getExtras().get("meal");
                meals.set(index_position, m);
                //TODO update meal into db

                adapter.notifyDataSetChanged();
            }
        }
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

}