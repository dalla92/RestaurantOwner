package it.polito.group2.restaurantowner.user.my_orders;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.data.Order;
import it.polito.group2.restaurantowner.user.order.MealModel;

/**
 * Created by Filippo on 12/05/2016.
 */
public class OrderModel {
    private String id;
    private Order order;
    private ArrayList<MealModel> mealList = new ArrayList<MealModel>();

    public OrderModel(String id, Order order, ArrayList<MealModel> mealList) {
        this.id = id;
        this.order = order;
        this.mealList = mealList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public ArrayList<MealModel> getMealList() {
        return mealList;
    }

    public void setMealList(ArrayList<MealModel> mealList) {
        this.mealList = mealList;
    }
}
