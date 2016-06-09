package it.polito.group2.restaurantowner.firebasedata;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Alessio on 16/05/2016.
 */


public class MealCategory implements Parcelable {

    private String meal_category_id;
    private String meal_category_name;

    public MealCategory(){

    }

    public String getMeal_category_id() {
        return meal_category_id;
    }

    public void setMeal_category_id(String meal_category_id) {
        this.meal_category_id = meal_category_id;
    }

    public String getMeal_category_name() {
        return meal_category_name;
    }

    public void setMeal_category_name(String meal_category_name) {
        this.meal_category_name = meal_category_name;
    }

    public MealCategory(Parcel parcel){
        this.meal_category_id = parcel.readString();
        this.meal_category_name = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.meal_category_id);
        dest.writeString(this.meal_category_name);
    }

    @Override
    public int describeContents(){
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MealCategory createFromParcel(Parcel in) {
            return new MealCategory(in);
        }

        public MealCategory[] newArray(int size) {
            return new MealCategory[size];
        }
    };
}
