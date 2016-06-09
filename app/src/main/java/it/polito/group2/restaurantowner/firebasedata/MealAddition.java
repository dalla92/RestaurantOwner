package it.polito.group2.restaurantowner.firebasedata;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Alessio on 16/05/2016.
 */
public class MealAddition implements Parcelable {

    private String meal_addition_id;
    private String meal_addition_name;
    private double meal_addition_price = 0.0;
    private Boolean additionSelected = false;

    public MealAddition(){

    }

    public String getMeal_addition_id() {
        return meal_addition_id;
    }

    public void setMeal_addition_id(String meal_addition_id) {
        this.meal_addition_id = meal_addition_id;
    }

    public String getMeal_addition_name() {
        return meal_addition_name;
    }

    public void setMeal_addition_name(String meal_addition_name) {
        this.meal_addition_name = meal_addition_name;
    }

    public double getMeal_addition_price() {
        return meal_addition_price;
    }

    public void setMeal_addition_price(double meal_addition_price) {
        this.meal_addition_price = meal_addition_price;
    }

    public Boolean getAdditionSelected() {
        return additionSelected;
    }

    public void setAdditionSelected(Boolean additionSelected) {
        this.additionSelected = additionSelected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MealAddition that = (MealAddition) o;

        if (Double.compare(that.meal_addition_price, meal_addition_price) != 0) return false;
        if (meal_addition_id != null ? !meal_addition_id.equals(that.meal_addition_id) : that.meal_addition_id != null)
            return false;
        if (meal_addition_name != null ? !meal_addition_name.equals(that.meal_addition_name) : that.meal_addition_name != null)
            return false;
        return !(additionSelected != null ? !additionSelected.equals(that.additionSelected) : that.additionSelected != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = meal_addition_id != null ? meal_addition_id.hashCode() : 0;
        result = 31 * result + (meal_addition_name != null ? meal_addition_name.hashCode() : 0);
        temp = Double.doubleToLongBits(meal_addition_price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (additionSelected != null ? additionSelected.hashCode() : 0);
        return result;
    }

    public MealAddition(Parcel parcel){
        this.meal_addition_id = parcel.readString();
        this.meal_addition_name = parcel.readString();
        this.meal_addition_price =parcel.readDouble();
        this.additionSelected =parcel.readInt() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.meal_addition_id);
        dest.writeString(this.meal_addition_name);
        dest.writeDouble(this.meal_addition_price);
        dest.writeInt(this.additionSelected ? 1 : 0);
    }

    @Override
    public int describeContents(){
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MealAddition createFromParcel(Parcel in) {
            return new MealAddition(in);
        }

        public MealAddition[] newArray(int size) {
            return new MealAddition[size];
        }
    };
}