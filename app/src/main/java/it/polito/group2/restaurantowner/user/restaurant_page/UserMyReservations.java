package it.polito.group2.restaurantowner.user.restaurant_page;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.JSONUtil;
import it.polito.group2.restaurantowner.data.OpenTime;
import it.polito.group2.restaurantowner.data.TableReservation;
import it.polito.group2.restaurantowner.data.Restaurant;
import it.polito.group2.restaurantowner.owner.MainActivity;
import it.polito.group2.restaurantowner.user.MyReviewsActivity;

import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarPickerView;

/**
 * Created by Alessio on 27/04/2016.
 */
public class UserMyReservations extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Toolbar toolbar;
    private Context context;
    private String current_username;
    ArrayList<TableReservation> all_table_reservations = new ArrayList<TableReservation>();
    private My_Reservations_Adapter adapter;
    List<Restaurant> resList = new ArrayList<Restaurant>();
    private String restaurant_number;
    private String user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_my_reservations);
        initToolBar();
        context = this;

        //get data
        Intent intent = getIntent();
        if(intent.getExtras().get("user_id")!=null)
            current_username = (String) intent.getExtras().get("user_id");

        try {
            all_table_reservations = JSONUtil.readJSONTableResList_user(context, current_username);
        } catch (JSONException e) {
            Log.e("EXCEPTION", "EXCETPION IN READING THE FILE IN onCreate");
        }
        try {
            resList = JSONUtil.readJSONResList(context);
        } catch (JSONException e) {
            Log.e("EXCEPTION", "EXCETPION IN READING THE FILE IN My_Reservations_Adapter");
        }
        //ordering from last done
        Collections.reverse(all_table_reservations);
        Log.d("tagx", String.valueOf(all_table_reservations.size()));

        //card view implementation
        RecyclerView rv = (RecyclerView)findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        rv.setLayoutManager(llm);
        adapter = new My_Reservations_Adapter(context, all_table_reservations);
        rv.setAdapter(adapter);

        //navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //TODO decomment handle logged/not logged user
        /*
        if(user_id==null){ //not logged
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_my_profile).setVisible(false);
            nav_Menu.findItem(R.id.nav_my_orders).setVisible(false);
            nav_Menu.findItem(R.id.nav_my_reservations).setVisible(false);
            nav_Menu.findItem(R.id.nav_my_reviews).setVisible(false);
            nav_Menu.findItem(R.id.nav_my_favorites).setVisible(false);
        }
        else{ //logged
            //if user is logged does not need to logout for any reason; he could authenticate with another user so Login is still maintained
        }
        */
    }

    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.my_reservations);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                    UserRestaurantList.class);
            Bundle b1 = new Bundle();
            b1.putString("user_id", user_id);
            intent1.putExtras(b1);
            startActivity(intent1);
            return true;
        } else if(id==R.id.nav_my_orders) {
            Intent intent1 = new Intent(
                    getApplicationContext(),
                    UserRestaurantList.class);
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
        } else if(id==R.id.nav_my_favorites){
            Intent intent3 = new Intent(
                    getApplicationContext(),
                    UserRestaurantList.class);
            Bundle b3 = new Bundle();
            b3.putString("user_id", user_id);
            intent3.putExtras(b3);
            startActivity(intent3);
            return true;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void my_reservation_call(View v){
        ViewGroup vg1 = (ViewGroup) v.getParent();
        //TextView child = (TextView) vg1.findViewById(R.id.restaurant_name_my_reservation);
        LinearLayout ll = (LinearLayout)vg1.getParent();
        TextView child = (TextView) ll.findViewById(R.id.restaurant_name_my_reservation);
        restaurant_number = findRestaurantNumberByName(child.getText().toString());
        //restaurant_number = "+39095365265";
        if(restaurant_number==null){
            Toast.makeText(context, R.string.telephone_number_not_provided, Toast.LENGTH_SHORT);
        }
        else {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.call_confirmation))
                    .setMessage(
                            getResources().getString(R.string.call_confirmation_message))
                    .setIcon(
                            getResources().getDrawable(
                                    android.R.drawable.ic_dialog_alert))
                    .setPositiveButton(
                            getResources().getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    //call
                                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                                    callIntent.setData(Uri.parse("tel:" + restaurant_number));
                                    try {
                                        startActivity(callIntent);
                                    } catch (SecurityException s) {
                                        Log.e("EXCEPTION", "EXCEPTION SECURITY IN my_reservation_call");
                                    }
                                }

                    })
                    .setNegativeButton(
                            getResources().getString(R.string.no),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    //Do Something Here
                                }
                            }).show();
                    }

    }

    public void my_reservation_remove(View v){
        ViewGroup vg1 = (ViewGroup) v.getParent();
        //TextView child = (TextView) vg1.findViewById(R.id.restaurant_name_my_reservation);
        TextView child = (TextView) vg1.getChildAt(2);
        String res_name = child.getText().toString();
        TextView child2 = (TextView) vg1.getChildAt(1);
        String date_reservation = child2.getText().toString();
        String res_id = findRestaurantIdByName(res_name);
        TableReservation tr = findReservationToRemove(res_id, date_reservation);
        if(tr!=null) {
            all_table_reservations.remove(tr);
            adapter.notifyDataSetChanged();
            try {
                JSONUtil.saveJSONTableResList(context, all_table_reservations);
            } catch (JSONException e) {
                Log.e("EXCEPTION", "EXCETPION IN WRITING THE FILE IN my_reservation_remove");
            }
        }
    }

    private TableReservation findReservationToRemove(String res_id, String date_reservation) {
        Calendar date = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            date.setTime(sdf.parse(date_reservation));
        } catch (java.text.ParseException e) {
            Log.e("EXCEPTION", "SDF.PARSE RAISED AN EXCEPTION IN onOptionsItemSelected");
        }
        for(TableReservation tr : all_table_reservations){
            if(tr.getUsername().equals(current_username)) {
                    if (tr.getRestaurantId().equals(res_id))
                        if (tr.getDate().YEAR==date.YEAR && tr.getDate().MONTH==date.MONTH && tr.getDate().DAY_OF_MONTH==date.DAY_OF_MONTH && tr.getDate().MINUTE==date.MINUTE && tr.getDate().HOUR==date.HOUR)
                            return tr;
            }
        }
        return null;
    }

    private String findRestaurantNumberByName(String res_name){
        for(Restaurant r : resList){
            if(r.getName().equals(res_name)){
                return r.getPhoneNum();
            }
        }
        return null;
    }

    private String findRestaurantIdByName(String res_name){
        for(Restaurant r : resList){
            if(r.getName().equals(res_name)){
                return r.getRestaurantId();
            }
        }
        return null;
    }
}
