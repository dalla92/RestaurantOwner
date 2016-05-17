package it.polito.group2.restaurantowner.firebasedata;

/**
 * Created by Alessio on 16/05/2016.
 */
public class RestaurantTimeSlot {

    private String restaurant_id;
    private boolean lunch;
    private boolean dinner; //is_open is not needed anymore because lunch and dinner are both false when close
    private int day_of_week;
    private String open_lunch_time;
    private String close_lunch_time;
    private String open_dinner_time;
    private String close_dinner_time;

    public RestaurantTimeSlot(){

    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public boolean isLunch() {
        return lunch;
    }

    public void setLunch(boolean lunch) {
        this.lunch = lunch;
    }

    public boolean isDinner() {
        return dinner;
    }

    public void setDinner(boolean dinner) {
        this.dinner = dinner;
    }

    public int getDay_of_week() {
        return day_of_week;
    }

    public void setDay_of_week(int day_of_week) {
        this.day_of_week = day_of_week;
    }

    public String getOpen_lunch_time() {
        return open_lunch_time;
    }

    public void setOpen_lunch_time(String open_lunch_time) {
        this.open_lunch_time = open_lunch_time;
    }

    public String getClose_lunch_time() {
        return close_lunch_time;
    }

    public void setClose_lunch_time(String close_lunch_time) {
        this.close_lunch_time = close_lunch_time;
    }

    public String getOpen_dinner_time() {
        return open_dinner_time;
    }

    public void setOpen_dinner_time(String open_dinner_time) {
        this.open_dinner_time = open_dinner_time;
    }

    public String getClose_dinner_time() {
        return close_dinner_time;
    }

    public void setClose_dinner_time(String close_dinner_time) {
        this.close_dinner_time = close_dinner_time;
    }
}
