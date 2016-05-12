package it.polito.group2.restaurantowner.data;

import java.io.Serializable;

/**
 * Created by TheChuck on 07/05/2016.
 */
public class MenuCategory implements Serializable {
    private String categoryID, restaurantID, name;

    public MenuCategory(String categoryID, String name, String restaurantID) {
        this.categoryID = categoryID;
        this.name = name;
        this.restaurantID = restaurantID;
    }

    public MenuCategory() {
    }

    public String getCategoryID() {
        return categoryID;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public String getName() {
        return name;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
    }

    public void setName(String name) {
        this.name = name;
    }
}
