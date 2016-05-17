package it.polito.group2.restaurantowner.firebasedata;

/**
 * Created by Alessio on 16/05/2016.
 */
public class Favourite {

    private String favourite_id;
    private String user_id;
    private RestaurantPreview restaurant_preview; //to take name and img_URL

    public Favourite(){

    }

    public RestaurantPreview getRestaurant_preview() {
        return restaurant_preview;
    }

    public void setRestaurant_preview(RestaurantPreview restaurant_preview) {
        this.restaurant_preview = restaurant_preview;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFavourite_id() {
        return favourite_id;
    }

    public void setFavourite_id(String favourite_id) {
        this.favourite_id = favourite_id;
    }
}
