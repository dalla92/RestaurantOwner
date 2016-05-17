package it.polito.group2.restaurantowner.firebasedata;

import android.graphics.Bitmap;

import java.util.Calendar;

/**
 * Created by Alessio on 16/05/2016.
 */

public class Offer {

    private String offer_id;
    private String restaurant_id;
    private String offer_name;
    private String offer_description;
    private Calendar offer_start_date;
    private Calendar offer_end_date;
    private boolean is_offer_at_lunch;
    private boolean is_offer_at_dinner;
    private String offer_meal_id;
    private String offer_meal_name;
    private String offer_meal_thumbnail; //for meal preview with Glide in AsyncTask
    private String offer_meal_photo_firebase_URL; //for enlarging image with Glide in AsyncTask

    public Offer() {

    }

    public String getOffer_id() {
        return offer_id;
    }

    public void setOffer_id(String offer_id) {
        this.offer_id = offer_id;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getOffer_name() {
        return offer_name;
    }

    public void setOffer_name(String offer_name) {
        this.offer_name = offer_name;
    }

    public String getOffer_description() {
        return offer_description;
    }

    public void setOffer_description(String offer_description) {
        this.offer_description = offer_description;
    }

    public Calendar getOffer_start_date() {
        return offer_start_date;
    }

    public void setOffer_start_date(Calendar offer_start_date) {
        this.offer_start_date = offer_start_date;
    }

    public Calendar getOffer_end_date() {
        return offer_end_date;
    }

    public void setOffer_end_date(Calendar offer_end_date) {
        this.offer_end_date = offer_end_date;
    }

    public boolean is_offer_at_lunch() {
        return is_offer_at_lunch;
    }

    public void setIs_offer_at_lunch(boolean is_offer_at_lunch) {
        this.is_offer_at_lunch = is_offer_at_lunch;
    }

    public boolean is_offer_at_dinner() {
        return is_offer_at_dinner;
    }

    public void setIs_offer_at_dinner(boolean is_offer_at_dinner) {
        this.is_offer_at_dinner = is_offer_at_dinner;
    }

    public String getOffer_meal_id() {
        return offer_meal_id;
    }

    public void setOffer_meal_id(String offer_meal_id) {
        this.offer_meal_id = offer_meal_id;
    }

    public String getOffer_meal_name() {
        return offer_meal_name;
    }

    public void setOffer_meal_name(String offer_meal_name) {
        this.offer_meal_name = offer_meal_name;
    }

    public String getOffer_meal_thumbnail() {
        return offer_meal_thumbnail;
    }

    public void setOffer_meal_thumbnail(String offer_meal_thumbnail) {
        this.offer_meal_thumbnail = offer_meal_thumbnail;
    }

    public String getOffer_meal_photo_firebase_URL() {
        return offer_meal_photo_firebase_URL;
    }

    public void setOffer_meal_photo_firebase_URL(String offer_meal_photo_firebase_URL) {
        this.offer_meal_photo_firebase_URL = offer_meal_photo_firebase_URL;
    }
}