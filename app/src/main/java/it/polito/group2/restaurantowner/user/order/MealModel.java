package it.polito.group2.restaurantowner.user.order;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.data.Meal;

/**
 * Created by Filippo on 11/05/2016.
 */
public class MealModel {

    private String id;
    private String name;
    private Meal meal;
    private Integer quantity;
    private ArrayList<AdditionModel> additionModel = new ArrayList<AdditionModel>();

    public MealModel(String id, String name, Meal meal, Integer quantity) {
        this.id = id;
        this.name = name;
        this.meal = meal;
        this.quantity = quantity;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public ArrayList<AdditionModel> getAdditionModel() {
        return additionModel;
    }

    public void setAdditionModel(ArrayList<AdditionModel> additionModel) {
        this.additionModel = additionModel;
    }
}
