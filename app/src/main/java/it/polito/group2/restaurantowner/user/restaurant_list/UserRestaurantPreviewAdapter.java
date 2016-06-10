package it.polito.group2.restaurantowner.user.restaurant_list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
import it.polito.group2.restaurantowner.firebasedata.RestaurantPreview;
import it.polito.group2.restaurantowner.firebasedata.RestaurantTimeSlot;
import it.polito.group2.restaurantowner.firebasedata.TableReservation;

/**
 * Created by Daniele on 05/04/2016.
 */
public class UserRestaurantPreviewAdapter extends RecyclerView.Adapter<UserRestaurantPreviewAdapter.ViewHolder> {
    private ArrayList<RestaurantPreview> mDataset;
    public static Context mContext;
    private static float PRICE_BOUNDARY_1 = 5;
    private static float PRICE_BOUNDARY_2 = 10;
    public static Marker mLastUserMarker;
    private static FirebaseDatabase firebase;
    public static int table_reservation_today_count = 0;
    public static int today_day;
    public static int today_month;
    public static int today_year;
    public static int total_tables_number;
    public static int index;

    // Provide a suitable constructor (depends on the kind of dataset)
    public UserRestaurantPreviewAdapter(ArrayList<RestaurantPreview> myDataset, Context myContext, Marker mLastUserMarker) {
        this.mLastUserMarker = mLastUserMarker;
        mDataset = myDataset;
        mContext = myContext;
        final Calendar today = Calendar.getInstance();
        today_day = today.get(Calendar.DAY_OF_MONTH);
        today_month = today.get(Calendar.MONTH);
        today_year = today.get(Calendar.YEAR);
    }

    public ArrayList<RestaurantPreview> getPreviews(){
        return this.mDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public UserRestaurantPreviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_restaurant_preview, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        RestaurantPreview current = mDataset.get(position);
        holder.setData(current, position);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void addItem(RestaurantPreview res) {
        mDataset.add(res);
        notifyItemInserted(mDataset.size() - 1);
    }

    public void clear() {
        mDataset = new ArrayList<>();
        notifyDataSetChanged();
    }

    public RestaurantPreview getItem(int position){
        return mDataset.get(position);
    }

    public boolean is_restaurant_near(LatLng res_position, double range) {
        double distance = calculate_distance2(res_position, mLastUserMarker);
        if (distance < range) {
            return true;
        } else
            return false;
    }

    public double calculate_distance2(LatLng a, Marker b) {
        double distance = SphericalUtil.computeDistanceBetween(a, b.getPosition());
        return distance;
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView image;
        public TextView resName;
        public TextView rating;
        public TextView reservationNumber;
        public TextView distance;
        public TextView price_range;
        public RestaurantPreview current;
        public ProgressBar mProgressBar;
        public int position;

        public ViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.imageViewRes);
            resName = (TextView) v.findViewById(R.id.textViewName);
            rating = (TextView) v.findViewById(R.id.textViewRating);
            reservationNumber = (TextView) v.findViewById(R.id.textViewReservationNumber);
            distance = (TextView) v.findViewById(R.id.textViewDistance);
            price_range = (TextView) v.findViewById(R.id.textViewPriceRange);
            mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        }

        public void setData(RestaurantPreview restaurant, int position) {

            mProgressBar.setVisibility(View.VISIBLE);
            if (restaurant.getRestaurant_cover_firebase_URL() == null || restaurant.getRestaurant_cover_firebase_URL().equals(""))
                Glide
                        .with(mContext)
                        .load(R.drawable.no_image)
                        .listener(new RequestListener<Integer, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
                                mProgressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                mProgressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(this.image);
            else
                Glide
                        .with(mContext)
                        .load(restaurant.getRestaurant_cover_firebase_URL())
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                mProgressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                mProgressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(this.image);

            /*
            SharedPreferences userDetails = mContext.getSharedPreferences("userdetails", mContext.MODE_PRIVATE);
            if(userDetails != null) {
                if (userDetails.getString(obj.getPhotoUri(), null) != null) {
                    Uri photouri = Uri.parse(userDetails.getString(obj.getPhotoUri(), null));
                    if (photouri != null)
                        this.image.setImageURI(photouri);
                }
            }*/

            switch (restaurant.getRestaurant_price_range()) {
                case 1:
                    this.price_range.setText("Average price: 5 euro");
                    break;
                case 2:
                    this.price_range.setText("Average price: 10 euro");
                    break;
                case 3:
                    this.price_range.setText("Average price: 15 euro");
                    break;
                case 4:
                    this.price_range.setText("Average price: 20 euro");
                    break;
            }
            this.price_range.setText("Average price: " + restaurant.getRestaurant_price_range());
            this.resName.setText(restaurant.getRestaurant_name());
            this.rating.setText(String.valueOf(restaurant.getRestaurant_rating()));

            count_bookings_today_and_display(restaurant.getRestaurant_id(), restaurant, reservationNumber);

            if (mLastUserMarker != null) {
                this.distance.setText(calculate_distance2(restaurant.getPosition(), mLastUserMarker));
            }

            this.position = position;
            this.current = restaurant;
        }

        public void count_bookings_today_and_display(String restaurant_id, RestaurantPreview r, TextView reservationNumber) {
            final TextView res_num_text_view = reservationNumber;
            firebase = FirebaseDatabase.getInstance();
            total_tables_number = r.getTables_number();
            DatabaseReference ref2 = firebase.getReferenceFromUrl("https://have-break-9713d.firebaseio.com/table_reservations/" + restaurant_id);
            ref2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        TableReservation snap_t_b = dataSnapshot.getValue(TableReservation.class);
                        Calendar that = snap_t_b.getTable_reservation_date();
                        int that_day = that.get(Calendar.DAY_OF_MONTH);
                        int that_month = that.get(Calendar.MONTH);
                        int that_year = that.get(Calendar.YEAR);
                        if (that_day == today_day && that_month == today_month && that_year == today_year) {
                            table_reservation_today_count++;
                            res_num_text_view.setText(table_reservation_today_count + "/" + total_tables_number);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });
        }


        public String calculate_distance2(LatLng a, Marker b) {
            double distance = SphericalUtil.computeDistanceBetween(a, b.getPosition());
            return formatNumber(distance);
        }

        private String formatNumber(double distance) {
            String unit = "m";
            if (distance < 1) {
                distance *= 1000;
                unit = "mm";
            } else if (distance > 1000) {
                distance /= 1000;
                unit = "km";
            }
            //return String.format("%4.3f%s", distance, unit);
            //trying to add space to split later
            return String.format("%4.3f %s", distance, unit);
        }
    }
}
