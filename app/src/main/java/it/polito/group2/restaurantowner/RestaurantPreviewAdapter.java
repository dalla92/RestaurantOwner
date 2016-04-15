package it.polito.group2.restaurantowner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniele on 05/04/2016.
 */
public class RestaurantPreviewAdapter extends RecyclerView.Adapter<RestaurantPreviewAdapter.ViewHolder> implements ItemTouchHelperAdapter{
    private List<Restaurant> mDataset;
    private static Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder  {
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
           /* TODO if(obj.getPhotoUri()!="") {
                Uri imageUri = Uri.parse(obj.getPhotoUri());
                InputStream imageStream = null;
                try {
                    imageStream = mContext.getContentResolver().openInputStream(imageUri);
                    this.image.setImageBitmap(BitmapFactory.decodeStream(imageStream));
                } catch (FileNotFoundException e) {
                    // Handle the error
                } finally {
                    if (imageStream != null) {
                        try {
                            imageStream.close();
                        } catch (IOException e) {
                            // Ignore the exception
                        }
                    }
                }
            }
            */
            SharedPreferences userDetails = mContext.getSharedPreferences("userdetails", mContext.MODE_PRIVATE);
            if(userDetails != null) {
                if (userDetails.getString(obj.getRestaurantId(), null) != null) {
                    Uri photouri = Uri.parse(userDetails.getString(obj.getRestaurantId(), null));
                    if (photouri != null)
                        this.image.setImageURI(photouri);
                }
            }
            this.resName.setText(obj.getName());
            this.rating.setText(obj.getRating());
            this.reservationNumber.setText(obj.getReservationNumber());
            this.reservedPercentage.setText(obj.getReservedPercentage());
            this.position = position;
            this.current = obj;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RestaurantPreviewAdapter(List<Restaurant> myDataset, Context myContext) {
        mDataset = myDataset;
        mContext = myContext;
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
    public void addItem(int position, Restaurant res){
        mDataset.add(position,res);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, mDataset.size());
    }
    public void removeItem(int position){
        mDataset.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,mDataset.size());
    }


    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Restaurant prev = mDataset.remove(fromPosition);
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

                notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        alert.show();

    }

}
