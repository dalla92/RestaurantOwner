package it.polito.group2.restaurantowner.user.restaurant_page;

import android.graphics.Color;

import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarCellView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import it.polito.group2.restaurantowner.R;

/**
 * Created by Alessio on 26/04/2016.
 */
public class MonthDecorator_CalendarPicker implements CalendarCellDecorator{
    ArrayList<Integer> closing_days = new ArrayList<>();
    Date today;

    public MonthDecorator_CalendarPicker(ArrayList<Integer> closing_days, Date today){
       this.closing_days = closing_days;
       this.today = today;
    }

    @Override
    public void decorate(CalendarCellView calendarCellView, Date date) {
        Calendar selected_calendar = Calendar.getInstance();
        selected_calendar.setTime(date);
        Calendar today_calendar = Calendar.getInstance();
        today_calendar.add(Calendar.DAY_OF_YEAR, -1);
        int day_of_week = selected_calendar.get(Calendar.DAY_OF_WEEK);
        if(this.closing_days.contains(day_of_week)){
            calendarCellView.setBackgroundColor(Color.parseColor("#8b0000"));
            calendarCellView.setSelectable(false);
        } else {
            calendarCellView.setBackgroundColor(Color.parseColor("#006400"));
        }
        if(today_calendar.getTimeInMillis() > selected_calendar.getTimeInMillis())
            calendarCellView.setBackgroundColor(Color.BLACK);
    }
}