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
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.owner.FragmentServices;

public class Filter extends AppCompatActivity {

    private CheckBox CBlunch;
    private CheckBox CBdinner;
    private CheckBox CBOneEuro;
    private CheckBox CBTwoEuro;
    private CheckBox CBThreeEuro;
    private CheckBox CBFourEuro;
    private Spinner category;
    private SeekBar seekBar;
    private TextView textView;
    private int range = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        category = (Spinner) findViewById(R.id.spinner);
        CBlunch = (CheckBox) findViewById(R.id.checkBoxLunch);
        CBdinner = (CheckBox) findViewById(R.id.checkBoxDinner);
        CBOneEuro = (CheckBox) findViewById(R.id.CBOneEuro);
        CBTwoEuro = (CheckBox) findViewById(R.id.CBTwoEuro);
        CBThreeEuro = (CheckBox) findViewById(R.id.CBThreeEuro);
        CBFourEuro = (CheckBox) findViewById(R.id.CBFourEuro);
        seekBar = (SeekBar) findViewById(R.id.seekBar1);
        textView = (TextView) findViewById(R.id.textView1);

        // seekbar and textview management
        seekBar.setMax(5000); //5  km at maximum of range
        textView.setText("Search range: " + seekBar.getProgress() + "/" + formatNumber(seekBar.getMax()));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                range = progresValue;
                textView.setText("Search range: " + formatNumber(range) + "/" + seekBar.getMax());
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textView.setText("Covered: " + range + "/" + seekBar.getMax());
                Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
            }
        });

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.filter_categories_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        category.setAdapter(adapter);

    }

    private String formatNumber(double distance) {
        String unit = "m";
        if (distance < 1) {
            distance *= 1000;
            unit = "mm";
        } else if (distance > 1000) {
            distance /= 1000;
            unit = "km";
        }

        //return String.format("%4.3f%s", distance, unit);
        //trying to add space to split later
        return String.format("%4.3f %s", distance, unit);
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
            intent.putExtra("OneEuro", CBOneEuro.isChecked());
            intent.putExtra("TwoEuro", CBTwoEuro.isChecked());
            intent.putExtra("ThreeEuro", CBThreeEuro.isChecked());
            intent.putExtra("FourEuro", CBFourEuro.isChecked());
            intent.putExtra("range", range);
            intent.putExtra("Lunch", CBlunch.isChecked());
            intent.putExtra("Dinner", CBdinner.isChecked());
            if(category.getSelectedItemPosition()==0)
                intent.putExtra("Category", "0");
            else
                intent.putExtra("Category", String.valueOf(category.getSelectedItem()));
            setResult(RESULT_OK, intent);
            finish();//finishing activity
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();//finishing activity
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
