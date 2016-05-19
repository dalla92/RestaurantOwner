package it.polito.group2.restaurantowner.firebasedata;


import java.io.Serializable;

/**
 * Created by Alessio on 16/05/2016.
 */
public class MealAddition implements Serializable {

    private String meal_addition_id;
    private String meal_addition_name;
    private double meal_addition_price = 0.0;
    private boolean is_addition_selected = false;

    public MealAddition(){

    }

    public String getMeal_addition_id() {
        return meal_addition_id;
    }

    public void setMeal_addition_id(String meal_addition_id) {
        this.meal_addition_id = meal_addition_id;
    }

    public String getMeal_addition_name() {
        return meal_addition_name;
    }

    public void setMeal_addition_name(String meal_addition_name) {
        this.meal_addition_name = meal_addition_name;
    }

    public double getMeal_addition_price() {
        return meal_addition_price;
    }

    public void setMeal_addition_price(double meal_addition_price) {
        this.meal_addition_price = meal_addition_price;
    }

    public boolean is_addition_selected() {
        return is_addition_selected;
    }

    public void setIs_addition_selected(boolean is_addition_selected) {
        this.is_addition_selected = is_addition_selected;
    }
}