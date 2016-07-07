package it.polito.group2.restaurantowner.owner.reservations;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.DrawerUtil;
import it.polito.group2.restaurantowner.Utils.OnBackUtil;

public class ReservationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private FragmentPageAdapter adapter;
    private String restaurant_id;
    public Context context;
    public int MODIFY_INFO = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        restaurant_id = getIntent().getExtras().getString("restaurant_id");

        //navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Calendar date;
        if(getIntent().getExtras().getSerializable("date") == null)
            date = Calendar.getInstance();
        else
            date = (Calendar) getIntent().getExtras().getSerializable("date");

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Reservation"));
        tabLayout.addTab(tabLayout.newTab().setText("Order"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new FragmentPageAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), date.getTimeInMillis(), restaurant_id);
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
        getMenuInflater().inflate(R.menu.menu_reservation, menu);
        //this.menu = menu;
        return true;
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return DrawerUtil.drawer_owner_not_restaurant_page(this, item, restaurant_id);
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

                    adapter.getTakeaway_fragment().changeData(c, restaurant_id);
                    adapter.getTable_fragment().changeData(c);

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

            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    private String getMonthName(int month){
        return new DateFormatSymbols().getMonths()[month];
    }

}
