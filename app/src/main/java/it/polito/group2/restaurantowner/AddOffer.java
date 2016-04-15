package it.polito.group2.restaurantowner;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.UUID;

public class AddOffer extends AppCompatActivity {

    private boolean lunch, dinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle(getString(R.string.title_add_offer));
        lunch = dinner = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_offer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_offer) {

            EditText edit_name = (EditText) findViewById(R.id.edit_offer_name);
            String name = edit_name.getText().toString();
            if(name.equals("") || name.equals("\\s+")){
                edit_name.setError(getResources().getString(R.string.offer_name_error));
                return false;
            }

            EditText edit_desc = (EditText) findViewById(R.id.edit_offer_desc);
            String desc = edit_desc.getText().toString();
            if(desc.equals("") || desc.equals("\\s+")){
                edit_desc.setError(getResources().getString(R.string.offer_desc_error));
                return false;
            }

            TextView from_view = (TextView) findViewById(R.id.text_from_date);
            String from = from_view.getText().toString();
            TextView to_view = (TextView) findViewById(R.id.text_to_date);
            String to = to_view.getText().toString();

            Intent intent = new Intent();
            intent.putExtra("name", name);
            intent.putExtra("description", desc);
            intent.putExtra("from", from);
            intent.putExtra("to", to);
            intent.putExtra("lunch", lunch);
            intent.putExtra("dinner", dinner);
            intent.putExtra("offerID", UUID.randomUUID().toString());
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }

        if(id == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.lunch_checkBox:
                if (checked)
                    lunch = true;
                break;
            case R.id.dinner_checkBox:
                if (checked)
                    dinner = true;
                break;
        }
    }

    public void showFromDatePickerDialog(View v) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dialog =  new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                TextView date = (TextView) AddOffer.this.findViewById(R.id.text_from_date);
                Calendar c = Calendar.getInstance();
                if(c.get(Calendar.YEAR) == year &&  c.get(Calendar.MONTH) == monthOfYear &&  c.get(Calendar.DAY_OF_MONTH) == dayOfMonth)
                    date.setText(getString(R.string.today));
                else {
                    date.setText(new StringBuilder()
                            .append(dayOfMonth).append("  ").append(getMonthName(monthOfYear)).append("  ")
                            .append(year).append(" "));
                }
            }
        }, year, month, day);
        dialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        dialog.show();
    }

    public void showToDatePickerDialog(View v) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dialog =  new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                TextView date = (TextView) AddOffer.this.findViewById(R.id.text_to_date);
                if(c.get(Calendar.YEAR) == year &&  c.get(Calendar.MONTH) == monthOfYear &&  c.get(Calendar.DAY_OF_MONTH) == dayOfMonth)
                    date.setText(getString(R.string.today));
                else {
                    date.setText(new StringBuilder()
                            .append(dayOfMonth).append("  ").append(getMonthName(monthOfYear)).append("  ")
                            .append(year).append(" "));
                }
            }
        }, year, month, day);

        dialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        dialog.show();
    }

    private String getMonthName(int month){
        return new DateFormatSymbols().getMonths()[month];
    }


}
