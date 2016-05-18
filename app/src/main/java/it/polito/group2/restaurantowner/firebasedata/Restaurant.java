package it.polito.group2.restaurantowner.firebasedata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Alessio on 16/05/2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Restaurant implements Serializable {

    private String restaurant_id;
    private String user_id; //a certain owner manages this restaurant
    private String restaurant_name;
    private String restaurant_address;
    private String restaurant_telephone_number;
    private float restaurant_rating; //android:stepSize="0.01"
    private ArrayList<RestaurantTimeSlot> restaurant_time_slot;
    private String restaurant_photo_firebase_URL; //with Glide in AsyncTask
    private String restaurant_category;
    private boolean is_fidelity_program_accepted;
    private boolean is_table_reservation_allowed;
    private int restaurant_total_tables_number;
    private boolean is_take_away_allowed;
    private int restaurant_orders_per_hour;
    private int restaurant_squared_meters;
    private String restaurant_closest_metro;
    private String restaurant_closest_bus;
    private int restaurant_price_range;
    private boolean is_animal_allowed;
    private boolean is_celiac_friendly;
    private boolean is_tv_present;
    private boolean is_wifi_present;
    private boolean is_credit_card_accepted;
    private boolean is_air_conditioned_present;
    private double restaurant_latitude_position;
    private double restaurant_longitude_position;

    public Restaurant(){

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

    public boolean is_fidelity_program_accepted() {
        return is_fidelity_program_accepted;
    }

    public void setIs_fidelity_program_accepted(boolean is_fidelity_program_accepted) {
        this.is_fidelity_program_accepted = is_fidelity_program_accepted;
    }

    public boolean is_table_reservation_allowed() {
        return is_table_reservation_allowed;
    }

    public void setIs_table_reservation_allowed(boolean is_table_reservation_allowed) {
        this.is_table_reservation_allowed = is_table_reservation_allowed;
    }

    public int getRestaurant_total_tables_number() {
        return restaurant_total_tables_number;
    }

    public void setRestaurant_total_tables_number(int restaurant_total_tables_number) {
        this.restaurant_total_tables_number = restaurant_total_tables_number;
    }

    public boolean is_take_away_allowed() {
        return is_take_away_allowed;
    }

    public void setIs_take_away_allowed(boolean is_take_away_allowed) {
        this.is_take_away_allowed = is_take_away_allowed;
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

    public boolean is_animal_allowed() {
        return is_animal_allowed;
    }

    public void setIs_animal_allowed(boolean is_animal_allowed) {
        this.is_animal_allowed = is_animal_allowed;
    }

    public boolean is_celiac_friendly() {
        return is_celiac_friendly;
    }

    public void setIs_celiac_friendly(boolean is_celiac_friendly) {
        this.is_celiac_friendly = is_celiac_friendly;
    }

    public boolean is_tv_present() {
        return is_tv_present;
    }

    public void setIs_tv_present(boolean is_tv_present) {
        this.is_tv_present = is_tv_present;
    }

    public boolean is_wifi_present() {
        return is_wifi_present;
    }

    public void setIs_wifi_present(boolean is_wifi_present) {
        this.is_wifi_present = is_wifi_present;
    }

    public boolean is_credit_card_accepted() {
        return is_credit_card_accepted;
    }

    public void setIs_credit_card_accepted(boolean is_credit_card_accepted) {
        this.is_credit_card_accepted = is_credit_card_accepted;
    }

    public boolean is_air_conditioned_present() {
        return is_air_conditioned_present;
    }

    public void setIs_air_conditioned_present(boolean is_air_conditioned_present) {
        this.is_air_conditioned_present = is_air_conditioned_present;
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
