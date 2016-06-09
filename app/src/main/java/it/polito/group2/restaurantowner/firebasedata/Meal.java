package it.polito.group2.restaurantowner.firebasedata;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Alessio on 16/05/2016.
 */

public class Meal implements Serializable, Parcelable {

    private String meal_id;
    private String restaurant_id;
    private String meal_name;
    private double meal_price = 0.0;
    private int meal_cooking_time;
    private String meal_description;
    private String meal_category;
    private Boolean mealVegan;
    private Boolean mealVegetarian;
    private Boolean mealGlutenFree;
    private Boolean mealAvailable;
    private ArrayList<MealAddition> meal_additions;
    private ArrayList<MealCategory> meal_tags;
    private Boolean mealTakeAway;
    private String meal_thumbnail; //for meal preview with Glide in AsyncTask
    private String meal_photo_firebase_URL; //for enlarging image with Glide in AsyncTask
    private Integer meal_quantity; //for takeaway order quantity

    public Meal(){

    }

    public String getMeal_id() {
        return meal_id;
    }

    public void setMeal_id(String meal_id) {
        this.meal_id = meal_id;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getMeal_name() {
        return meal_name;
    }

    public void setMeal_name(String meal_name) {
        this.meal_name = meal_name;
    }

    public double getMeal_price() {
        return meal_price;
    }

    public void setMeal_price(double meal_price) {
        this.meal_price = meal_price;
    }

    public int getMeal_cooking_time() {
        return meal_cooking_time;
    }

    public void setMeal_cooking_time(int meal_cooking_time) {
        this.meal_cooking_time = meal_cooking_time;
    }

    public String getMeal_description() {
        return meal_description;
    }

    public void setMeal_description(String meal_description) {
        this.meal_description = meal_description;
    }

    public String getMeal_category() {
        return meal_category;
    }

    public void setMeal_category(String meal_category) {
        this.meal_category = meal_category;
    }

    public Boolean getMealVegan() {
        return mealVegan;
    }

    public void setMealVegan(Boolean mealVegan) {
        this.mealVegan = mealVegan;
    }

    public Boolean getMealVegetarian() {
        return mealVegetarian;
    }

    public void setMealVegetarian(Boolean mealVegetarian) {
        this.mealVegetarian = mealVegetarian;
    }

    public Boolean getMealGlutenFree() {
        return mealGlutenFree;
    }

    public void setMealGlutenFree(Boolean mealGlutenFree) {
        this.mealGlutenFree = mealGlutenFree;
    }

    public Boolean getMealAvailable() {
        return mealAvailable;
    }

    public void setMealAvailable(Boolean mealAvailable) {
        this.mealAvailable = mealAvailable;
    }

    public ArrayList<MealAddition> getMeal_additions() {
        return meal_additions;
    }

    public void setMeal_additions(ArrayList<MealAddition> meal_additions) {
        this.meal_additions = meal_additions;
    }

    public ArrayList<MealCategory> getMeal_tags() {
        return meal_tags;
    }

    public void setMeal_tags(ArrayList<MealCategory> meal_tags) {
        this.meal_tags = meal_tags;
    }

    public Boolean getMealTakeAway() {
        return mealTakeAway;
    }

    public void setMealTakeAway(Boolean mealTakeAway) {
        this.mealTakeAway = mealTakeAway;
    }

    public String getMeal_thumbnail() {
        return meal_thumbnail;
    }

    public void setMeal_thumbnail(String meal_thumbnail) {
        this.meal_thumbnail = meal_thumbnail;
    }

    public String getMeal_photo_firebase_URL() {
        return meal_photo_firebase_URL;
    }

    public void setMeal_photo_firebase_URL(String meal_photo_firebase_URL) {
        this.meal_photo_firebase_URL = meal_photo_firebase_URL;
    }

    public Integer getMeal_quantity() {
        return meal_quantity;
    }

    public void setMeal_quantity(Integer meal_quantity) {
        this.meal_quantity = meal_quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Meal meal = (Meal) o;

        if (Double.compare(meal.meal_price, meal_price) != 0) return false;
        if (meal_cooking_time != meal.meal_cooking_time) return false;
        if (meal_id != null ? !meal_id.equals(meal.meal_id) : meal.meal_id != null) return false;
        if (restaurant_id != null ? !restaurant_id.equals(meal.restaurant_id) : meal.restaurant_id != null)
            return false;
        if (meal_name != null ? !meal_name.equals(meal.meal_name) : meal.meal_name != null)
            return false;
        if (meal_description != null ? !meal_description.equals(meal.meal_description) : meal.meal_description != null)
            return false;
        if (meal_category != null ? !meal_category.equals(meal.meal_category) : meal.meal_category != null)
            return false;
        if (mealVegan != null ? !mealVegan.equals(meal.mealVegan) : meal.mealVegan != null)
            return false;
        if (mealVegetarian != null ? !mealVegetarian.equals(meal.mealVegetarian) : meal.mealVegetarian != null)
            return false;
        if (mealGlutenFree != null ? !mealGlutenFree.equals(meal.mealGlutenFree) : meal.mealGlutenFree != null)
            return false;
        if (mealAvailable != null ? !mealAvailable.equals(meal.mealAvailable) : meal.mealAvailable != null)
            return false;
        if (meal_additions != null ? !meal_additions.equals(meal.meal_additions) : meal.meal_additions != null)
            return false;
        if (meal_tags != null ? !meal_tags.equals(meal.meal_tags) : meal.meal_tags != null)
            return false;
        if (mealTakeAway != null ? !mealTakeAway.equals(meal.mealTakeAway) : meal.mealTakeAway != null)
            return false;
        if (meal_thumbnail != null ? !meal_thumbnail.equals(meal.meal_thumbnail) : meal.meal_thumbnail != null)
            return false;
        if (meal_photo_firebase_URL != null ? !meal_photo_firebase_URL.equals(meal.meal_photo_firebase_URL) : meal.meal_photo_firebase_URL != null)
            return false;
        return !(meal_quantity != null ? !meal_quantity.equals(meal.meal_quantity) : meal.meal_quantity != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = meal_id != null ? meal_id.hashCode() : 0;
        result = 31 * result + (restaurant_id != null ? restaurant_id.hashCode() : 0);
        result = 31 * result + (meal_name != null ? meal_name.hashCode() : 0);
        temp = Double.doubleToLongBits(meal_price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + meal_cooking_time;
        result = 31 * result + (meal_description != null ? meal_description.hashCode() : 0);
        result = 31 * result + (meal_category != null ? meal_category.hashCode() : 0);
        result = 31 * result + (mealVegan != null ? mealVegan.hashCode() : 0);
        result = 31 * result + (mealVegetarian != null ? mealVegetarian.hashCode() : 0);
        result = 31 * result + (mealGlutenFree != null ? mealGlutenFree.hashCode() : 0);
        result = 31 * result + (mealAvailable != null ? mealAvailable.hashCode() : 0);
        result = 31 * result + (meal_additions != null ? meal_additions.hashCode() : 0);
        result = 31 * result + (meal_tags != null ? meal_tags.hashCode() : 0);
        result = 31 * result + (mealTakeAway != null ? mealTakeAway.hashCode() : 0);
        result = 31 * result + (meal_thumbnail != null ? meal_thumbnail.hashCode() : 0);
        result = 31 * result + (meal_photo_firebase_URL != null ? meal_photo_firebase_URL.hashCode() : 0);
        result = 31 * result + (meal_quantity != null ? meal_quantity.hashCode() : 0);
        return result;
    }

    //Parcelable part
    public Meal(Parcel parcel){
        this.meal_id = parcel.readString();
        this.restaurant_id = parcel.readString();
        this.meal_name = parcel.readString();
        this.meal_price = parcel.readDouble();
        this.meal_cooking_time = parcel.readInt();
        this.meal_description = parcel.readString();
        this.meal_category = parcel.readString();
        this.mealVegan = parcel.readInt() != 0;
        this.mealVegetarian = parcel.readInt() != 0;
        this.mealGlutenFree = parcel.readInt() != 0;
        this.mealAvailable = parcel.readInt() != 0;
        this.meal_additions = parcel.readArrayList(null);
        this.meal_tags = parcel.readArrayList(null);
        this.mealTakeAway = parcel.readInt() != 0;
        this.meal_thumbnail = parcel.readString();
        this.meal_photo_firebase_URL = parcel.readString();
        this.meal_quantity = parcel.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.meal_id);
        dest.writeString(this.restaurant_id);
        dest.writeString(this.meal_name);
        dest.writeDouble(this.meal_price);
        dest.writeInt(this.meal_cooking_time);
        dest.writeString(this.meal_description);
        dest.writeString(this.meal_category);
        dest.writeInt(this.mealVegan ? 1 : 0);
        dest.writeInt(this.mealVegetarian ? 1 : 0);
        dest.writeInt(this.mealGlutenFree? 1 : 0);
        dest.writeInt(this.mealAvailable? 1 : 0);
        dest.writeList(this.meal_additions);
        dest.writeList(this.meal_tags);
        dest.writeInt(this.mealTakeAway? 1 : 0);
        dest.writeString(this.meal_thumbnail);
        dest.writeString(this.meal_photo_firebase_URL);
        dest.writeInt(this.meal_quantity);
    }

    @Override
    public int describeContents(){
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Meal createFromParcel(Parcel in) {
            return new Meal(in);
        }

        public Meal[] newArray(int size) {
            return new Meal[size];
        }
    };

}
