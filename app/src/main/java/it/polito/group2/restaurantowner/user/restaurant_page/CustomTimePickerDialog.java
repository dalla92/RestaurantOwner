package it.polito.group2.restaurantowner.user.restaurant_page;

/**
 * Created by Alessio on 27/04/2016.
 */
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import it.polito.group2.restaurantowner.firebasedata.RestaurantTimeSlot;

public class CustomTimePickerDialog extends TimePickerDialog {

    private final static int TIME_PICKER_INTERVAL = 10;
    private TimePicker timePicker;
    private final OnTimeSetListener callback;
    private String weekday;
    String restaurant_id;
    private ArrayList<RestaurantTimeSlot> open_times = new ArrayList<>();

    public CustomTimePickerDialog(Context context, String weekday, ArrayList<RestaurantTimeSlot> open_times, String restaurant_id, OnTimeSetListener callBack,
                                  int hourOfDay, int minute, boolean is24HourView) {
        super(context, TimePickerDialog.THEME_HOLO_LIGHT, callBack, hourOfDay, minute / TIME_PICKER_INTERVAL,
                is24HourView);
        this.weekday = weekday;
        this.open_times = open_times;
        this.callback = callBack;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (callback != null && timePicker != null) {
            timePicker.clearFocus();
            callback.onTimeSet(timePicker, timePicker.getCurrentHour(),
                    timePicker.getCurrentMinute() * TIME_PICKER_INTERVAL);
        }
    }

    @Override
    protected void onStop() {
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            //restrict minutes interval
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            Field timePickerField = classForid.getField("timePicker");
            this.timePicker = (TimePicker) findViewById(timePickerField
                    .getInt(null));
            Field field = classForid.getField("minute");

            NumberPicker mMinuteSpinner = (NumberPicker) timePicker
                    .findViewById(field.getInt(null));
            mMinuteSpinner.setMinValue(0);
            mMinuteSpinner.setMaxValue((60 / TIME_PICKER_INTERVAL) - 1);
            List<String> displayed_minutes_values = new ArrayList<String>();
            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                displayed_minutes_values.add(String.format("%02d", i));
            }
            mMinuteSpinner.setDisplayedValues(displayed_minutes_values
                    .toArray(new String[0]));

            //restrict hours interval
            if(open_times != null && !open_times.isEmpty()) {
                Class<?> classForid2 = Class.forName("com.android.internal.R$id");
                Field timePickerField2 = classForid2.getField("timePicker");
                this.timePicker = (TimePicker) findViewById(timePickerField2
                        .getInt(null));
                Field field2 = classForid2.getField("hour");
                NumberPicker mHourSpinner = (NumberPicker) timePicker
                        .findViewById(field2.getInt(null));
                mHourSpinner.setMinValue(0);
                mHourSpinner.setMaxValue(24);
                //find open hours of that weekday
                List<String> displayed_hours_values = new ArrayList<String>();
                //find hours of lunch
                for (RestaurantTimeSlot o : this.open_times) {
                        if (o.getDay_of_week()+1 == Integer.parseInt(weekday)) {
                            if (o.getLunch()==true) {
                                int open_time = Integer.parseInt(o.getOpen_lunch_time().substring(0, 2)); //I take only the hour because minutes are fixed to 00
                                int close_time = Integer.parseInt(o.getClose_lunch_time().substring(0, 2)); //I take only the hour because minutes are fixed to 00
                                for (int i = open_time; i < close_time; i++) { //-1 because at that hous it closes, and minutes of previous hour arrive to 50
                                    displayed_hours_values.add(String.valueOf(i));
                                }
                            }
                        }
                }
                //find hours of dinner
                for (RestaurantTimeSlot o : this.open_times) {
                    if (o.getDay_of_week()+1 == Integer.parseInt(weekday)) {
                        if (o.getDinner()==true) {
                                int open_time = Integer.parseInt(o.getOpen_dinner_time().substring(0, 2)); //I take only the hour because minutes are fixed to 00
                                int close_time = Integer.parseInt(o.getClose_dinner_time().substring(0, 2)); //I take only the hour because minutes are fixed to 00
                                for (int i = open_time; i < close_time; i++) { //-1 because at that hous it closes, and minutes of previous hour arrive to 50
                                    displayed_hours_values.add(String.valueOf(i));
                                }
                            }
                        }
                }
                if(displayed_hours_values != null && !displayed_hours_values.isEmpty()) {
                    mHourSpinner.setMaxValue(displayed_hours_values.size()-1);
                    mHourSpinner.setDisplayedValues(displayed_hours_values
                            .toArray(new String[0]));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}