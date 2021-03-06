package it.polito.group2.restaurantowner.firebasedata;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Alessio on 16/05/2016.
 */

public class Meal implements Parcelable {

    private String meal_id;
    private String restaurant_id;
    private String meal_name;
    private double meal_price = 0.0;
    private int meal_cooking_time;
    private String meal_description;
    private String meal_category;
    private Boolean mealVegan = false;
    private Boolean mealVegetarian = false;
    private Boolean mealGlutenFree = false;
    private Boolean mealAvailable = false;
    private HashMap<String, MealAddition> meal_additions = new HashMap<>();
    private HashMap<String, MealCategory> meal_tags = new HashMap<>();
    private Boolean mealTakeAway;
    private String meal_thumbnail; //for meal preview with Glide in AsyncTask
    private String meal_photo_firebase_URL; //for enlarging image with Glide in AsyncTask
    private int meal_quantity; //for takeaway order quantity

    public Meal(){

    }

    public void addAddition(MealAddition addition) {
        meal_additions.put(addition.getMeal_addition_id(), addition);
    }

    public void addManyAdditions(ArrayList<MealAddition> list) {
        for(MealAddition add : list) {
            addAddition(add);
        }
    }

    public void remAddition(MealAddition addition) {
        meal_additions.remove(addition.getMeal_addition_id());
    }

    public ArrayList<MealAddition> allAdditions() {
        ArrayList<MealAddition> list = new ArrayList<>();
        list.addAll(meal_additions.values());
        return list;
    }

    public void addTag(MealCategory tag) {
        meal_tags.put(tag.getMeal_category_id(), tag);
    }

    public void addManyTags(ArrayList<MealCategory> list) {
        for(MealCategory tag : list) {
            addTag(tag);
        }
    }

    public void remTag(MealCategory tag) {
        meal_tags.remove(tag.getMeal_category_id());
    }

    public ArrayList<MealCategory> allTags() {
        ArrayList<MealCategory> list = new ArrayList<>();
        list.addAll(meal_tags.values());
        return list;
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

    public HashMap<String, MealAddition> getMeal_additions() {
        return meal_additions;
    }

    public void setMeal_additions(HashMap<String, MealAddition> meal_additions) {
        this.meal_additions = meal_additions;
    }

    public HashMap<String, MealCategory> getMeal_tags() {
        return meal_tags;
    }

    public void setMeal_tags(HashMap<String, MealCategory> meal_tags) {
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
        if (!restaurant_id.equals(meal.restaurant_id)) return false;
        if (!meal_name.equals(meal.meal_name)) return false;
        if (!meal_category.equals(meal.meal_category)) return false;
        if (!mealVegan.equals(meal.mealVegan)) return false;
        if (!mealVegetarian.equals(meal.mealVegetarian)) return false;
        if (!mealGlutenFree.equals(meal.mealGlutenFree)) return false;
        if (!mealAvailable.equals(meal.mealAvailable)) return false;
        if (!meal_additions.equals(meal.meal_additions)) return false;
        return meal_tags.equals(meal.meal_tags);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = restaurant_id.hashCode();
        result = 31 * result + meal_name.hashCode();
        temp = Double.doubleToLongBits(meal_price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + meal_category.hashCode();
        result = 31 * result + mealVegan.hashCode();
        result = 31 * result + mealVegetarian.hashCode();
        result = 31 * result + mealGlutenFree.hashCode();
        result = 31 * result + mealAvailable.hashCode();
        result = 31 * result + meal_additions.hashCode();
        result = 31 * result + meal_tags.hashCode();
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
        this.meal_additions = parcel.readHashMap(MealAddition.class.getClassLoader());
        this.meal_tags = parcel.readHashMap(MealCategory.class.getClassLoader());
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
        dest.writeInt(this.mealAvailable ? 1 : 0);
        dest.writeMap(this.meal_additions);
        dest.writeMap(this.meal_tags);
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
