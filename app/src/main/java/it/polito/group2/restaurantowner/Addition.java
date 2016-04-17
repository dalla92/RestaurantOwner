package it.polito.group2.restaurantowner;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alessio on 08/04/2016.
 */
public class Addition implements android.os.Parcelable {

    //String code = null;
    String restaurant_id;
    String meal_id;
    String name = null;
    double price = 0.0;
    boolean selected = false;

    public Addition(String restaurant_id, String meal_id, String name, double price, boolean selected) {
        super();
        //this.code = code;
        this.restaurant_id = restaurant_id;
        this.meal_id = meal_id;
        this.name = name;
        this.price = price;
        this.selected = selected;
    }

    public Addition(){

    }

    //public String getCode() {
    //return code;
    //}
    //public void setCode(String code) {
    //this.code = code;
    //}
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getmeal_id() {
        return meal_id;
    }

    public void setmeal_id(String meal_id) {
        this.meal_id = meal_id;
    }




    protected Addition(Parcel in) {
        restaurant_id = in.readString();
        meal_id = in.readString();
        name = in.readString();
        price = in.readDouble();
        selected = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(restaurant_id);
        dest.writeString(meal_id);
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeByte((byte) (selected ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Addition> CREATOR = new Parcelable.Creator<Addition>() {
        @Override
        public Addition createFromParcel(Parcel in) {
            return new Addition(in);
        }

        @Override
        public Addition[] newArray(int size) {
            return new Addition[size];
        }
    };
}