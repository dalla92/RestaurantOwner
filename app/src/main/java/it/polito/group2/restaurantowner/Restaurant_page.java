package it.polito.group2.restaurantowner;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class Restaurant_page extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Menu menu;

    private RecyclerView rv;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<Comment> comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_page);

        //navigation drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //prepare enlarged image option
        ImageView imageview = (ImageView) findViewById(R.id.image_to_enlarge);
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fullScreenIntent = new Intent(getApplicationContext(), Enlarged_image.class);
                fullScreenIntent.putExtra(Enlarged_image.class.getName(), R.id.image_to_enlarge);
                startActivity(fullScreenIntent);
            }
        });


        //cardview implementation
        rv=(RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        initializeData();
        initializeAdapter();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_restaurant, menu);

        this.menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {

            case R.id.action_menu:
                Intent intent1 = new Intent(
                        getApplicationContext(),
                        MenuRestaurant_page.class);
                startActivity(intent1);

                return true;

            case R.id.action_more:
                Intent intent2 = new Intent(
                        getApplicationContext(),
                        MoreRestaurantInfo.class);
                startActivity(intent2);

                return true;

            case R.id.action_edit:
                //Intent intent = new Intent(this,AddRestaurantActivity.class);
                //startActivity(intent);

                //menu item show/hide
                MenuItem action_confirm_item1 = menu.findItem(R.id.action_confirm);
                action_confirm_item1.setVisible(true);
                MenuItem action_edit_item1 = menu.findItem(R.id.action_edit);
                action_edit_item1.setVisible(false);
                //edit text clickable yes/not
                EditText edit_restaurant_name1 = (EditText) findViewById(R.id.edit_restaurant_name);
                EditText edit_restaurant_address1 = (EditText) findViewById(R.id.edit_restaurant_address);
                EditText edit_restaurant_telephone_number1 = (EditText) findViewById(R.id.edit_restaurant_telephone_number);
                edit_restaurant_name1.setFocusableInTouchMode(true);
                edit_restaurant_name1.setFocusable(true);
                edit_restaurant_name1.setAlpha(1);
                edit_restaurant_address1.setFocusableInTouchMode(true);
                edit_restaurant_address1.setFocusable(true);
                edit_restaurant_address1.setAlpha(1);
                edit_restaurant_telephone_number1.setFocusableInTouchMode(true);
                edit_restaurant_telephone_number1.setFocusable(true);
                edit_restaurant_telephone_number1.setAlpha(1);
                //button present yes/not
                Button button_take_photo1 = (Button) findViewById(R.id.button_take_photo);
                button_take_photo1.setVisibility(View.VISIBLE);
                Button button_choose_photo1 = (Button) findViewById(R.id.button_choose_photo);
                button_choose_photo1.setVisibility(View.VISIBLE);

                return true;

            case R.id.action_confirm:
                //Intent intent = new Intent(this,AddRestaurantActivity.class);
                //startActivity(intent);

                //menu item show/hide
                MenuItem action_confirm_item2 = menu.findItem(R.id.action_confirm);
                action_confirm_item2.setVisible(false);
                MenuItem action_edit_item2 = menu.findItem(R.id.action_edit);
                action_edit_item2.setVisible(true);
                //edit text clickable yes/not
                EditText edit_restaurant_name2 = (EditText) findViewById(R.id.edit_restaurant_name);
                EditText edit_restaurant_address2 = (EditText) findViewById(R.id.edit_restaurant_address);
                EditText edit_restaurant_telephone_number2 = (EditText) findViewById(R.id.edit_restaurant_telephone_number);
                edit_restaurant_name2.setFocusableInTouchMode(false);
                edit_restaurant_name2.setFocusable(false);
                edit_restaurant_name2.setAlpha(0);
                edit_restaurant_address2.setFocusableInTouchMode(false);
                edit_restaurant_address2.setFocusable(false);
                edit_restaurant_address2.setAlpha(0);
                edit_restaurant_telephone_number2.setFocusableInTouchMode(false);
                edit_restaurant_telephone_number2.setFocusable(false);
                edit_restaurant_telephone_number2.setAlpha(0);
                //button present yes/not
                Button button_take_photo2 = (Button) findViewById(R.id.button_take_photo);
                button_take_photo2.setVisibility(View.GONE);
                Button button_choose_photo2 = (Button) findViewById(R.id.button_choose_photo);
                button_choose_photo2.setVisibility(View.GONE);

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            // TODO Handle the logout action
        } else if (id == R.id.nav_manage) {
            // TODO Handle the manage action
        } else if (id == R.id.one_restaurant) {
            Intent intent = new Intent(
                    getApplicationContext(),
                    Restaurant_page.class);
            startActivity(intent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // This method creates an ArrayList that has three Comment objects
    // Checkout the project associated with this tutorial on Github if
    // you want to use the same images.
    private void initializeData(){
        comments = new ArrayList<>();
        comments.add(new Comment("Turi Lecce", "01/02/2003", 1, R.mipmap.ic_launcher, "Ah chi ni sacciu mba"));
        comments.add(new Comment("Iaffiu u cuttu", "11/06/2002", 2.7, R.mipmap.money_icon, "Cosa assai"));
        comments.add(new Comment("Iano Papale", "21/12/2001", 5, R.mipmap.image_preview_black, "Fussi pi mia ci tunnassi, ma appi problemi cu me soggira ca ogni bota si lassa curriri de scali, iu no sacciu va."));
        comments.add(new Comment("Tano Sghei", "22/05/2000", 3.4, R.mipmap.image_preview_black, "M'uccullassi n'autra vota. Turi ci emu?"));
    }



    private void initializeAdapter(){
        RVAdapter adapter = new RVAdapter(comments);
        rv.setAdapter(adapter);
    }
}
