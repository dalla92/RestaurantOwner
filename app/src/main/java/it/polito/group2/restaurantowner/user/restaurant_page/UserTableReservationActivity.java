package it.polito.group2.restaurantowner.user.restaurant_page;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.JSONUtil;
import it.polito.group2.restaurantowner.data.OpenTime;
import it.polito.group2.restaurantowner.data.TableReservation;
import it.polito.group2.restaurantowner.data.Restaurant;

import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarPickerView;
/**
 * Created by Alessio on 25/04/2016.
 */
public class UserTableReservationActivity extends AppCompatActivity implements View.OnClickListener{

    private String restaurant_id;
    private Restaurant current_restaurant = null;
    private TableReservation current_table_reservation = null;
    Toolbar toolbar;
    private String current_year, current_month, current_day, current_hour, current_minute;
    private String chosen_year, chosen_month, chosen_day, chosen_hour, chosen_minute, chosen_weekday;
    Button timepicker_button;
    Button datepicker_button;
    String current_username = null;
    private NumberPicker guests_number;
    private EditText notes = null;
    private CalendarView calendar_view = null;
    private Date date;
    private ArrayList<OpenTime> open_times = new ArrayList<>();
    private Date mMinDate;
    private TextView date_reserved_text;
    private TextView time_reserved_text;
    private ArrayList<Integer> closing_days;
    private CalendarPickerView calendar;
    private Context context;
    private List<CalendarCellDecorator> decoratorList;
    private Calendar last_chosen_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_table_reservation);
        initToolBar();
        context = this;

        //remove focus from edittext
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //get data
        Intent intent = getIntent();
        current_restaurant = (Restaurant) intent.getExtras().get("Restaurant");
        try {
            open_times = JSONUtil.readJSONOpenTimeList(this, current_restaurant.getRestaurantId());
        }
        catch(JSONException e){
            Log.e("EXCEPTION", "JSON EXCEPTION WHILE READING OPENTIMELIST IN ONCREATE");
        }
        if(current_restaurant.getTableNum().equals("0") || current_restaurant.getTableNum()==null){
            new AlertDialog.Builder(context)
                    .setMessage(R.string.no_table_reservation_service)
                    .setPositiveButton(
                            getResources().getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Intent intent = new Intent(
                                            getApplicationContext(),
                                            UserRestaurantActivity.class);
                                    startActivity(intent);
                                }
                            }
                    )
                    .setMessage(
                            getResources().getString(R.string.table_reservation_confirmation_message2))
                    .show();
        }
        else {
            //till Daniel solves bugs about Restaurant Add data
            current_restaurant.setOrdersPerHour("20");
            current_restaurant.setTableReservation(true);
            current_restaurant.setTableNum("100");
            //initialization
            current_table_reservation = new TableReservation();
            current_username = "0";
            notes = (EditText) findViewById(R.id.table_reservation_notes);
            try {
                current_table_reservation.setRestaurantId(current_restaurant.getRestaurantId());
                current_table_reservation.setTableReservationId(UUID.randomUUID().toString());
                current_table_reservation.setUsername(current_username);
            } catch (NullPointerException e) {
                Log.e("EXCEPTION", "I DETECTED A NULL POINTER EXCEPTION IN ON CREATE");
            }

            //numberpicker
            guests_number = (NumberPicker) findViewById(R.id.guests_number_picker);
            guests_number.setValue(0);
            guests_number.setMaxValue(0);
            guests_number.setMinValue(0);
            guests_number.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    if (guests_number.getMaxValue() == 0)
                        Toast.makeText(context, R.string.set_time_first, Toast.LENGTH_SHORT).show();
                }
            });
            guests_number.setOnScrollListener(new NumberPicker.OnScrollListener() {
                @Override
                public void onScrollStateChange(NumberPicker view, int scrollState) {
                    if (guests_number.getMaxValue() == 0)
                        Toast.makeText(context, R.string.set_time_first, Toast.LENGTH_SHORT).show();
                }
            });

            //text fields
            date_reserved_text = (TextView) findViewById(R.id.date_reserved);
            time_reserved_text = (TextView) findViewById(R.id.time_reserved);

            //timepicker
            // Get Current Time
            final Calendar today = Calendar.getInstance();
            current_hour = Integer.toString(today.get(Calendar.HOUR_OF_DAY));
            current_minute = Integer.toString(today.get(Calendar.MINUTE));
            chosen_hour = null; //current_hour;
            chosen_minute = null; //current_minute;
            timepicker_button = (Button) findViewById(R.id.table_reservation_time);
            timepicker_button.setOnClickListener(this);

            //datepicker
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            current_year = Integer.toString(c.get(Calendar.YEAR));
            current_month = Integer.toString(c.get(Calendar.MONTH));
            current_day = Integer.toString(c.get(Calendar.DAY_OF_MONTH));
            chosen_year = null; //current_year;
            chosen_month = null; //current_month;
            chosen_day = null; //current_day;
            chosen_weekday = null; //current_day;
            datepicker_button = (Button) findViewById(R.id.table_reservation_date);
            datepicker_button.setOnClickListener(this);

            //searching closing days
            closing_days = new ArrayList<>();
            for (OpenTime o : open_times) {
                if (o.getRestaurantId().equals(current_table_reservation.getRestaurantId()))
                    if (o.isOpen() == false)
                        closing_days.add(o.getDayOfWeek());
            }
        }

    }

    @Override
    public void onClick(View v) {
        if(v == timepicker_button){
            getTime();
        }
        if(v == datepicker_button){
            getDate();
        }
    }

    public void getDate(){
        // Launch Calendar Picker Dialog
        final Dialog dialog = new Dialog(this, R.style.PauseDialog);
        dialog.setContentView(R.layout.calendar_picker_view);
        /*
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        */
        Calendar next_two_months = Calendar.getInstance();
        next_two_months.add(Calendar.MONTH, 2);
        calendar = (CalendarPickerView) dialog.findViewById(R.id.calendar_view);
        Date today = new Date();
        calendar.init(today, next_two_months.getTime()).withSelectedDate(today);
        //decorate calendar
        decoratorList = new ArrayList<>();
        decoratorList.add(new MonthDecorator_CalendarPicker(closing_days, today));
        calendar.setDecorators(decoratorList);
        calendar.setBackgroundResource(R.color.coloPrimaryLight);
        calendar.setOnScrollListener(new AbsListView.OnScrollListener() {
                                         @Override
                                         public void onScrollStateChanged(AbsListView view, int scrollState) {
                                            //calendar.setDecorators(decoratorList);
                                             //view.invalidate();
                                         }

                                         @Override
                                         public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                                             //calendar.setDecorators(decoratorList);
                                             //view.invalidate();
                                         }
                                     });
        mMinDate = new Date(System.currentTimeMillis() - 1000);
        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                Calendar selected_calendar = Calendar.getInstance();
                selected_calendar.setTime(date);
                chosen_day = Integer.toString(selected_calendar.get(Calendar.DAY_OF_MONTH));
                chosen_weekday = Integer.toString(selected_calendar.get(Calendar.DAY_OF_WEEK));
                chosen_month = Integer.toString(selected_calendar.get(Calendar.MONTH) + 1); //need to correct +1
                chosen_year = Integer.toString(selected_calendar.get(Calendar.YEAR));
                date_reserved_text.setText(chosen_day + "/" + chosen_month + "/" + chosen_year);
                time_reserved_text.setText(R.string.insert_time);
                guests_number.setMaxValue(0);
                guests_number.setValue(0);
                /*
                chosen_day = Integer.toString(selected_calendar.DAY_OF_MONTH);
                chosen_month = Integer.toString(selected_calendar.MONTH);
                chosen_year = Integer.toString(selected_calendar.YEAR);
                */
                dialog.dismiss();
            }

            @Override
            public void onDateUnselected(Date date) {
                chosen_day = null;
                chosen_month = null;
                chosen_year = null;
                chosen_weekday = null;
            }
        });
        dialog.getWindow().setBackgroundDrawableResource(R.color.colorPrimary);
        dialog.setTitle(R.string.calendar);
        dialog.show();
    }

    public void getTime(){
        if(chosen_weekday == null) { //date calendar is not set
            Toast.makeText(context, R.string.set_date_first, Toast.LENGTH_SHORT).show();
            Log.d("what", "what");
        }
        else {
            // Launch Time Picker Dialog with current hour and minute
            CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(this, chosen_weekday, open_times, current_table_reservation.getRestaurantId(),
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            chosen_hour = Integer.toString(hourOfDay);
                            chosen_minute = Integer.toString(minute);
                            time_reserved_text.setText(chosen_hour + ":" + chosen_minute);
                            find_max_guests();
                        }
                    }, Integer.parseInt(current_hour), Integer.parseInt(current_minute), true); //true because I want 24H
            timePickerDialog.show();
        }
    }

    public void find_max_guests(){
        int orders_per_hour = Integer.parseInt(current_restaurant.getOrdersPerHour());
        int max_guests = Integer.parseInt(current_restaurant.getTableNum())*4; //I do not have the information about the number of max_guests, so I suppose that each table is composed by 4 chairs
        //I do not have either the information about the number of tables/hour, so I suppose that at each clock hour I wil have full capacity
        //compose chosen_date_reservation
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            cal.setTime(sdf.parse(chosen_day + "/" + chosen_month + "/" + chosen_year + " " + chosen_hour + ":" + chosen_minute));
        } catch (java.text.ParseException e) {
            Log.e("EXCEPTION", "SDF.PARSE RAISED AN EXCEPTION IN onOptionsItemSelected");
        }
        if (cal.before(mMinDate)) {
            Log.e("EXCEPTION", "mMinDate: " + mMinDate.getTime()
                    + " does not precede cal: " + date.getTime() + " IN onOptionsItemSelected");
        }
        ArrayList<TableReservation> reservations_that_day = getDataJson(cal, current_restaurant.getRestaurantId());
        //counter of reservations with that hour (that day)
        int already_booked_guests=0;
        for(TableReservation tr : reservations_that_day){
            int hour = tr.getDate().HOUR;
            if(hour == Integer.parseInt(chosen_hour)){
                already_booked_guests+= tr.getN_people();
            }
        }
        if(already_booked_guests >= max_guests){
            guests_number.setMaxValue(0);
            guests_number.setValue(0);
        }
        else{
            guests_number.setMaxValue(max_guests - already_booked_guests);
            guests_number.setValue(1);
        }
    }

    private ArrayList<TableReservation> getDataJson(Calendar date, String restaurantId) {
        /*ArrayList<TableReservation> reservations = new ArrayList<>();
        TableReservation res1 = new TableReservation("Andrea Cuiuli", 4 , date, "Una persona allergica alle noci", restaurantId);
        TableReservation res2 = new TableReservation("Andrea Cuiuli", 4 , date, "Una persona allergica alle noci", restaurantId);
        TableReservation res3 = new TableReservation("Andrea Cuiuli", 4 , date, "Una persona allergica alle noci", restaurantId);
        reservations.add(res1);
        reservations.add(res2);
        reservations.add(res3);*/
        try {
            return JSONUtil.readJSONTableResList(context, date, restaurantId);
        } catch (JSONException e) {
            return new ArrayList<>();
        }
    }

    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_table_reservation_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void showConfirmationDialog() throws Resources.NotFoundException {
        context = this;
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.table_reservation_confirmation))
                .setMessage(
                        getResources().getString(R.string.table_reservation_confirmation_message))
                .setIcon(
                        getResources().getDrawable(
                                android.R.drawable.ic_dialog_alert))
                .setPositiveButton(
                        getResources().getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                //save (write file)
                                ArrayList<TableReservation> all_table_reservations = getDataJson(null, null);
                                all_table_reservations.add(current_table_reservation);
                                try {
                                    JSONUtil.saveJSONTableResList(context, all_table_reservations);
                                } catch (JSONException e) {
                                    Log.e("EXCEPTION", "EXCETPION IN WRITING THE FILE IN showConfirmationDialog");
                                }
                                //readdress after feedback
                                new AlertDialog.Builder(context)
                                        .setPositiveButton(
                                                getResources().getString(R.string.ok),
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog,
                                                                        int which) {
                                                        Intent intent = new Intent(
                                                                getApplicationContext(),
                                                                UserRestaurantActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }
                                        )
                                        .setMessage(
                                                getResources().getString(R.string.table_reservation_confirmation_message2))
                                        .show();
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

                            @Override
                            public boolean onCreateOptionsMenu(Menu menu) {
                                getMenuInflater().inflate(R.menu.menu_user_table_reservation, menu);
                                return true;
                            }

                            @Override
                            public boolean onOptionsItemSelected(MenuItem item) {
                                int id = item.getItemId();
                                switch (item.getItemId()) {

                                    case R.id.table_reservation:
                                        try {
                                            last_chosen_date = Calendar.getInstance();
                                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                            try {
                                                last_chosen_date.setTime(sdf.parse(chosen_day + "/" + chosen_month + "/" + chosen_year + " " + chosen_hour + ":" + chosen_minute));
                                            } catch (java.text.ParseException e) {
                                                Log.e("EXCEPTION", "SDF.PARSE RAISED AN EXCEPTION IN onOptionsItemSelected");
                                            }
                                            if (last_chosen_date.before(mMinDate)) {
                                                Log.e("EXCEPTION", "mMinDate: " + mMinDate.getTime()
                                                        + " does not precede last_chosen_date: " + date.getTime() + " IN onOptionsItemSelected");
                                            }
                                            else
                                                current_table_reservation.setDate(last_chosen_date);
                                            current_table_reservation.setN_people(guests_number.getValue());
                                            if(notes.getText()!=null)
                                                current_table_reservation.setNotes(notes.getText().toString());
                                        }
                                        catch(NullPointerException e){
                                            Log.e("EXCEPTION", "NULL POINTER EXCEPTION IN onOptionsItemSelected");
                                        }
                                        if(current_table_reservation.getDate() != null
                                                && current_table_reservation.getN_people() !=0
                                                && current_table_reservation.getRestaurantId() != null
                                                && current_table_reservation.getTableReservationId() != null
                                                && current_table_reservation.getUsername() != null
                                                && chosen_hour != null
                                                && chosen_minute !=null) {

                                            showConfirmationDialog();
                                        }
                                        else
                                            Toast.makeText(this, R.string.invalid_or_incomplete_data, Toast.LENGTH_SHORT).show();
                                        return true;

                                    default:
                                        // If we got here, the user's action was not recognized.
                                        // Invoke the superclass to handle it.
                                        return super.onOptionsItemSelected(item);

                                }
                            }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("chosen_hour", chosen_hour);
        outState.putString("chosen_minute", chosen_minute);
        outState.putString("chosen_year", chosen_year);
        outState.putString("chosen_month", chosen_month);
        outState.putString("chosen_day", chosen_day);
        outState.putInt("guests_number", guests_number.getValue());
        outState.putString("notes", notes.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        chosen_hour = savedInstanceState.getString("chosen_hour");
        chosen_minute = savedInstanceState.getString("chosen_minute");
        chosen_year = savedInstanceState.getString("chosen_year");
        chosen_month = savedInstanceState.getString("chosen_month");
        chosen_day = savedInstanceState.getString("chosen_day");
        guests_number.setValue(savedInstanceState.getInt("guests_number"));
        notes.setText(savedInstanceState.getString("notes"));

        date_reserved_text = (TextView) findViewById(R.id.date_reserved);
        if(chosen_day!=null && chosen_month != null && chosen_year!=null)
            date_reserved_text.setText(chosen_day + "/" + chosen_month + "/" + chosen_year);

        time_reserved_text = (TextView) findViewById(R.id.time_reserved);
        if(chosen_hour!=null && chosen_minute != null)
            time_reserved_text.setText(chosen_hour + ":" + chosen_minute);

    }

}
