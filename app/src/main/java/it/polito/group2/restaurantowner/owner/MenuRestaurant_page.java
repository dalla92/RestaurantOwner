package it.polito.group2.restaurantowner.owner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.graphics.Rect;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.DrawerUtil;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.Utils.OnBackUtil;
import it.polito.group2.restaurantowner.Utils.RemoveListenerUtil;
import it.polito.group2.restaurantowner.firebasedata.MealAddition;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;

public class MenuRestaurant_page extends AppCompatActivity {
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
    private FirebaseDatabase firebase;
    private DatabaseReference q;
    private ChildEventListener l;
    private ProgressDialog mProgressDialog;
    private Activity a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_restaurant_page);

        context = this;
        a = this;
        firebase = FirebaseDatabase.getInstance();

        FirebaseUtil.initProgressDialog(this);
        FirebaseUtil.showProgressDialog(mProgressDialog);

        if(getIntent().getExtras()!=null && getIntent().getExtras().getString("restaurant_id") != null)
            restaurant_id = getIntent().getExtras().getString("restaurant_id");

        //recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager mLayoutManager = null;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager = new GridLayoutManager(this, 1);
            mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(1, 5, true));
        }
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLayoutManager = new GridLayoutManager(this, 2);
            mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 5, true));
        }
        //mRecyclerView.setLayoutManager(mLayoutManager);

        ImageView add_button = (ImageView) findViewById(R.id.imageButton);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mealRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://have-break-9713d.firebaseio.com/meals/" + restaurant_id).push();
                Meal m = new Meal();
                m.setMeal_name("");
                m.setMeal_price(0.0);
                m.setMealAvailable(false);
                m.setMeal_cooking_time(0);
                m.setMeal_id(mealRef.getKey());
                m.setRestaurant_id(restaurant_id);
                m.setMeal_description("");
                m.setMealVegetarian(false);
                m.setMealVegan(false);
                m.setMealGlutenFree(false);
                m.setMealTakeAway(false);
                m.setMeal_photo_firebase_URL("");
                m.setMeal_thumbnail("");
                m.setMeal_quantity(0);
                mealRef.setValue(m);
                adapter.addItem(m);
                Toast.makeText(MenuRestaurant_page.this, getString(R.string.meal_added), Toast.LENGTH_SHORT).show();
            }
        });

        //get and fill related data
        get_data_from_firebase();

    }
    
    private void get_data_from_firebase(){
        q = FirebaseDatabase.getInstance().getReferenceFromUrl("https://have-break-9713d.firebaseio.com/meals/"+restaurant_id);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    meals.add(data.getValue(Meal.class));
                }
                adapter = new Adapter_Meals((Activity)context, 0, meals, restaurant_id);
                mRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /*l = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Meal snap_meal = dataSnapshot.getValue(Meal.class);
                adapter.addItem(0, snap_meal);
             //   adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int res = adapter.findMeal(dataSnapshot.getValue(Meal.class));
                if(res!=-1){
                    adapter.removeItem(res);
                    adapter.addItem(res, dataSnapshot.getValue(Meal.class));
            //        adapter.notifyItemChanged(res);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int res = adapter.findMeal(dataSnapshot.getValue(Meal.class));
                if(res!=-1){
                    adapter.removeItem(res);
            //        adapter.notifyItemRemoved(res);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        };*/

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                (Activity) context, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem i = menu.findItem(R.id.action_edit);
        i.setVisible(false);
        MenuItem i2 = menu.findItem(R.id.action_show_as);
        i.setVisible(false);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressWarnings("StatementWithEmptyBody")
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Handle navigation view item clicks here.
                return DrawerUtil.drawer_owner_not_restaurant_page(a, item, restaurant_id);
            }
        });

                /*
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    mLayoutManager = new GridLayoutManager(this, 1);
                    mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(1,5,true));
                }
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mLayoutManager = new GridLayoutManager(this, 2);
                    mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2,5,true));
                }
                */
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        meal_to_edit = meals.get(position);
                        Intent intent = new Intent(getApplicationContext(), MenuRestaurant_edit.class);
                        intent.putExtra("meal", meal_to_edit);
                        startActivityForResult(intent, MODIFY_MEAL);
                    }
                }));
        //delete with swipe
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

    }

   /*@Override
    protected void onStart() {
        super.onStart();
        if(q!=null) {
            q.addChildEventListener(l);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(q!=null) {
            RemoveListenerUtil.remove_child_event_listener(q, l);
            meals.clear();
            adapter.notifyDataSetChanged();
        }
    }*/

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
            OnBackUtil.clean_stack_and_go_to_restaurant_page(this, restaurant_id);
        }
    }

    public void myClickHandler_enlarge(View v) {
        ImageView imageview = (ImageView) v.findViewById(R.id.meal_image);
        LinearLayout ll = (LinearLayout) v.getParent();
        TextView child = (TextView) ll.findViewById(R.id.meal_name);
        String meal_name = child.getText().toString();

        for (int i = 0; i < meals.size(); i++) {
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

    /*
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
    */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MODIFY_MEAL) {
            if (resultCode == RESULT_OK) {
                Meal m = (Meal) data.getExtras().get("meal");
                DatabaseReference dr = firebase.getReference("meals/" + m.getRestaurant_id() + "/" + m.getMeal_id());
                dr.setValue(m);
                adapter.replaceItem(m);
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