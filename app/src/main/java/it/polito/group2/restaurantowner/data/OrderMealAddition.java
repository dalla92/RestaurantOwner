package it.polito.group2.restaurantowner.data;

import java.io.Serializable;

/**
 * Created by Filippo on 11/05/2016.
 */
public class OrderMealAddition implements Serializable {

    private String orderMealAdditionID;
    private String orderMealID;
    private String orderID;
    private MealAddition addition;

    public OrderMealAddition() {
    }

    public OrderMealAddition(String orderMealID, String orderID, MealAddition addition) {
        this.orderMealID = orderMealID;
        this.orderID = orderID;
        this.addition = addition;
    }

    public OrderMealAddition(String orderMealAdditionID, MealAddition addition, String orderID, String orderMealID) {
        this.orderMealAdditionID = orderMealAdditionID;
        this.addition = addition;
        this.orderID = orderID;
        this.orderMealID = orderMealID;
    }

    public String getOrderMealAdditionID() {
        return orderMealAdditionID;
    }

    public void setOrderMealAdditionID(String orderMealAdditionID) {
        this.orderMealAdditionID = orderMealAdditionID;
    }

    public String getOrderMealID() {
        return orderMealID;
    }

    public void setOrderMealID(String orderMealID) {
        this.orderMealID = orderMealID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public MealAddition getAddition() {
        return addition;
    }

    public void setAddition(MealAddition addition) {
        this.addition = addition;
    }
}
