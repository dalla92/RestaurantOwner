package it.polito.group2.restaurantowner.firebasedata;

/**
 * Created by Alessio on 16/05/2016.
 */
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Alessio on 16/05/2016.
 */
public class RestaurantPreview implements ClusterItem, Parcelable {

    public Double lat;
    public Double lon;
    private String restaurant_id; //to pass to the new Activity to open the right restaurant
    private String restaurant_cover_firebase_URL; //with Glide in AsyncTask
    private String restaurant_name;
    private int restaurant_price_range;
    private float restaurant_rating; //android:stepSize="0.01"
    private int reservations_number;
    private int tables_number;
    private String restaurant_category;
    private ArrayList<RestaurantTimeSlot> restaurant_time_slot;

    public RestaurantPreview(){

    }

    @Override
    public LatLng getPosition() {
        return new LatLng(getLat(), getLon());
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getRestaurant_cover_firebase_URL() {
        return restaurant_cover_firebase_URL;
    }

    public void setRestaurant_cover_firebase_URL(String restaurant_cover_firebase_URL) {
        this.restaurant_cover_firebase_URL = restaurant_cover_firebase_URL;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String restaurant_name) {
        this.restaurant_name = restaurant_name;
    }

    public float getRestaurant_rating() {
        return restaurant_rating;
    }

    public void setRestaurant_rating(float restaurant_rating) {
        this.restaurant_rating = restaurant_rating;
    }

    public int getReservations_number() {
        return reservations_number;
    }

    public void setReservations_number(int reservations_number) {
        this.reservations_number = reservations_number;
    }

    public int getTables_number() {
        return tables_number;
    }

    public void setTables_number(int tables_number) {
        this.tables_number = tables_number;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public int getRestaurant_price_range() {
        return restaurant_price_range;
    }

    public void setRestaurant_price_range(int restaurant_price_range) {
        this.restaurant_price_range = restaurant_price_range;
    }

    public String getRestaurant_category() {
        return restaurant_category;
    }

    public void setRestaurant_category(String restaurant_category) {
        this.restaurant_category = restaurant_category;
    }

    public ArrayList<RestaurantTimeSlot> getRestaurant_time_slot() {
        return restaurant_time_slot;
    }

    public void setRestaurant_time_slot(ArrayList<RestaurantTimeSlot> restaurant_time_slot) {
        this.restaurant_time_slot = restaurant_time_slot;
    }

    public boolean isOpenNow() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        Calendar openTime = Calendar.getInstance();
        Calendar closeTime = Calendar.getInstance();

        Calendar now = Calendar.getInstance();
        RestaurantTimeSlot tSlot = null;
        for(RestaurantTimeSlot s : restaurant_time_slot) {
            if(s.getDay_of_week() == now.get(Calendar.DAY_OF_WEEK)) {
                tSlot = s;
                break;
            }
        }
        assert tSlot != null;
        if(tSlot.getLunch()) {
            try {
                openTime.setTime(sdf.parse(tSlot.getOpen_lunch_time()));
                closeTime.setTime(sdf.parse(tSlot.getClose_lunch_time()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(openTime.before(now.getTime()) && closeTime.after(now.getTime()))
                return true;
        }
        if(tSlot.getDinner()) {
            try {
                openTime.setTime(sdf.parse(tSlot.getOpen_dinner_time()));
                closeTime.setTime(sdf.parse(tSlot.getClose_dinner_time()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(openTime.before(now.getTime()) && closeTime.after(now.getTime()))
                return true;
        }
        return false;
    }

    //Parcelable part
    public RestaurantPreview(Parcel in){
        String[] data = new String[9];

        in.readStringArray(data);
        this.lat = Double.valueOf(data[1]);
        this.lon = Double.valueOf(data[2]);
        this.restaurant_id = data[3];
        this.restaurant_cover_firebase_URL = data[4];
        this.restaurant_name = data[5];
        this.restaurant_rating = Float.valueOf(data[6]);
        this.reservations_number = Integer.valueOf(data[7]);
        this.tables_number = Integer.valueOf(data[8]);

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                String.valueOf(this.lat),
                String.valueOf(this.lon),
                this.restaurant_id,
                this.restaurant_cover_firebase_URL,
                this.restaurant_name,
                String.valueOf(this.restaurant_rating),
                String.valueOf(this.reservations_number),
                String.valueOf(this.tables_number)
                });
    }

    @Override
    public int describeContents(){
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public RestaurantPreview createFromParcel(Parcel in) {
            return new RestaurantPreview(in);
        }

        public RestaurantPreview[] newArray(int size) {
            return new RestaurantPreview[size];
        }
    };
}