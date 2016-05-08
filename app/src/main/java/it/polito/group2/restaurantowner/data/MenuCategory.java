package it.polito.group2.restaurantowner.data;

/**
 * Created by TheChuck on 07/05/2016.
 */
public class MenuCategory {
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
}
