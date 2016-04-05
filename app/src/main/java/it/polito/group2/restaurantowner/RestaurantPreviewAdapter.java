package it.polito.group2.restaurantowner;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Daniele on 05/04/2016.
 */
public class RestaurantPreviewAdapter extends RecyclerView.Adapter<RestaurantPreviewAdapter.ViewHolder> {
    private List<Restaurant> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView image;
        public TextView resName;
        public TextView rating;
        public TextView reservationNumber;
        public TextView reservedPercentage;
        public Restaurant current;
        public int position;

        public ViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.imageViewRes);
            resName = (TextView) v.findViewById(R.id.textViewName);
            rating = (TextView) v.findViewById(R.id.textViewRating);
            reservationNumber = (TextView) v.findViewById(R.id.textViewReservationNumber);
            reservedPercentage = (TextView) v.findViewById(R.id.textViewReservedPercentage);
        }
        public void setData(Restaurant obj, int position){
            //TODO this.image.setImageBitmap();
            this.resName.setText(obj.getName());
            this.rating.setText(obj.getRating());
            this.reservationNumber.setText(obj.getReservationNumber());
            this.reservedPercentage.setText(obj.getReservedPercentage());
            this.position = position;
            this.current = obj;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RestaurantPreviewAdapter(List<Restaurant> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RestaurantPreviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_preview, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Restaurant current = mDataset.get(position);
        holder.setData(current,position);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
