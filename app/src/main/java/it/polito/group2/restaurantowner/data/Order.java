package it.polito.group2.restaurantowner.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class Order implements Serializable {

    private String orderID;
    private String restaurantID;
    private String userID;
    private Calendar timestamp;
    private Float price;
    private String note;
    private ArrayList<OrderMeal> mealList = new ArrayList<>();

    public Order() {
    }

    public Order(String restaurantID, String userID) {
        this.restaurantID = restaurantID;
        this.userID = userID;
    }

    public Order(String orderID, String restaurantID, String userID, Calendar timestamp, Float price, String note, ArrayList<OrderMeal> mealList) {
        this.orderID = orderID;
        this.restaurantID = restaurantID;
        this.userID = userID;
        this.timestamp = timestamp;
        this.price = price;
        this.note = note;
        this.mealList = mealList;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Calendar getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Calendar timestamp) {
        this.timestamp = timestamp;
    }

    public Float getPrice() { return price; }

    public void setPrice(Float price) { this.price = price; }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ArrayList<OrderMeal> getMealList() {
        return mealList;
    }

    public void setMealList(ArrayList<OrderMeal> mealList) {
        this.mealList = mealList;
    }

}
