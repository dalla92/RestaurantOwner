package it.polito.group2.restaurantowner.firebasedata;

/**
 * Created by Alessio on 16/05/2016.
 */
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Alessio on 16/05/2016.
 */
public class RestaurantPreview implements ClusterItem {
    public LatLng mPosition;
    public Double lat;
    public Double lon;

    private String restaurant_id; //to pass to the new Activity to open the right restaurant

    private String restaurant_cover_firebase_URL; //with Glide in AsyncTask
    private String restaurant_name;
    private float rating; //android:stepSize="0.01"
    private int reservations_number;
    private int tables_number;

    public RestaurantPreview(){

    }

    @Override
    public LatLng getPosition() {
        //return this.mPosition;
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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
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

    public LatLng getmPosition() {
        //TODO Decomment maybe after integration
        /*
        //get only latitude
        DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://have-break-9713d.firebaseio.com/restaurants/"+getRestaurant_id()+"/restaurant_latitude_position");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot res_latSnapshot : snapshot.getChildren()) {
                    Double snap_lat = (Double) res_latSnapshot.getValue();
                    lat = snap_lat;
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
        //get only longitude
        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://have-break-9713d.firebaseio.com/restaurants/"+getRestaurant_id()+"/restaurant_longitude_position");
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot res_longSnapshot : snapshot.getChildren()) {
                    Double snap_long = (Double) res_longSnapshot.getValue();
                    lon = snap_long;
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
        */
        mPosition = new LatLng(lat, lon);
        return mPosition;
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
}