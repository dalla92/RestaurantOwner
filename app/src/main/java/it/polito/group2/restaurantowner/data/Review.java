package it.polito.group2.restaurantowner.data;

import android.graphics.Bitmap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;

public class Review implements Comparable<Review>{
    private String restaurantId, userID, comment, reviewID, userphoto;
    private Calendar date;
    private float stars_number;

    public Review() {
    }

    public Review(String restaurantId, String userID, String date, String comment, String reviewID, String picture, float stars_number) {
        this.restaurantId = restaurantId;
        this.userID = userID;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("EEE dd MMM yyyy 'at' HH:mm");
        try {
            c.setTime(format.parse(date));
        }
        catch(ParseException e){
            e.printStackTrace();
        }
        this.date = c;
        this.comment = comment;
        this.reviewID = reviewID;
        this.stars_number = stars_number;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public String getUserID() {
        return userID;
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

    public float getStars_number() {
        return stars_number;
    }

    public String getUserphoto() {
        return userphoto;
    }

    public void setUserphoto(String userphoto) {
        this.userphoto = userphoto;
    }

    public void setStars_number(float stars_number) {
        this.stars_number = stars_number;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setReviewID(String reviewID) {
        this.reviewID = reviewID;
    }

    public void setDate(Calendar date) {
        this.date = date;
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
