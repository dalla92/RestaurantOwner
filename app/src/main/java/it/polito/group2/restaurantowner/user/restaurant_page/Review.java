package it.polito.group2.restaurantowner.user.restaurant_page;

import android.graphics.Bitmap;

import java.util.Calendar;

public class Review {
    private String restaurantId, username, comment, reviewID;
    private Calendar date;
    private float stars_number;
    private Bitmap picture;

    public Review(String restaurantId, String username, Calendar date, String comment, String reviewID, Bitmap picture, float stars_number) {
        this.restaurantId = restaurantId;
        this.username = username;
        this.date = date;
        this.comment = comment;
        this.reviewID = reviewID;
        this.picture = picture;
        this.stars_number = stars_number;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public String getUsername() {
        return username;
    }

    public Calendar getDate() {
        return date;
    }

    public String getComment() {
        return comment;
    }

    public String getReviewID() {
        return reviewID;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public float getStars_number() {
        return stars_number;
    }
}
