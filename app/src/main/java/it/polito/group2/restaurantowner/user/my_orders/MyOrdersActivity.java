package it.polito.group2.restaurantowner.user.my_orders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Random;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.JSONUtil;
import it.polito.group2.restaurantowner.data.User;
import it.polito.group2.restaurantowner.owner.MainActivity;
import it.polito.group2.restaurantowner.user.my_reviews.MyReviewsActivity;
import it.polito.group2.restaurantowner.user.restaurant_page.UserMyFavourites;
import it.polito.group2.restaurantowner.user.restaurant_page.UserMyReservations;
import it.polito.group2.restaurantowner.user.restaurant_page.UserProfile;
import it.polito.group2.restaurantowner.user.restaurant_list.UserRestaurantList;
import it.polito.group2.restaurantowner.data.Meal;
import it.polito.group2.restaurantowner.data.MealAddition;
import it.polito.group2.restaurantowner.data.MenuCategory;
import it.polito.group2.restaurantowner.data.Order;
import it.polito.group2.restaurantowner.data.OrderMeal;
import it.polito.group2.restaurantowner.data.OrderMealAddition;
import it.polito.group2.restaurantowner.user.order.AdditionModel;
import it.polito.group2.restaurantowner.user.order.MealModel;

public class MyOrdersActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ArrayList<User> users = new ArrayList<User>();
    private Context context;
    public User current_user;
    private String user_id;
    private Drawable d;

    private ArrayList<OrderModel> modelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myorders_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO Rearrange the following code
        if(getIntent().getExtras()!=null && getIntent().getExtras().getString("user_id")!=null) {
            user_id = getIntent().getExtras().getString("user_id");
            try {
                users = JSONUtil.readJSONUsersList(this, null);
            }
            catch(JSONException e){
                e.printStackTrace();
            }
            for(User u : users){
                if(u.getId().equals(user_id)){
                    current_user = u;
                    break;
                }
            }
        }
        else{
            current_user = new User();
            current_user.setEmail("jkjs@dskj");
            //current_user.setFidelity_points(110);
            current_user.setFirst_name("Alex");
            current_user.setIsOwner(true);
            current_user.setPassword("tipiacerebbe");
            current_user.setPhone_number("0989897879789");
            current_user.setVat_number("sw8d9wd8w9d8w9d9");
        }
        //navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        TextView nav_username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderUsername);
        TextView nav_email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderEmail);
        ImageView nav_photo = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView);
        if(current_user != null) {
            if (current_user.getFirst_name() != null && current_user.getLast_name() == null)
                nav_username.setText(current_user.getFirst_name());
            else if (current_user.getFirst_name() == null && current_user.getLast_name() != null)
                nav_username.setText(current_user.getLast_name());
            else if (current_user.getFirst_name() != null && current_user.getLast_name() != null)
                nav_username.setText(current_user.getFirst_name() + " " + current_user.getLast_name());
            if (current_user.getEmail() != null)
                nav_email.setText(current_user.getEmail());
            if (current_user.getPhoto() != null)
                nav_photo.setImageBitmap(current_user.getPhoto());
        }
        SharedPreferences userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
        Uri photouri = null;
        if(userDetails.getString("photouri", null)!=null) {
            photouri = Uri.parse(userDetails.getString("photouri", null));
            File f = new File(getRealPathFromURI(photouri));
            Drawable d = Drawable.createFromPath(f.getAbsolutePath());
            navigationView.getHeaderView(0).setBackground(d);
        }
        else
            nav_photo.setImageResource(R.drawable.blank_profile);

        setOrderList();
    }

    private void setOrderList() {
        modelList = getModel();
        final RecyclerView orderList = (RecyclerView) findViewById(R.id.order_list);
        assert orderList != null;
        orderList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        orderList.setNestedScrollingEnabled(false);
        OrderAdapter adapter = new OrderAdapter(this, modelList);
        orderList.setAdapter(adapter);
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        d = null;
        System.gc();
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
        if(id==R.id.nav_owner){
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    MainActivity.class);
            Bundle b1 = new Bundle();
            b1.putString("user_id", user_id);
            intent1.putExtras(b1);
            startActivity(intent1);
            return true;
        }
        else if(id==R.id.nav_home){
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    UserRestaurantList.class);
            Bundle b1 = new Bundle();
            b1.putString("user_id", user_id);
            intent1.putExtras(b1);
            startActivity(intent1);
            return true;
        }
        else if(id==R.id.nav_login){
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    UserRestaurantList.class);
            startActivity(intent1);
            return true;
        } else if(id==R.id.nav_my_profile) {
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    UserProfile.class);
            Bundle b1 = new Bundle();
            b1.putString("user_id", user_id);
            intent1.putExtras(b1);
            startActivity(intent1);
            return true;
        } else if(id==R.id.nav_my_orders) {
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    MyOrdersActivity.class);
            Bundle b1 = new Bundle();
            b1.putString("user_id", user_id);
            intent1.putExtras(b1);
            startActivity(intent1);
            return true;
        } else if(id==R.id.nav_my_reservations){
            Intent intent3 = new Intent(
                    getApplicationContext(),
                    UserMyReservations.class);
            Bundle b3 = new Bundle();
            b3.putString("user_id", user_id);
            intent3.putExtras(b3);
            startActivity(intent3);
            return true;
        } else if(id==R.id.nav_my_reviews){
            Intent intent3 = new Intent(
                    getApplicationContext(),
                    MyReviewsActivity.class);
            Bundle b3 = new Bundle();
            b3.putString("user_id", user_id);
            intent3.putExtras(b3);
            startActivity(intent3);
            return true;
        } else if(id==R.id.nav_my_favourites){
            Intent intent3 = new Intent(
                    getApplicationContext(),
                    UserMyFavourites.class);
            Bundle b3 = new Bundle();
            b3.putString("user_id", user_id);
            intent3.putExtras(b3);
            startActivity(intent3);
            return true;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private ArrayList<OrderModel> getModel() {
        ArrayList<OrderModel> list = new ArrayList<OrderModel>();
        ArrayList<Order> myorders = getMyOrders();

        for(Order o : myorders) {
            ArrayList<MealModel> mealList = new ArrayList<MealModel>();
            for(OrderMeal m : o.getMealList()) {
                ArrayList<AdditionModel> additionList = new ArrayList<AdditionModel>();
                for(OrderMealAddition a : m.getAdditionList()) {
                    AdditionModel aModel = new AdditionModel(a.getAddition().getAddition_id(),
                            a.getAddition().getName(),true, a.getAddition());
                    aModel.setSelected(true);
                    additionList.add(aModel);
                }
                MealModel mModel = new MealModel(m.getMeal().getMealId(),m.getMeal().getMeal_name(),m.getMeal(),m.getQuantity());
                mModel.setAdditionModel(additionList);
                mealList.add(mModel);
            }
            OrderModel oMod = new OrderModel(o.getOrderID(),o,mealList);
            list.add(oMod);
        }
        return list;
    }

    private ArrayList<Order> getMyOrders() {
        //TODO modificare il metodo per prendere i dati da firebase
        ArrayList<Order> myorders = new ArrayList<Order>();

        Random randomGenerator = new Random();
        for(int i=0; i<(randomGenerator.nextInt(5)+3); i++) {
            Order o = new Order();
            o.setNote("Test note " + i);
            o.setRestaurantID("restID0");
            o.setUserID("userID0");
            o.setOrderID("orderID" + i);
            o.setTimestamp(Calendar.getInstance());
            for(int j=0; j<(randomGenerator.nextInt(5)+2); j++){
                OrderMeal om = new OrderMeal();
                om.setNote("meal note");
                om.setOrderID("orderID" + i);
                om.setQuantity((randomGenerator.nextInt(3) + 1));
                om.setOrderMealID("ordermealID" + j);
                Meal m = new Meal();
                m.setMeal_name("Meal " + j);
                m.setMealId("mealID" + j);
                m.setCategory("CAT0");
                m.setDescription("Example description");
                m.setMeal_price(5.2);
                om.setMeal(m);
                MenuCategory cat = new MenuCategory();
                cat.setName("CAT0");
                cat.setCategoryID("CAT0");
                om.setCategory(cat);

                for(int k=0; k<(randomGenerator.nextInt(2)+2); k++) {
                    OrderMealAddition oam = new OrderMealAddition();
                    oam.setOrderID("orderID" + i);
                    oam.setOrderMealID("ordermealID" + j);
                    oam.setOrderMealAdditionID("orderadditionID" + k);
                    MealAddition ma = new MealAddition();
                    ma.setName("add "+k);
                    ma.setAddition_id("addID"+k);
                    ma.setPrice(0.5);
                    oam.setAddition(ma);
                    om.getAdditionList().add(oam);
                }

                o.getMealList().add(om);
            }
            myorders.add(o);
        }
        return myorders;
    }

}
