package it.polito.group2.restaurantowner;

/**
 * Created by Alessio on 08/04/2016.
 */
public class Addition {

    //String code = null;
    String restaurant_id;
    String meal_id;
    String name = null;
    boolean selected = false;
    double price = 0.0;

    public Addition(String restaurant_id, String meal_id, String name, double price, boolean selected) {
        super();
        //this.code = code;
        this.name = name;
        this.selected = selected;
        this.price = price;
    }

    public Addition(){

    }

    //public String getCode() {
        //return code;
    //}
    //public void setCode(String code) {
        //this.code = code;
    //}
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getMeal_id() {
        return meal_id;
    }

    public void setMeal_id(String meal_id) {
        this.meal_id = meal_id;
    }
}