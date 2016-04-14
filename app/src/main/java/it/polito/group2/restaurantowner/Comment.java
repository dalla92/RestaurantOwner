package it.polito.group2.restaurantowner;

import java.sql.Blob;
import java.util.Date;
/**
 * Created by Alessio on 09/04/2016.
 */
public class Comment {
    int restaurantId;
    String username;
    String date;
    double stars_number;
    String comment;
    String userphoto;

    public Comment(int restaurantId, String username, String date, double stars_number, String userphoto, String comment) {
        this.restaurantId = restaurantId;
        this.username = username;
        this.date = date;
        this.userphoto = userphoto;
        this.stars_number=stars_number;
        this.comment=comment;
    }

    public Comment(){

    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getStars_number() {
        return stars_number;
    }

    public void setStars_number(double stars_number) {
        this.stars_number = stars_number;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String userphoto() {
        return userphoto;
    }

    public void setPhotoId(String photoId) {
        this.userphoto = userphoto;
    }
}
