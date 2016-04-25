package it.polito.group2.restaurantowner.owner;

import android.graphics.Color;

import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import com.github.mikephil.charting.charts.LineChart;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import android.support.v7.widget.Toolbar;

import it.polito.group2.restaurantowner.R;

public class StatisticsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private int restaurantID;
    private DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        LineChart chart = (LineChart) findViewById(R.id.chart);

        //get the restaurant obj
        Bundle b = getIntent().getExtras();
        if(b!=null) {
            restaurantID = b.getInt("restaurantID");
            LineData data = new LineData(getDataName(), getDataSet());
            chart.setData(data);
            chart.setDescription(getString(R.string.statistics_chart_title));
            chart.animateXY(2000, 2000);
            chart.invalidate();

        } else {
            //error on restaurant id
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    private ArrayList<LineDataSet> getDataSet() {
        ArrayList<TableReservation> bookingList = new ArrayList<>();
        ArrayList<TakeAwayReservation> orderList = new ArrayList<>();

        /*
        try {
            bookingList = readJsonBookingList();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            orderList = readJsonOrderList();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Calendar c = new GregorianCalendar();
        c.setTime(bookingList.get(0).getReservationTime());

        Date bookingCal[] = new Date[0];
        Calendar cal = new GregorianCalendar();

        switch(timeFilterSelected()) {
            case 0:
                bookingCounter = new int[7];
                orderCounter = new int[7];
                bookingCal = new Date[7];

                cal = new GregorianCalendar();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                bookingCal[0] = cal.getTime();
                bookingCal[0].setTime(bookingCal[0].getTime() - 100);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                bookingCal[1] = cal.getTime();
                bookingCal[1].setTime(bookingCal[1].getTime()-100);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                bookingCal[2] = cal.getTime();
                bookingCal[2].setTime(bookingCal[2].getTime() - 100);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                bookingCal[3] = cal.getTime();
                bookingCal[3].setTime(bookingCal[3].getTime() - 100);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                bookingCal[4] = cal.getTime();
                bookingCal[4].setTime(bookingCal[4].getTime() - 100);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                bookingCal[5] = cal.getTime();
                bookingCal[5].setTime(bookingCal[5].getTime() - 100);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                bookingCal[6] = cal.getTime();
                bookingCal[6].setTime(bookingCal[6].getTime() - 100);
                break;
            case 1:
                bookingCounter = new int[c.getActualMaximum(Calendar.DAY_OF_MONTH)];
                orderCounter = new int[c.getActualMaximum(Calendar.DAY_OF_MONTH)];
                bookingCal = new Date[c.getActualMaximum(Calendar.DAY_OF_MONTH)];
                cal = new GregorianCalendar();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                for(int i=0;i<bookingCal.length;i++) {
                    cal.set(Calendar.DAY_OF_MONTH, i+1);
                    bookingCal[i] = cal.getTime();
                    bookingCal[i].setTime(bookingCal[i].getTime() - 100);
                }
                break;
            case 2:
                bookingCounter = new int[12];
                orderCounter = new int[12];
                bookingCal = new Date[12];
                for(int i=0;i<12;i++) {
                    cal.set(Calendar.MONTH, i+1);
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    bookingCal[i] = cal.getTime();
                    bookingCal[i].setTime(bookingCal[i].getTime() - 100);
                }
                break;
        }

        for (int i=0;i<bookingCounter.length;i++) {
            bookingCounter[i] = 0;
            orderCounter[i] = 0;
        }

        for(Booking b : bookingList) {
            for (int i=0;i<bookingCal.length-1;i++) {
                if(b.getReservationTime().after(bookingCal[i]) && b.getReservationTime().before(bookingCal[i + 1])) {
                    bookingCounter[i]++;
                }
            }
            if(b.getReservationTime().after(bookingCal[bookingCal.length-1])) {
                bookingCounter[bookingCal.length-1]++;
            }
        }

        for(Order o : orderList) {
            for (int i=0;i<bookingCal.length-1;i++) {
                if(o.getOrderTime().after(bookingCal[i]) && o.getOrderTime().before(bookingCal[i + 1])) {
                    orderCounter[i]++;
                }
            }
            if(o.getOrderTime().after(bookingCal[bookingCal.length-1])) {
                orderCounter[bookingCal.length-1]++;
            }
        }
        */

        ArrayList<LineDataSet> dataSets = null;
        ArrayList<Entry> valueSet1 = new ArrayList<>();
        ArrayList<Entry> valueSet2 = new ArrayList<>();

        int bookingCounter[] = new int[0];
        int orderCounter[] = new int[0];

        switch(timeFilterSelected()) {
            case 0:
                bookingCounter = new int[7];
                orderCounter = new int[7];
                break;
            case 1:
                bookingCounter = new int[Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)];
                orderCounter = new int[Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)];
                break;
            case 2:
                bookingCounter = new int[12];
                orderCounter = new int[12];
                break;
        }

        for (int i=0;i<bookingCounter.length;i++) {
            Entry val = new Entry((float)(Math.random()*10), i);
            valueSet1.add(val);
        }

        for (int i=0;i<orderCounter.length;i++) {
            Entry val = new Entry((float)(Math.random()*10), i);
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

        switch(timeFilterSelected()) {
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
                for(int i=1; i<=Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);i++) {
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

/*
    public ArrayList<Booking> readJsonBookingList()
            throws JSONException {
        String json = null;
        ArrayList<Booking> bookingList = new ArrayList<>();
        FileInputStream fis = null;
        String FILENAME = "bookingList.json";
        try {
            fis = openFileInput(FILENAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            json = new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return bookingList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jobj = new JSONObject(json);
        JSONArray jsonArray = jobj.optJSONArray("Bookings");
        Date date;
        Booking book;

        Date dFrom = new Date();
        Date dTo = new Date();
        Calendar cal;
        switch(timeFilterSelected()) {
            case 0: //weekFilter
                cal = new GregorianCalendar();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                dFrom = cal.getTime();
                cal = new GregorianCalendar();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                dTo = cal.getTime();
                break;
            case 1: //monthFilter
                cal = new GregorianCalendar();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                dFrom = cal.getTime();
                cal = new GregorianCalendar();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                dTo = cal.getTime();
                break;
            case 2: //yearFilter
                cal = new GregorianCalendar();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.MONTH, Calendar.JANUARY);
                dFrom = cal.getTime();
                cal = new GregorianCalendar();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.DAY_OF_MONTH, 31);
                cal.set(Calendar.MONTH, Calendar.DECEMBER);
                dTo = cal.getTime();
                break;
        }

        dFrom.setTime(dFrom.getTime()-100);
        dTo.setTime(dTo.getTime()+100);

        //Iterate the jsonArray and print the info of JSONObjects
        for(int i=0; i < jsonArray.length(); i++){

            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if(Integer.getInteger(jsonObject.optString("RestaurantID")).equals(restaurantID)) {
                book = new Booking();
                date = null;
                try {
                    date = format.parse(jsonObject.optString("Date") + " " + jsonObject.optString("Time"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                book.setReservationTime(date);
                if(date.after(dFrom) && date.before(dTo)) {
                    bookingList.add(book);
                }
            }
        }
        return bookingList;
    }

    public ArrayList<Order> readJsonOrderList()
            throws JSONException {
        String json = null;
        ArrayList<Order> orderList = new ArrayList<>();
        FileInputStream fis = null;
        String FILENAME = "orderList.json";
        try {
            fis = openFileInput(FILENAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            json = new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return orderList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jobj = new JSONObject(json);
        JSONArray jsonArray = jobj.optJSONArray("Orders");
        Date date;
        Order order;

        Date dFrom = new Date();
        Date dTo = new Date();
        Calendar cal;
        switch(timeFilterSelected()) {
            case 0: //weekFilter
                cal = new GregorianCalendar();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                dFrom = cal.getTime();
                cal = new GregorianCalendar();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                dTo = cal.getTime();
                break;
            case 1: //monthFilter
                cal = new GregorianCalendar();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                dFrom = cal.getTime();
                cal = new GregorianCalendar();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                dTo = cal.getTime();
                break;
            case 2: //yearFilter
                cal = new GregorianCalendar();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.MONTH, Calendar.JANUARY);
                dFrom = cal.getTime();
                cal = new GregorianCalendar();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.DAY_OF_MONTH, 31);
                cal.set(Calendar.MONTH, Calendar.DECEMBER);
                dTo = cal.getTime();
                break;
        }

        dFrom.setTime(dFrom.getTime()-100);
        dTo.setTime(dTo.getTime()+100);

        //Iterate the jsonArray and print the info of JSONObjects
        for(int i=0; i < jsonArray.length(); i++){

            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if(Integer.getInteger(jsonObject.optString("RestaurantID")).equals(restaurantID)) {
                order = new Order();
                date = null;
                try {
                    date = format.parse(jsonObject.optString("Date") + " " + jsonObject.optString("Time"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                order.setOrderTime(date);
                if(date.after(dFrom) && date.before(dTo)) {
                    orderList.add(order);
                }
            }
        }
        return orderList;
    }
*/
    public void onRadioButtonClicked(View view) {
        LineChart chart = (LineChart) findViewById(R.id.chart);
        LineData data = new LineData(getDataName(), getDataSet());
        chart.setData(data);
        chart.setDescription(getString(R.string.statistics_chart_title));
        chart.animateXY(2000, 2000);
        chart.invalidate();
    }

    private int timeFilterSelected() {

        RadioButton radioButton = new RadioButton(this);

        radioButton = (RadioButton) findViewById(R.id.weekFilter);
        if(radioButton.isChecked())
            return 0;

        radioButton = (RadioButton) findViewById(R.id.monthFilter);
        if(radioButton.isChecked())
            return 1;

        radioButton = (RadioButton) findViewById(R.id.yearFilter);
        if(radioButton.isChecked())
            return 2;

        return 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reviews, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        /*
        int id = item.getItemId();
        if (id == R.id.nav_logout) {
            // TODO Handle the logout action
        } else if (id == R.id.nav_manage) {
            // TODO Handle the manage action
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        */
        return true;
    }
}
