package it.polito.group2.restaurantowner.owner;

/**
 * Created by Daniele on 07/04/2016.
 */
public class RestaurantService {
    private String restaurantId;
    private String name;
    private String attribute;

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
}
