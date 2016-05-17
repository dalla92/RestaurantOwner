package it.polito.group2.restaurantowner.firebasedata;

import android.graphics.Bitmap;

import java.util.Calendar;
import java.util.Comparator;

/**
 * Created by Alessio on 16/05/2016.
 */
public class Review implements Comparable<Review>{

    private String review_id;
    private String user_id;
    private String user_full_name;
    private String user_thumbnail; //with Glide in AsyncTask
    private String restaurant_id;
    private String review_comment;
    private Calendar review_date;
    private float review_rating; //android:stepSize="0.01"

    public Review(){

    }

    @Override
    public int compareTo(Review another) {
        if(this.review_date.before(another.getReview_date()))
            return 1;
        else if(this.review_date.after(another.getReview_date()))
            return -1;
        else
            return 0;
    }

    public static Comparator<Review> ReviewRatingComparator = new Comparator<Review>() {

        public int compare(Review review1, Review review2) {

            //ascending order
            if(review1.getReview_rating() > review2.getReview_rating())
                return 1;
            else if(review1.getReview_rating() < review2.getReview_rating())
                return -1;
            else
                return 0;
        }

    };

    public String getReview_id() {
        return review_id;
    }

    public void setReview_id(String review_id) {
        this.review_id = review_id;
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

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getReview_comment() {
        return review_comment;
    }

    public void setReview_comment(String review_comment) {
        this.review_comment = review_comment;
    }

    public Calendar getReview_date() {
        return review_date;
    }

    public void setReview_date(Calendar review_date) {
        this.review_date = review_date;
    }

    public float getReview_rating() {
        return review_rating;
    }

    public void setReview_rating(float review_rating) {
        this.review_rating = review_rating;
    }
}
