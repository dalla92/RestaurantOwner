package it.polito.group2.restaurantowner.user.restaurant_page;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.owner.FragmentServices;

public class Filter extends AppCompatActivity {

    private CheckBox CBtime;
    private CheckBox CBOneEuro;
    private CheckBox CBTwoEuro;
    private CheckBox CBThreeEuro;
    private CheckBox CBFourEuro;
    private Spinner category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        category = (Spinner) findViewById(R.id.spinner);
        CBtime = (CheckBox) findViewById(R.id.checkBoxTime);
        CBOneEuro = (CheckBox) findViewById(R.id.CBOneEuro);
        CBTwoEuro = (CheckBox) findViewById(R.id.CBTwoEuro);
        CBThreeEuro = (CheckBox) findViewById(R.id.CBThreeEuro);
        CBFourEuro = (CheckBox) findViewById(R.id.CBFourEuro);

        CBtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CBtime.isChecked()){
                    DialogFragment newFragment = new FragmentServices.TimePickerFragment() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String time = hourOfDay + ":" + new DecimalFormat("00").format(minute);
                            CBtime.setText(time);
                        }
                    };
                    newFragment.show(getSupportFragmentManager(), "timePicker");
                }
                else{
                    CBtime.setText("Set time");
                }
            }
        });


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        category.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_save) {
            Intent intent = new Intent();
            if(CBOneEuro.isChecked())
                intent.putExtra("OneEuro", true);
            if(CBTwoEuro.isChecked())
                intent.putExtra("TwoEuro", true);
            if(CBThreeEuro.isChecked())
                intent.putExtra("ThreeEuro", true);
            if(CBFourEuro.isChecked())
                intent.putExtra("FourEuro", true);
            if(CBtime.isChecked())
                intent.putExtra("Time", CBtime.getText().toString());
            intent.putExtra("Category", String.valueOf(category.getSelectedItem()));
            setResult(RESULT_OK, intent);
            finish();//finishing activity
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
