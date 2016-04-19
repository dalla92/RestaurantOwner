package it.polito.group2.restaurantowner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ArrayList;

/**
 * Created by Alessio on 12/04/2016.
 */
public class Meal implements Serializable{
    private String meal_photo;
    private String meal_name;
    private double meal_price;
    private String type1;
    private String type2;
    private boolean available;
    private ArrayList<Addition> meal_additions;
    private ArrayList<Addition> meal_categories;
    private boolean take_away;
    private String restaurantId;
    private String mealId;
    private int cooking_time;
    private String description;
    private String category;

    public Meal(){
        meal_additions = new ArrayList<Addition>();
        meal_categories = new ArrayList<Addition>();
    }

    public String getMeal_photo() {
        return meal_photo;
    }

    public void setMeal_photo(String meal_photo) {
        this.meal_photo = meal_photo;
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

    public String getType1() {
        return type1;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public ArrayList<Addition> getMeal_additions() {
        return meal_additions;
    }

    public void setMeal_additions(ArrayList<Addition> meal_additions) {
        this.meal_additions = meal_additions;
    }

    public ArrayList<Addition> getMeal_categories() {
        return meal_categories;
    }

    public void setMeal_categories(ArrayList<Addition> meal_categories) {
        this.meal_categories = meal_categories;
    }

    public boolean isTake_away() {
        return take_away;
    }

    public void setTake_away(boolean take_away) {
        this.take_away = take_away;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getMealId() {
        return mealId;
    }

    public void setMealId(String mealId) {
        this.mealId = mealId;
    }

    public int getCooking_time() {
        return cooking_time;
    }

    public void setCooking_time(int cooking_time) {
        this.cooking_time = cooking_time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
