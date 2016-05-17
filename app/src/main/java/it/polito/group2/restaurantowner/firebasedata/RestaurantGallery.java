package it.polito.group2.restaurantowner.firebasedata;

/**
 * Created by Alessio on 16/05/2016.
 */
public class RestaurantGallery {

    private String restaurant_id;
    private String restaurant_gallery_image_URL; //with Glide in AsyncTask

    public RestaurantGallery(){

    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getRestaurant_gallery_image_URL() {
        return restaurant_gallery_image_URL;
    }

    public void setRestaurant_gallery_image_URL(String restaurant_gallery_image_URL) {
        this.restaurant_gallery_image_URL = restaurant_gallery_image_URL;
    }
}
