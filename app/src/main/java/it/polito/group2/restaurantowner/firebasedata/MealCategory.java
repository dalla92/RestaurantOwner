package it.polito.group2.restaurantowner.firebasedata;

import java.io.Serializable;

/**
 * Created by Alessio on 16/05/2016.
 */
public class MealCategory implements Serializable {

    private String meal_category_id;
    private String meal_category_name;

    public MealCategory(){

    }

    public String getMeal_category_id() {
        return meal_category_id;
    }

    public void setMeal_category_id(String meal_category_id) {
        this.meal_category_id = meal_category_id;
    }

    public String getMeal_category_name() {
        return meal_category_name;
    }

    public void setMeal_category_name(String meal_category_name) {
        this.meal_category_name = meal_category_name;
    }
}
