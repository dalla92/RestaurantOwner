package it.polito.group2.restaurantowner;


public class OrderedMeal {
    private String meal_name;
    private int quantity;

    public OrderedMeal(String meal_name, int quantity) {
        this.meal_name = meal_name;
        this.quantity = quantity;
    }

    public String getMeal_name() {
        return meal_name;
    }

    public int getQuantity() {
        return quantity;
    }
}
