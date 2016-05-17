package it.polito.group2.restaurantowner.firebasedata;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Alessio on 16/05/2016.
 */
public class Meal implements Serializable {

    private String meal_id;
    private String restaurant_id;
    private String meal_name;
    private double meal_price = 0.0;
    private int meal_cooking_time;
    private String meal_description;
    private String meal_category;
    private boolean is_meal_vegan;
    private boolean is_meal_vegetarian;
    private boolean is_meal_celiac;
    private boolean is_meal_availabile;
    private ArrayList<MealAddition> meal_additions;
    private ArrayList<MealCategory> meal_tags;
    private boolean is_meal_take_away;
    private String offer_meal_thumbnail; //for meal preview with Glide in AsyncTask
    private String offer_meal_photo_firebase_URL; //for enlarging image with Glide in AsyncTask

    public Meal(){

    }

    public String getMeal_id() {
        return meal_id;
    }

    public void setMeal_id(String meal_id) {
        this.meal_id = meal_id;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getMeal_name() {
        return meal_name;
    }

    public void setMeal_name(String meal_name) {
        this.meal_name = meal_name;
    }

    public double getMeal_price() {
        return meal_price;
    }

    public void setMeal_price(double meal_price) {
        this.meal_price = meal_price;
    }

    public int getMeal_cooking_time() {
        return meal_cooking_time;
    }

    public void setMeal_cooking_time(int meal_cooking_time) {
        this.meal_cooking_time = meal_cooking_time;
    }

    public String getMeal_description() {
        return meal_description;
    }

    public void setMeal_description(String meal_description) {
        this.meal_description = meal_description;
    }

    public String getMeal_category() {
        return meal_category;
    }

    public void setMeal_category(String meal_category) {
        this.meal_category = meal_category;
    }

    public boolean is_meal_vegan() {
        return is_meal_vegan;
    }

    public void setIs_meal_vegan(boolean is_meal_vegan) {
        this.is_meal_vegan = is_meal_vegan;
    }

    public boolean is_meal_vegetarian() {
        return is_meal_vegetarian;
    }

    public void setIs_meal_vegetarian(boolean is_meal_vegetarian) {
        this.is_meal_vegetarian = is_meal_vegetarian;
    }

    public boolean is_meal_celiac() {
        return is_meal_celiac;
    }

    public void setIs_meal_celiac(boolean is_meal_celiac) {
        this.is_meal_celiac = is_meal_celiac;
    }

    public boolean is_meal_availabile() {
        return is_meal_availabile;
    }

    public void setIs_meal_availabile(boolean is_meal_availabile) {
        this.is_meal_availabile = is_meal_availabile;
    }

    public ArrayList<MealAddition> getMeal_additions() {
        return meal_additions;
    }

    public void setMeal_additions(ArrayList<MealAddition> meal_additions) {
        this.meal_additions = meal_additions;
    }

    public ArrayList<MealCategory> getMeal_tags() {
        return meal_tags;
    }

    public void setMeal_tags(ArrayList<MealCategory> meal_tags) {
        this.meal_tags = meal_tags;
    }

    public boolean is_meal_take_away() {
        return is_meal_take_away;
    }

    public void setIs_meal_take_away(boolean is_meal_take_away) {
        this.is_meal_take_away = is_meal_take_away;
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
