package it.polito.group2.restaurantowner.owner;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.RestaurantPreview;
import it.polito.group2.restaurantowner.firebasedata.Review;
import it.polito.group2.restaurantowner.firebasedata.TableReservation;

/**
 * Created by Daniele on 05/04/2016.
 */
public class OwnerRestaurantPreviewAdapter extends RecyclerView.Adapter<OwnerRestaurantPreviewAdapter.ViewHolder> implements ItemTouchHelperAdapter{
    private List<RestaurantPreview> mDataset;
    private static Context mContext;
    private static FirebaseDatabase firebase;
    private static int total_tables_number = 0;
    private static int table_reservation_today_count = 0;
    private static int today_day;
    private static int today_month;
    private static int today_year;

    // Provide a suitable constructor (depends on the kind of dataset)
    public OwnerRestaurantPreviewAdapter(List<RestaurantPreview> myDataset, Context myContext) {
        mDataset = myDataset;
        mContext = myContext;
    }

 

        // Create new views (invoked by the layout manager)
        @Override
        public OwnerRestaurantPreviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.restaurant_preview, parent, false);
            // set the view's size, margins, paddings and layout parameters
            OwnerRestaurantPreviewAdapter.ViewHolder vh = new OwnerRestaurantPreviewAdapter.ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(OwnerRestaurantPreviewAdapter.ViewHolder holder, int position) {
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

        public void addItem(int position, RestaurantPreview res) {
            mDataset.add(position, res);
            notifyItemInserted(position);
            notifyItemRangeChanged(position, mDataset.size());
        }

        public void clear(){
            mDataset = new ArrayList<>();
            notifyDataSetChanged();
        }

        public void removeItem(int position) {
            final RestaurantPreview r = mDataset.get(position);
            final FirebaseDatabase firebase = FirebaseDatabase.getInstance();
            DatabaseReference resReference = firebase.getReference("restaurants/" + r.getRestaurant_id());
            DatabaseReference resPreviewReference = firebase.getReference("restaurants_previews/" + r.getRestaurant_id());
            DatabaseReference resNameRef = firebase.getReference("restaurant_names/" + r.getRestaurant_name() + "/" + r.getRestaurant_id());
            DatabaseReference resMealsReference = firebase.getReference("meals/" + r.getRestaurant_id());
            DatabaseReference resOffersReference = firebase.getReference("offers/" + r.getRestaurant_id());
            DatabaseReference resGalleriesReference = firebase.getReference("restaurants_galleries/" + r.getRestaurant_id());
            DatabaseReference resReviewsReference = firebase.getReference("reviews/" + r.getRestaurant_id());

            //TODO test
            DatabaseReference userRef = firebase.getReference("restaurants/" + r.getRestaurant_id() + "/favourite_users");
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                    HashMap<String,Boolean> hm = (HashMap<String,Boolean>)dataSnapshot.getValue();
                    if(hm!=null) {
                        for (String id : hm.keySet()) {
                            DatabaseReference favUserRef = firebase.getReference("users/" + id + "/favourites_restaurants/" + r.getRestaurant_id());
                            favUserRef.setValue(null);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w("Firebase error", "loadPost:onCancelled", databaseError.toException());
                    // ...
                }
            });

            DatabaseReference revRef = firebase.getReference("reviews/" + r.getRestaurant_id());

            resNameRef.setValue(null);
            revRef.setValue(null);
            resReference.setValue(null);
            resPreviewReference.setValue(null);
            resMealsReference.setValue(null);
            resOffersReference.setValue(null);
            resGalleriesReference.setValue(null);
            resReviewsReference.setValue(null);
            mDataset.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mDataset.size());
        }

    public int findRestPrev(RestaurantPreview m){
        int i = 0;
        for(;i<mDataset.size();i++){
            RestaurantPreview meal = mDataset.get(i);
            if(m.getRestaurant_id().equals(meal.getRestaurant_id()))
                return i;
        }
        return -1;
    }


        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            RestaurantPreview prev = mDataset.remove(fromPosition);
            mDataset.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemDismiss(final int position) {
            AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
            alert.setTitle("Confirmation!");
            alert.setMessage("Are you sure you want to delete the restaurant?\nThe operation cannot be undone!");
            alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    removeItem(position);
                    dialog.dismiss();

                }
            });
            alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    notifyItemChanged(position);
                    notifyDataSetChanged();
                    dialog.dismiss();
                }
            });

            alert.show();

        }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private ImageView image;
        private TextView resName;
        private TextView rating;
        private TextView reservationNumber;
        private TextView reservedPercentage;
        private ProgressBar progressBar;
        private RestaurantPreview current;
        private int position;

        public ViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.imageViewRes);
            resName = (TextView) v.findViewById(R.id.textViewName);
            rating = (TextView) v.findViewById(R.id.textViewRating);
            reservationNumber = (TextView) v.findViewById(R.id.textViewReservationNumber);
            reservedPercentage = (TextView) v.findViewById(R.id.textViewReservedPercentage);
            progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        }

        public void setData(RestaurantPreview obj, int position) {
            if (obj.getRestaurant_cover_firebase_URL() == null || obj.getRestaurant_cover_firebase_URL().equals("")) {
                Glide.with(mContext).load(R.drawable.no_image).into(this.image);
                progressBar.setVisibility(View.GONE);
            } else
                Glide
                        .with(mContext)
                        .load(obj.getRestaurant_cover_firebase_URL())
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(this.image);

            this.resName.setText(obj.getRestaurant_name());
            this.rating.setText(String.valueOf(obj.getRestaurant_rating()));
            if (obj.getTables_number() != 0)
                this.reservationNumber.setText(String.valueOf(obj.getTables_number()));
            else
                this.reservationNumber.setText("0");
            count_bookings_today_and_display(obj.getRestaurant_id(), obj, this.reservedPercentage);
            this.position = position;
            this.current = obj;
        }

        public void count_bookings_today_and_display(String restaurant_id, RestaurantPreview r, TextView reservationNumber) {
            final TextView res_num_text_view = reservationNumber;
            Calendar today = Calendar.getInstance();
            today_day = today.get(Calendar.DAY_OF_MONTH);
            today_month = today.get(Calendar.MONTH);
            today_year = today.get(Calendar.YEAR);
            firebase = FirebaseDatabase.getInstance();
            total_tables_number = r.getTables_number();
            res_num_text_view.setText(String.valueOf(total_tables_number));
            /*
            DatabaseReference ref2 = firebase.getReferenceFromUrl("https://have-break-9713d.firebaseio.com/table_reservations/");
            ref2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        TableReservation snap_t_b = (TableReservation) dataSnapshot.getValue();
                        Calendar that = Calendar.getInstance();
                        that.setTimeInMillis(snap_t_b.getTable_reservation_date());
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
            */
        }
    }

}
