package it.polito.group2.restaurantowner.firebasedata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Alessio on 16/05/2016.
 */
public class Order implements Serializable {

    private String order_id;
    private String restaurant_id;
    private String user_id;
    private String user_full_name;
    private Calendar order_date;
    private String order_notes;
    private ArrayList<Meal> order_meals;
    private Double order_price;

    public Order(){

    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_full_name() {
        return user_full_name;
    }

    public void setUser_full_name(String user_full_name) {
        this.user_full_name = user_full_name;
    }

    public Calendar getOrder_date() {
        return order_date;
    }

    public void setOrder_date(Calendar order_date) {
        this.order_date = order_date;
    }

    public String getOrder_notes() {
        return order_notes;
    }

    public void setOrder_notes(String order_notes) {
        this.order_notes = order_notes;
    }

    public ArrayList<Meal> getOrder_meals() {
        return order_meals;
    }

    public void setOrder_meals(ArrayList<Meal> order_meals) {
        this.order_meals = order_meals;
    }

    public Double getOrder_price() {
        return order_price;
    }

    public void setOrder_price(Double order_price) {
        this.order_price = order_price;
    }
}
