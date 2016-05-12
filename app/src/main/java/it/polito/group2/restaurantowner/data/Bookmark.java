package it.polito.group2.restaurantowner.data;

/**
 * Created by Alessio on 12/05/2016.
 */
public class Bookmark {

    private String bookmark_id;
    private String user_id;
    private String restaurant_id;

    public Bookmark(){

    }

    public String getBookmark_id() {
        return bookmark_id;
    }

    public void setBookmark_id(String bookmark_id) {
        this.bookmark_id = bookmark_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }
}
