package it.polito.group2.restaurantowner.firebasedata;

import java.util.HashMap;

/**
 * Created by Alessio on 16/05/2016.
 */
public class RestaurantGallery {

    private String restaurant_id;
    private HashMap<String, String> urls;

    public RestaurantGallery(){

    }

    public HashMap<String, String> getUrls() {
        if(urls == null)
            return new HashMap<>();
        return urls;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

}
