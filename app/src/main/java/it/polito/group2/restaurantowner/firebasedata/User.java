package it.polito.group2.restaurantowner.firebasedata;

import android.graphics.Bitmap;

import java.util.HashMap;


/**
 * Created by Alessio on 16/05/2016.
 */

public class User {

    private String user_id;
    private String user_full_name;
    private String user_thumbnail; //for Reviews with Glide in AsyncTask
    private String user_photo_firebase_URL; //for Navigation Drawer with Glide in AsyncTask
    private String user_telephone_number;
    private String user_email;
    private HashMap<String, Boolean> providers;
    private int user_fidelity_points = 0;
    private HashMap<String, Boolean> favourites_restaurants;
    private Boolean ownerUser = false;
    private String owner_vat_number; //only if owner


    public User(){

    }

    public User(String user_id, String user_full_name, String user_telephone_number, String user_email) {
        this.user_id = user_id;
        this.user_full_name = user_full_name;
        this.user_telephone_number = user_telephone_number;
        this.user_email = user_email;
    }

    public HashMap<String, Boolean> getProviders() {
        if(providers == null)
            providers = new HashMap<>();
        return providers;
    }

    public HashMap<String, Boolean> getFavourites_restaurants() {
        if(favourites_restaurants == null)
            favourites_restaurants = new HashMap<>();
        return favourites_restaurants;
    }

    public void setFavourites_restaurants(HashMap<String, Boolean> favourites_restaurants) {
        this.favourites_restaurants = favourites_restaurants;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_full_name() {
        return user_full_name;
    }

    public void setUser_full_name(String user_full_name) {
        this.user_full_name = user_full_name;
    }

    public String getUser_thumbnail() {
        return user_thumbnail;
    }

    public void setUser_thumbnail(String user_thumbnail) {
        this.user_thumbnail = user_thumbnail;
    }

    public String getUser_photo_firebase_URL() {
        return user_photo_firebase_URL;
    }

    public void setUser_photo_firebase_URL(String user_photo_firebase_URL) {
        this.user_photo_firebase_URL = user_photo_firebase_URL;
    }

    public String getUser_telephone_number() {
        return user_telephone_number;
    }

    public void setUser_telephone_number(String user_telephone_number) {
        this.user_telephone_number = user_telephone_number;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public int getUser_fidelity_points() {
        return user_fidelity_points;
    }

    public void setUser_fidelity_points(int user_fidelity_points) {
        this.user_fidelity_points = user_fidelity_points;
    }

    public Boolean getOwnerUser() {
        return ownerUser;
    }

    public void setOwnerUser(Boolean ownerUser) {
        this.ownerUser = ownerUser;
    }

    public String getOwner_vat_number() {
        return owner_vat_number;
    }

    public void setOwner_vat_number(String owner_vat_number) {
        this.owner_vat_number = owner_vat_number;
    }
}
