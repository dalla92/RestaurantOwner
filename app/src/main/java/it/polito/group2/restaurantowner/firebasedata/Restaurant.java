package it.polito.group2.restaurantowner.firebasedata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Alessio on 16/05/2016.
 */

public class Restaurant implements Serializable {

    private String restaurant_id;
    private String user_id; //a certain owner manages this restaurant
    private String restaurant_name;
    private String restaurant_address;
    private String restaurant_telephone_number;
    private float restaurant_rating; //android:stepSize="0.01"
    private ArrayList<RestaurantTimeSlot> restaurant_time_slot;
    private HashMap<String, Boolean> favourite_users;
    private String restaurant_photo_firebase_URL; //with Glide in AsyncTask
    private String restaurant_category;
    private Boolean fidelityProgramAccepted;
    private Boolean tableReservationAllowed;
    private int restaurant_total_tables_number;
    private Boolean takeAwayAllowed;
    private int restaurant_orders_per_hour;
    private int restaurant_squared_meters;
    private String restaurant_closest_metro;
    private String restaurant_closest_bus;
    private int restaurant_price_range;
    private Boolean animalAllowed;
    private Boolean celiacFriendly;
    private Boolean tvPresent;
    private Boolean wifiPresent;
    private Boolean creditCardAccepted;
    private Boolean airConditionedPresent;
    private double restaurant_latitude_position;
    private double restaurant_longitude_position;

    public Restaurant(){

    }

    public HashMap<String, Boolean> getFavourite_users() {
        return favourite_users;
    }

    public Boolean getFidelityProgramAccepted() {
        return fidelityProgramAccepted;
    }

    public Boolean getTableReservationAllowed() {
        return tableReservationAllowed;
    }

    public Boolean getTakeAwayAllowed() {
        return takeAwayAllowed;
    }

    public Boolean getAnimalAllowed() {
        return animalAllowed;
    }

    public Boolean getCeliacFriendly() {
        return celiacFriendly;
    }

    public Boolean getTvPresent() {
        return tvPresent;
    }

    public Boolean getWifiPresent() {
        return wifiPresent;
    }

    public Boolean getCreditCardAccepted() {
        return creditCardAccepted;
    }

    public Boolean getAirConditionedPresent() {
        return airConditionedPresent;
    }

    public void setFavourite_users(HashMap<String, Boolean> favourite_users) {
        this.favourite_users = favourite_users;
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

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String restaurant_name) {
        this.restaurant_name = restaurant_name;
    }

    public String getRestaurant_address() {
        return restaurant_address;
    }

    public void setRestaurant_address(String restaurant_address) {
        this.restaurant_address = restaurant_address;
    }

    public String getRestaurant_telephone_number() {
        return restaurant_telephone_number;
    }

    public void setRestaurant_telephone_number(String restaurant_telephone_number) {
        this.restaurant_telephone_number = restaurant_telephone_number;
    }

    public float getRestaurant_rating() {
        return restaurant_rating;
    }

    public void setRestaurant_rating(float restaurant_rating) {
        this.restaurant_rating = restaurant_rating;
    }

    public ArrayList<RestaurantTimeSlot> getRestaurant_time_slot() {
        return restaurant_time_slot;
    }

    public void setRestaurant_time_slot(ArrayList<RestaurantTimeSlot> restaurant_time_slot) {
        this.restaurant_time_slot = restaurant_time_slot;
    }

    public String getRestaurant_photo_firebase_URL() {
        return restaurant_photo_firebase_URL;
    }

    public void setRestaurant_photo_firebase_URL(String restaurant_photo_firebase_URL) {
        this.restaurant_photo_firebase_URL = restaurant_photo_firebase_URL;
    }

    public String getRestaurant_category() {
        return restaurant_category;
    }

    public void setRestaurant_category(String restaurant_category) {
        this.restaurant_category = restaurant_category;
    }

    public void setFidelityProgramAccepted(Boolean fidelityProgramAccepted) {
        this.fidelityProgramAccepted = fidelityProgramAccepted;
    }

    public void setTableReservationAllowed(Boolean tableReservationAllowed) {
        this.tableReservationAllowed = tableReservationAllowed;
    }

    public int getRestaurant_total_tables_number() {
        return restaurant_total_tables_number;
    }

    public void setRestaurant_total_tables_number(int restaurant_total_tables_number) {
        this.restaurant_total_tables_number = restaurant_total_tables_number;
    }

    public void setTakeAwayAllowed(Boolean takeAwayAllowed) {
        this.takeAwayAllowed = takeAwayAllowed;
    }

    public int getRestaurant_orders_per_hour() {
        return restaurant_orders_per_hour;
    }

    public void setRestaurant_orders_per_hour(int restaurant_orders_per_hour) {
        this.restaurant_orders_per_hour = restaurant_orders_per_hour;
    }

    public int getRestaurant_squared_meters() {
        return restaurant_squared_meters;
    }

    public void setRestaurant_squared_meters(int restaurant_squared_meters) {
        this.restaurant_squared_meters = restaurant_squared_meters;
    }

    public String getRestaurant_closest_metro() {
        return restaurant_closest_metro;
    }

    public void setRestaurant_closest_metro(String restaurant_closest_metro) {
        this.restaurant_closest_metro = restaurant_closest_metro;
    }

    public String getRestaurant_closest_bus() {
        return restaurant_closest_bus;
    }

    public void setRestaurant_closest_bus(String restaurant_closest_bus) {
        this.restaurant_closest_bus = restaurant_closest_bus;
    }

    public int getRestaurant_price_range() {
        return restaurant_price_range;
    }

    public void setRestaurant_price_range(int restaurant_price_range) {
        this.restaurant_price_range = restaurant_price_range;
    }


    public void setAnimalAllowed(Boolean animalAllowed) {
        this.animalAllowed = animalAllowed;
    }

    public void setCeliacFriendly(Boolean celiacFriendly) {
        this.celiacFriendly = celiacFriendly;
    }

    public void setTvPresent(Boolean tvPresent) {
        this.tvPresent = tvPresent;
    }


    public void setWifiPresent(Boolean wifiPresent) {
        this.wifiPresent = wifiPresent;
    }

    public void setCreditCardAccepted(Boolean creditCardAccepted) {
        this.creditCardAccepted = creditCardAccepted;
    }

    public void setAirConditionedPresent(Boolean airConditionedPresent) {
        this.airConditionedPresent = airConditionedPresent;
    }

    public double getRestaurant_latitude_position() {
        return restaurant_latitude_position;
    }

    public void setRestaurant_latitude_position(double restaurant_latitude_position) {
        this.restaurant_latitude_position = restaurant_latitude_position;
    }

    public double getRestaurant_longitude_position() {
        return restaurant_longitude_position;
    }

    public void setRestaurant_longitude_position(double restaurant_longitude_position) {
        this.restaurant_longitude_position = restaurant_longitude_position;
    }
}
