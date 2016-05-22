package it.polito.group2.restaurantowner.firebasedata;

import java.util.GregorianCalendar;

/**
 * Created by Alessio on 16/05/2016.
 */

public class Offer {

    private String offer_id;
    private String restaurant_id;
    private String offer_name;
    private String offer_description;
    private GregorianCalendar offer_start_date;
    private GregorianCalendar offer_end_date;
    private Boolean offerAtLunch;
    private Boolean offerAtDinner;
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

    public GregorianCalendar getOffer_start_date() {
        return offer_start_date;
    }

    public void setOffer_start_date(GregorianCalendar offer_start_date) {
        this.offer_start_date = offer_start_date;
    }

    public GregorianCalendar getOffer_end_date() {
        return offer_end_date;
    }

    public void setOffer_end_date(GregorianCalendar offer_end_date) {
        this.offer_end_date = offer_end_date;
    }

    public Boolean getOfferAtLunch() {
        return offerAtLunch;
    }

    public void setOfferAtLunch(Boolean offerAtLunch) {
        this.offerAtLunch = offerAtLunch;
    }

    public Boolean getOfferAtDinner() {
        return offerAtDinner;
    }

    public void setOfferAtDinner(Boolean offerAtDinner) {
        this.offerAtDinner = offerAtDinner;
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