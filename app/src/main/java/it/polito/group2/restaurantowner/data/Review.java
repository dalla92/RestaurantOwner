package it.polito.group2.restaurantowner.data;

import android.graphics.Bitmap;

import java.util.Calendar;
import java.util.Comparator;

public class Review implements Comparable<Review>{
    private String restaurantId, username, comment, reviewID;
    private Calendar date;
    private float stars_number;
    private Bitmap picture;

    public Review() {
    }

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

    @Override
    public int compareTo(Review another) {
        if(this.date.before(another.getDate()))
            return 1;
        else if(this.date.after(another.getDate()))
            return -1;
        else
            return 0;
    }

    public static Comparator<Review> ReviewRatingComparator = new Comparator<Review>() {

        public int compare(Review review1, Review review2) {

            //ascending order
            if(review1.getStars_number() > review2.getStars_number())
                return 1;
            else if(review1.getStars_number() < review2.getStars_number())
                return -1;
            else
                return 0;
        }

    };
}
