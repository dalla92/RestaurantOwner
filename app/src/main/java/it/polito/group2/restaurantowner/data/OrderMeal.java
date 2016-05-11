package it.polito.group2.restaurantowner.data;

import java.util.ArrayList;

public class OrderMeal {

    private String orderMealID;
    private String orderID;
    private Meal meal;
    private Integer quantity;
    private String note;
    private MenuCategory category;
    private ArrayList<OrderMealAddition> additionList = new ArrayList<OrderMealAddition>();

    public OrderMeal() {
    }

    public OrderMeal(String orderMealID, String orderID, Meal meal, Integer quantity, String note,
                     MenuCategory category, ArrayList<OrderMealAddition> additionList) {
        this.orderMealID = orderMealID;
        this.orderID = orderID;
        this.meal = meal;
        this.quantity = quantity;
        this.note = note;
        this.category = category;
        this.additionList = additionList;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public MenuCategory getCategory() {
        return category;
    }

    public void setCategory(MenuCategory category) {
        this.category = category;
    }

    public ArrayList<OrderMealAddition> getAdditionList() {
        return additionList;
    }

    public void setAdditionList(ArrayList<OrderMealAddition> additionList) {
        this.additionList = additionList;
    }

}
