package it.polito.group2.restaurantowner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alessio on 09/04/2016.
 */
public class Comment {
    String restaurantId;
    String username;
    String date;
    double stars_number;
    String comment;
    String userphoto;
    private String commentID; //Filippo: reference to this comment


    public Comment(String restaurantId, String username, String date, double stars_number, String userphoto, String comment) {
        this.restaurantId = restaurantId;
        this.username = username;
        this.date = date;
        this.userphoto = userphoto;
        this.stars_number=stars_number;
        this.comment=comment;
    }

    public Comment(){

    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
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

    /* Filippo start */
    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }
    /* Filippo stop */
}
