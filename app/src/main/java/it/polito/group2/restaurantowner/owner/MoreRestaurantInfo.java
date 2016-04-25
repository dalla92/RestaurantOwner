package it.polito.group2.restaurantowner.owner;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import it.polito.group2.restaurantowner.R;

public class MoreRestaurantInfo extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_restaurant_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //feed spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner_restaurant_type);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.restaurant_type_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        //spinner edit yes/no
        spinner.setEnabled(false);
        spinner.setClickable(false);
        spinner.setFocusableInTouchMode(false);
        spinner.setFocusableInTouchMode(false);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_restaurant_more_info, menu);

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

            case R.id.action_edit:
                //Intent intent = new Intent(this,AddRestaurantActivity.class);
                //startActivity(intent);

                //menu item show/hide
                MenuItem action_confirm_item1 = menu.findItem(R.id.action_confirm);
                action_confirm_item1.setVisible(true);
                MenuItem action_edit_item1 = menu.findItem(R.id.action_edit);
                action_edit_item1.setVisible(false);
                //edit text clickable yes/not
                EditText edit_seating_capacity1 = (EditText) findViewById(R.id.edit_seating_capacity);
                EditText edit_squared_metres1 = (EditText) findViewById(R.id.edit_squared_metres);
                EditText edit_closest_metro1 = (EditText) findViewById(R.id.edit_closest_metro);
                EditText edit_closest_bus1 = (EditText) findViewById(R.id.edit_closest_bus);
                edit_seating_capacity1.setFocusableInTouchMode(true);
                edit_seating_capacity1.setFocusable(true);
                edit_seating_capacity1.setAlpha(1);
                edit_squared_metres1.setFocusableInTouchMode(true);
                edit_squared_metres1.setFocusable(true);
                edit_squared_metres1.setAlpha(1);
                edit_closest_metro1.setFocusableInTouchMode(true);
                edit_closest_metro1.setFocusable(true);
                edit_closest_metro1.setAlpha(1);
                edit_closest_bus1.setFocusableInTouchMode(true);
                edit_closest_bus1.setFocusable(true);
                edit_closest_bus1.setAlpha(1);
                //edit checkbox clickable
                CheckBox check_inside = (CheckBox) findViewById(R.id.inside);
                CheckBox check_celiac = (CheckBox) findViewById(R.id.check_celiac);
                CheckBox check_vegan = (CheckBox) findViewById(R.id.check_vegan);
                CheckBox check_vegetarian = (CheckBox) findViewById(R.id.check_vegetarian);
                CheckBox credit_card = (CheckBox) findViewById(R.id.credit_card);
                CheckBox take_away = (CheckBox) findViewById(R.id.take_away);
                CheckBox booking_via_telephone = (CheckBox) findViewById(R.id.booking_via_telephone);
                CheckBox wi_fi = (CheckBox) findViewById(R.id.wi_fi);
                CheckBox outside = (CheckBox) findViewById(R.id.outside);
                check_inside.setClickable(true);
                check_celiac.setClickable(true);
                check_vegan.setClickable(true);
                check_vegetarian.setClickable(true);
                credit_card.setClickable(true);
                take_away.setClickable(true);
                booking_via_telephone.setClickable(true);
                wi_fi.setClickable(true);
                outside.setClickable(true);
                //spinner edit yes/no
                Spinner spinner1 = (Spinner) findViewById(R.id.spinner_restaurant_type);
                spinner1.setEnabled(true);
                spinner1.setClickable(true);
                spinner1.setFocusable(true);
                spinner1.setFocusableInTouchMode(true);


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
                EditText edit_seating_capacity2 = (EditText) findViewById(R.id.edit_seating_capacity);
                EditText edit_squared_metres2 = (EditText) findViewById(R.id.edit_squared_metres);
                EditText edit_closest_metro2 = (EditText) findViewById(R.id.edit_closest_metro);
                EditText edit_closest_bus2 = (EditText) findViewById(R.id.edit_closest_bus);
                edit_seating_capacity2.setFocusableInTouchMode(false);
                edit_seating_capacity2.setFocusable(false);
                edit_seating_capacity2.setAlpha(0);
                edit_squared_metres2.setFocusableInTouchMode(false);
                edit_squared_metres2.setFocusable(false);
                edit_squared_metres2.setAlpha(0);
                edit_closest_metro2.setFocusableInTouchMode(false);
                edit_closest_metro2.setFocusable(false);
                edit_closest_metro2.setAlpha(0);
                edit_closest_bus2.setFocusableInTouchMode(false);
                edit_closest_bus2.setFocusable(false);
                edit_closest_bus2.setAlpha(0);
                //edit checkbox clickable
                CheckBox check_inside2 = (CheckBox) findViewById(R.id.inside);
                CheckBox check_celiac2 = (CheckBox) findViewById(R.id.check_celiac);
                CheckBox check_vegan2 = (CheckBox) findViewById(R.id.check_vegan);
                CheckBox check_vegetarian2 = (CheckBox) findViewById(R.id.check_vegetarian);
                CheckBox credit_card2 = (CheckBox) findViewById(R.id.credit_card);
                CheckBox take_away2 = (CheckBox) findViewById(R.id.take_away);
                CheckBox booking_via_telephone2 = (CheckBox) findViewById(R.id.booking_via_telephone);
                CheckBox wi_fi2 = (CheckBox) findViewById(R.id.wi_fi);
                CheckBox outside2 = (CheckBox) findViewById(R.id.outside);
                check_inside2.setClickable(false);
                check_celiac2.setClickable(false);
                check_vegan2.setClickable(false);
                check_vegetarian2.setClickable(false);
                credit_card2.setClickable(false);
                take_away2.setClickable(false);
                booking_via_telephone2.setClickable(false);
                wi_fi2.setClickable(false);
                outside2.setClickable(false);
                //spinner edit yes/no
                Spinner spinner2 = (Spinner) findViewById(R.id.spinner_restaurant_type);
                spinner2.setEnabled(false);
                spinner2.setClickable(false);
                spinner2.setFocusable(false);
                spinner2.setFocusableInTouchMode(false);

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}
