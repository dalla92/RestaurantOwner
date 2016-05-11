package it.polito.group2.restaurantowner.data;

import android.graphics.Bitmap;

import java.sql.Blob;

/**
 * Created by Filippo on 12/04/2016.
 */
public class User {
    private String id;
    private String username;
    private String password; //mandatory field
    private String first_name; //mandatory field
    private String last_name; //mandatory field
    private String email; //mandatory field
    private String phone_number;
    private String vat_number;
    private Bitmap photo;
    private Boolean isOwner = false;
    private int fidelity_points = 0;

    public User(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getFidelity_points() {
        return fidelity_points;
    }

    public void setFidelity_points(int fidelity_points) {
        this.fidelity_points = fidelity_points;
    }

    public void setOwner(Boolean owner) {
        isOwner = owner;
    }

    public Boolean getOwner() {
        return isOwner;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getVat_number() {
        return vat_number;
    }

    public void setVat_number(String vat_number) {
        this.vat_number = vat_number;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public Boolean getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(Boolean isOwner) {
        this.isOwner = isOwner;
    }
}
