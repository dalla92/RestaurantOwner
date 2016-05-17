package it.polito.group2.restaurantowner.firebasedata;

import android.graphics.Bitmap;

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
    private String user_password;
    private int user_fidelity_points = 0;

    private boolean isOwner = false;
    private String owner_vat_number; //only if owner


    public User(){

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

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public int getUser_fidelity_points() {
        return user_fidelity_points;
    }

    public void setUser_fidelity_points(int user_fidelity_points) {
        this.user_fidelity_points = user_fidelity_points;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setIsOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

    public String getOwner_vat_number() {
        return owner_vat_number;
    }

    public void setOwner_vat_number(String owner_vat_number) {
        this.owner_vat_number = owner_vat_number;
    }
}
