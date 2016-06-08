package it.polito.group2.restaurantowner.firebasedata;

import org.jetbrains.annotations.NotNull;

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
    private Long review_timestamp;
    private float review_rating; //android:stepSize="0.01"

    public Review(){

    }

    @Override
    public int compareTo(@NotNull Review another) {
        if(this.review_timestamp < another.getReview_timestamp())
            return 1;
        else if(this.review_timestamp > another.getReview_timestamp())
            return -1;
        else
            return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Review review = (Review) o;

        if (review_id != null ? !review_id.equals(review.review_id) : review.review_id != null)
            return false;
        if (user_id != null ? !user_id.equals(review.user_id) : review.user_id != null)
            return false;
        return !(restaurant_id != null ? !restaurant_id.equals(review.restaurant_id) : review.restaurant_id != null);

    }

    @Override
    public int hashCode() {
        int result = review_id != null ? review_id.hashCode() : 0;
        result = 31 * result + (user_id != null ? user_id.hashCode() : 0);
        result = 31 * result + (restaurant_id != null ? restaurant_id.hashCode() : 0);
        return result;
    }

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

    public Long getReview_timestamp() {
        return review_timestamp;
    }

    public void setReview_timestamp(Long review_timestamp) {
        this.review_timestamp = review_timestamp;
    }

    public float getReview_rating() {
        return review_rating;
    }

    public void setReview_rating(float review_rating) {
        this.review_rating = review_rating;
    }
}
