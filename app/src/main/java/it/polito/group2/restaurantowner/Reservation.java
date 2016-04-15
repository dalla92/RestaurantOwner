package it.polito.group2.restaurantowner;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Reservation extends AppCompatActivity {

    private PagerAdapter adapter;
    private String restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        restaurantId = getIntent().getExtras().getString("restaurant_id");
        Calendar date;
        if(getIntent().getExtras().getSerializable("date") == null)
            date = Calendar.getInstance();
        else
            date = (Calendar) getIntent().getExtras().getSerializable("date");

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Table"));
        tabLayout.addTab(tabLayout.newTab().setText("Take Away"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), date.getTimeInMillis(), restaurantId);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reservation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.icon_reservation_cal) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog =  new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.YEAR, year);
                    c.set(Calendar.MONTH, monthOfYear);
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    adapter.getTakeaway_fragment().changeData(c, restaurantId);
                    adapter.getTable_fragment().changeData(c, restaurantId);

                    c = Calendar.getInstance();
                    if(c.get(Calendar.YEAR) == year &&  c.get(Calendar.MONTH) == monthOfYear &&  c.get(Calendar.DAY_OF_MONTH) == dayOfMonth)
                        getSupportActionBar().setTitle((getString(R.string.title_activity_reservation)));
                    else {
                        getSupportActionBar().setTitle(new StringBuilder()
                                .append(getString(R.string.title_activity_reservation))
                                .append("  (")
                                .append(dayOfMonth)
                                .append(" ").append(getMonthName(monthOfYear))
                                .append(" ")
                                .append(year).append(")"));
                    }
                }
            }, year, month, day);
            //dialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    private String getMonthName(int month){
        return new DateFormatSymbols().getMonths()[month];
    }

}
