package it.polito.group2.restaurantowner.firebasedata;

/**
 * Created by Alessio on 16/05/2016.
 */
public class RestaurantPreview {

    private String restaurant_id; //to pass to the new Activity to open the right restaurant

    private String restaurant_cover_firebase_URL; //with Glide in AsyncTask
    private String restaurant_name;
    private float rating; //android:stepSize="0.01"
    private int reservations_number;
    private int tables_number;

    public RestaurantPreview(){

    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getRestaurant_cover_firebase_URL() {
        return restaurant_cover_firebase_URL;
    }

    public void setRestaurant_cover_firebase_URL(String restaurant_cover_firebase_URL) {
        this.restaurant_cover_firebase_URL = restaurant_cover_firebase_URL;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String restaurant_name) {
        this.restaurant_name = restaurant_name;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getReservations_number() {
        return reservations_number;
    }

    public void setReservations_number(int reservations_number) {
        this.reservations_number = reservations_number;
    }

    public int getTables_number() {
        return tables_number;
    }

    public void setTables_number(int tables_number) {
        this.tables_number = tables_number;
    }
}
