package it.polito.group2.restaurantowner.firebasedata;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Alessio on 16/05/2016.
 */
public class RestaurantTimeSlot implements Parcelable{

    private Boolean lunch;
    private Boolean dinner; //is_open is not needed anymore because lunch and dinner are both false when close
    private int day_of_week;
    private String open_lunch_time;
    private String close_lunch_time;
    private String open_dinner_time;
    private String close_dinner_time;

    public RestaurantTimeSlot(){

    }


    public Boolean getLunch() {
        return lunch;
    }

    public void setLunch(Boolean lunch) {
        this.lunch = lunch;
    }

    public Boolean getDinner() {
        return dinner;
    }

    public void setDinner(Boolean dinner) {
        this.dinner = dinner;
    }

    public int getDay_of_week() {
        return day_of_week;
    }

    public void setDay_of_week(int day_of_week) {
        this.day_of_week = day_of_week;
    }

    public String getOpen_lunch_time() {
        return open_lunch_time;
    }

    public void setOpen_lunch_time(String open_lunch_time) {
        this.open_lunch_time = open_lunch_time;
    }

    public String getClose_lunch_time() {
        return close_lunch_time;
    }

    public void setClose_lunch_time(String close_lunch_time) {
        this.close_lunch_time = close_lunch_time;
    }

    public String getOpen_dinner_time() {
        return open_dinner_time;
    }

    public void setOpen_dinner_time(String open_dinner_time) {
        this.open_dinner_time = open_dinner_time;
    }

    public String getClose_dinner_time() {
        return close_dinner_time;
    }

    public void setClose_dinner_time(String close_dinner_time) {
        this.close_dinner_time = close_dinner_time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RestaurantTimeSlot that = (RestaurantTimeSlot) o;

        if (lunch != null ? !lunch.equals(that.lunch) : that.lunch != null) return false;
        if (dinner != null ? !dinner.equals(that.dinner) : that.dinner != null) return false;
        if (open_lunch_time != null ? !open_lunch_time.equals(that.open_lunch_time) : that.open_lunch_time != null)
            return false;
        if (close_lunch_time != null ? !close_lunch_time.equals(that.close_lunch_time) : that.close_lunch_time != null)
            return false;
        if (open_dinner_time != null ? !open_dinner_time.equals(that.open_dinner_time) : that.open_dinner_time != null)
            return false;
        if (close_dinner_time != null ? !close_dinner_time.equals(that.close_dinner_time) : that.close_dinner_time != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = lunch != null ? lunch.hashCode() : 0;
        result = 31 * result + (dinner != null ? dinner.hashCode() : 0);
        result = 31 * result + (open_lunch_time != null ? open_lunch_time.hashCode() : 0);
        result = 31 * result + (close_lunch_time != null ? close_lunch_time.hashCode() : 0);
        result = 31 * result + (open_dinner_time != null ? open_dinner_time.hashCode() : 0);
        result = 31 * result + (close_dinner_time != null ? close_dinner_time.hashCode() : 0);
        return result;
    }

    //Parcelable part
    public RestaurantTimeSlot(Parcel parcel){
        this.lunch = parcel.readInt() != 0;
        this.dinner = parcel.readInt() != 0;
        this.day_of_week = parcel.readInt();
        this.open_lunch_time = parcel.readString();
        this.close_lunch_time = parcel.readString();
        this.open_dinner_time = parcel.readString();
        this.close_dinner_time = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.lunch ? 1 : 0);
        dest.writeInt(this.dinner ? 1 : 0);
        dest.writeInt(this.day_of_week);
        dest.writeString(this.open_lunch_time);
        dest.writeString(this.close_lunch_time);
        dest.writeString(this.open_dinner_time);
        dest.writeString(this.close_dinner_time);
    }

    @Override
    public int describeContents(){
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public RestaurantTimeSlot createFromParcel(Parcel in) {
            return new RestaurantTimeSlot(in);
        }

        public RestaurantTimeSlot[] newArray(int size) {
            return new RestaurantTimeSlot[size];
        }
    };
}
