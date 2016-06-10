package it.polito.group2.restaurantowner.owner.statistics;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.LineChart;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.DrawerUtil;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.Utils.OnBackUtil;
import it.polito.group2.restaurantowner.Utils.RemoveListenerUtil;
import it.polito.group2.restaurantowner.firebasedata.Order;
import it.polito.group2.restaurantowner.firebasedata.TableReservation;
import it.polito.group2.restaurantowner.firebasedata.User;
import it.polito.group2.restaurantowner.gallery.GalleryViewActivity;
import it.polito.group2.restaurantowner.owner.AddRestaurantActivity;
import it.polito.group2.restaurantowner.owner.MainActivity;
import it.polito.group2.restaurantowner.owner.MenuRestaurant_page;
import it.polito.group2.restaurantowner.owner.Restaurant_page;
import it.polito.group2.restaurantowner.owner.my_offers.MyOffersActivity;
import it.polito.group2.restaurantowner.owner.reservations.ReservationActivity;
import it.polito.group2.restaurantowner.owner.reviews.ReviewsActivity;
import it.polito.group2.restaurantowner.user.restaurant_page.UserRestaurantActivity;

public class StatisticsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private ProgressDialog mProgressDialog;
    public int MODIFY_INFO = 4;
    private Query q_orders;
    private ValueEventListener l_orders;
    private Query q_reservations;
    private ValueEventListener l_reservations;
    private String restaurantID;
    ArrayList<Order> orderList = null;
    ArrayList<TableReservation> reservationList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_statistics_activity);

        //Toolbar setting
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgressDialog = FirebaseUtil.initProgressDialog(this);
        FirebaseUtil.showProgressDialog(mProgressDialog);

        DatabaseReference userRef = FirebaseUtil.getCurrentUserRef();
        if (userRef == null)
            abortActivity();

        if (getIntent().getExtras() == null || getIntent().getExtras().getString("restaurant_id") == null)
            abortActivity();
        restaurantID = getIntent().getExtras().getString("restaurant_id");
        q_orders = FirebaseUtil.getOrdersByRestaurantRef(restaurantID);
        if (q_orders == null)
            abortActivity();
        q_reservations = FirebaseUtil.getOrdersByRestaurantRef(restaurantID);
        if (q_reservations == null)
            abortActivity();

        assert userRef != null;

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                setDrawer(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        if (q_orders != null) {
            l_orders = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<Order> orders = new ArrayList<>();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        orders.add(d.getValue(Order.class));
                    }
                    orderList = orders;
                    getStart(true);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
        }

        if (q_reservations != null) {
            l_reservations = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<TableReservation> reservations = new ArrayList<>();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        reservations.add(d.getValue(TableReservation.class));
                    }
                    FirebaseUtil.hideProgressDialog(mProgressDialog);
                    reservationList = reservations;
                    getStart(false);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUtil.initProgressDialog(this);
        FirebaseUtil.showProgressDialog(mProgressDialog);
        q_reservations.addValueEventListener(l_reservations);
        q_orders.addValueEventListener(l_orders);
    }
    @Override
    protected void onStop() {
        super.onStop();
        RemoveListenerUtil.remove_value_event_listener(q_orders, l_orders);
        RemoveListenerUtil.remove_value_event_listener(q_reservations, l_reservations);
    }

    private synchronized void getStart(boolean orders) {
        if(orders) {
            if(reservationList != null) {
                drawChart();
            }
        } else {
            if(orderList != null) {
                drawChart();
            }
        }
    }

    private void drawChart() {
        FirebaseUtil.hideProgressDialog(mProgressDialog);
        LineChart chart = (LineChart) findViewById(R.id.chart);
        LineData data = new LineData(getDataName(), getDataSet());
        assert chart != null;
        chart.setData(data);
        chart.setDescription(getString(R.string.statistics_chart_title));
        chart.animateXY(2000, 2000);
        chart.invalidate();
    }


    private ArrayList<LineDataSet> getDataSet() {
        ArrayList<LineDataSet> dataSets = null;
        ArrayList<Entry> valueSet1 = new ArrayList<>();
        ArrayList<Entry> valueSet2 = new ArrayList<>();

        int bookingCounter[] = new int[0];
        int orderCounter[] = new int[0];
        Calendar start;
        Calendar stop;
        switch (timeFilterSelected()) {
            case 0:
                bookingCounter = new int[7];
                orderCounter = new int[7];
                start = Calendar.getInstance();
                start.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                start.set(Calendar.HOUR_OF_DAY, 0);
                start.set(Calendar.MINUTE, 0);
                start.set(Calendar.SECOND, 0);
                stop = Calendar.getInstance();
                stop.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                stop.set(Calendar.HOUR_OF_DAY, 23);
                stop.set(Calendar.MINUTE, 59);
                stop.set(Calendar.SECOND, 59);
                for(int i=0; i<7; i++) {
                    orderCounter[i] = 0;
                    bookingCounter[i] = 0;
                    for(Order o : orderList) {
                        if(o.orderDateToCalendar().after(start) && o.orderDateToCalendar().after(stop))
                            orderCounter[i]++;
                    }
                    for(TableReservation r : reservationList) {
                        if(r.getTable_reservation_date().after(start) && r.getTable_reservation_date().after(stop))
                            bookingCounter[i]++;
                    }
                    start.setTimeInMillis(start.getTimeInMillis()+(24*60*60*1000));
                    stop.setTimeInMillis(start.getTimeInMillis()+(24*60*60*1000));
                }
                break;
            case 1:
                bookingCounter = new int[Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)];
                orderCounter = new int[Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)];
                start = Calendar.getInstance();
                start.set(Calendar.DAY_OF_MONTH, 1);
                start.set(Calendar.HOUR_OF_DAY, 0);
                start.set(Calendar.MINUTE, 0);
                start.set(Calendar.SECOND, 0);
                stop = Calendar.getInstance();
                stop.set(Calendar.DAY_OF_MONTH, 1);
                stop.set(Calendar.HOUR_OF_DAY, 23);
                stop.set(Calendar.MINUTE, 59);
                stop.set(Calendar.SECOND, 59);
                for(int i=0; i<bookingCounter.length; i++) {
                    orderCounter[i] = 0;
                    bookingCounter[i] = 0;
                    for(Order o : orderList) {
                        if(o.orderDateToCalendar().after(start) && o.orderDateToCalendar().after(stop))
                            orderCounter[i]++;
                    }
                    for(TableReservation r : reservationList) {
                        if(r.getTable_reservation_date().after(start) && r.getTable_reservation_date().after(stop))
                            bookingCounter[i]++;
                    }
                    start.setTimeInMillis(start.getTimeInMillis()+(24*60*60*1000));
                    stop.setTimeInMillis(start.getTimeInMillis()+(24*60*60*1000));
                }
                break;
            case 2:
                bookingCounter = new int[12];
                orderCounter = new int[12];
                start = Calendar.getInstance();
                start.set(Calendar.HOUR_OF_DAY, 0);
                start.set(Calendar.MINUTE, 0);
                start.set(Calendar.SECOND, 0);
                stop = Calendar.getInstance();
                stop.set(Calendar.HOUR_OF_DAY, 23);
                stop.set(Calendar.MINUTE, 59);
                stop.set(Calendar.SECOND, 59);

                for(int i=0; i<12; i++) {
                    orderCounter[i] = 0;
                    bookingCounter[i] = 0;
                    start.set(Calendar.MONTH, i);
                    start.set(Calendar.DAY_OF_MONTH, 1);
                    stop.set(Calendar.MONTH, i);
                    stop.set(Calendar.DAY_OF_MONTH, stop.getActualMaximum(Calendar.DAY_OF_MONTH));
                    for(Order o : orderList) {
                        if(o.orderDateToCalendar().after(start) && o.orderDateToCalendar().after(stop))
                            orderCounter[i]++;
                    }
                    for(TableReservation r : reservationList) {
                        if(r.getTable_reservation_date().after(start) && r.getTable_reservation_date().after(stop))
                            bookingCounter[i]++;
                    }
                }
                break;
        }

        for (int i = 0; i < bookingCounter.length; i++) {
            Entry val = new Entry(bookingCounter[i], i);
            valueSet1.add(val);
        }

        for (int i = 0; i < orderCounter.length; i++) {
            Entry val = new Entry(orderCounter[i], i);
            valueSet2.add(val);
        }

        LineDataSet dataSet1 = new LineDataSet(valueSet1, getString(R.string.statistics_booking_line));
        dataSet1.setColor(Color.rgb(0, 155, 0));

        LineDataSet dataSet2 = new LineDataSet(valueSet2, getString(R.string.statistics_order_line));
        dataSet2.setColor(Color.rgb(155, 0, 0));

        dataSets = new ArrayList<>();
        dataSets.add(dataSet1);
        dataSets.add(dataSet2);

        return dataSets;
    }

    private ArrayList<String> getDataName() {
        ArrayList<String> xAxis = new ArrayList<>();

        switch (timeFilterSelected()) {
            case 0:
                xAxis.add(getString(R.string.statistics_Sun));
                xAxis.add(getString(R.string.statistics_Mon));
                xAxis.add(getString(R.string.statistics_Tue));
                xAxis.add(getString(R.string.statistics_Wed));
                xAxis.add(getString(R.string.statistics_Thu));
                xAxis.add(getString(R.string.statistics_Fri));
                xAxis.add(getString(R.string.statistics_Sat));
                break;
            case 1:
                for (int i = 1; i <= Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                    String s = "" + i;
                    s = s.length() < 2 ? "0" + s : s;
                    xAxis.add(s);
                }
                break;
            case 2:
                xAxis.add(getString(R.string.statistics_Jan));
                xAxis.add(getString(R.string.statistics_Feb));
                xAxis.add(getString(R.string.statistics_Mar));
                xAxis.add(getString(R.string.statistics_Apr));
                xAxis.add(getString(R.string.statistics_May));
                xAxis.add(getString(R.string.statistics_Jun));
                xAxis.add(getString(R.string.statistics_Jul));
                xAxis.add(getString(R.string.statistics_Aug));
                xAxis.add(getString(R.string.statistics_Sep));
                xAxis.add(getString(R.string.statistics_Oct));
                xAxis.add(getString(R.string.statistics_Nov));
                xAxis.add(getString(R.string.statistics_Dec));
                break;
        }

        return xAxis;
    }

    public void onRadioButtonClicked(View view) {
        LineChart chart = (LineChart) findViewById(R.id.chart);
        LineData data = new LineData(getDataName(), getDataSet());
        assert chart != null;
        chart.setData(data);
        chart.setDescription(getString(R.string.statistics_chart_title));
        chart.animateXY(2000, 2000);
        chart.invalidate();
    }

    private int timeFilterSelected() {
        RadioButton radioButton;

        radioButton = (RadioButton) findViewById(R.id.weekFilter);
        assert radioButton != null;
        if (radioButton.isChecked())
            return 0;

        radioButton = (RadioButton) findViewById(R.id.monthFilter);
        assert radioButton != null;
        if (radioButton.isChecked())
            return 1;

        radioButton = (RadioButton) findViewById(R.id.yearFilter);
        assert radioButton != null;
        if (radioButton.isChecked())
            return 2;

        return 0;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            OnBackUtil.clean_stack_and_go_to_main_activity(this);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return DrawerUtil.drawer_owner_not_restaurant_page(this, item, restaurantID);
    }

    private void setDrawer(User user) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        final MenuItem ownerItem = menu.findItem(R.id.nav_owner);
        MenuItem loginItem = menu.findItem(R.id.nav_login);
        MenuItem logoutItem = menu.findItem(R.id.nav_logout);
        MenuItem myProfileItem = menu.findItem(R.id.nav_my_profile);
        MenuItem myOrdersItem = menu.findItem(R.id.nav_my_orders);
        MenuItem mrResItem = menu.findItem(R.id.nav_my_reservations);
        MenuItem myReviewsItem = menu.findItem(R.id.nav_my_reviews);
        MenuItem myFavItem = menu.findItem(R.id.nav_my_favourites);

        ownerItem.setVisible(false);
        loginItem.setVisible(false);
        logoutItem.setVisible(true);
        myProfileItem.setVisible(true);
        myOrdersItem.setVisible(true);
        mrResItem.setVisible(true);
        myReviewsItem.setVisible(true);
        myFavItem.setVisible(true);

        TextView nav_username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderUsername);
        TextView nav_email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderEmail);
        ImageView nav_picture = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.navHeaderPicture);

        if (user.getOwnerUser())
            ownerItem.setVisible(true);

        nav_username.setText(user.getUser_full_name());
        nav_email.setText(user.getUser_email());
        String photoUri = user.getUser_photo_firebase_URL();

        if (photoUri == null || photoUri.equals("")) {
            Glide
                    .with(StatisticsActivity.this)
                    .load(R.drawable.blank_profile_nav)
                    .centerCrop()
                    .into(nav_picture);
        } else {
            Glide
                    .with(StatisticsActivity.this)
                    .load(photoUri)
                    .centerCrop()
                    .into(nav_picture);
        }

    }

    private void abortActivity() {
        Intent intent = new Intent(this, Restaurant_page.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("restaurant_id", restaurantID);
        startActivity(intent);
        finish();
    }
}
