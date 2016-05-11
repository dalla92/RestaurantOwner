package it.polito.group2.restaurantowner.user.order;

import it.polito.group2.restaurantowner.data.Meal;

/**
 * Created by Filippo on 11/05/2016.
 */
public class MealModel {

    private String id;
    private String name;
    private Meal meal;

    public MealModel(String id, String name, Meal meal) {
        this.id = id;
        this.name = name;
        this.meal = meal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Meal getMeal() {
        return meal;
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
    }
}
